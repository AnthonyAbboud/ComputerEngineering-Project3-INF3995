/*
*  J�r�me Collin - Test de base du ARM avec quelques p�riph�riques simples
*
*  Juillet 2015
*
*  Le code fait ceci:
*    - a chaque 2 secondes, le timer genere une interruption qui
*      fait afficher par RS232 une string au terminal
*    - Appuyer sur le bouton PB1 fait allumer la led LD9 juste � c�t� sur la carte
*    - Dans une boucle infinie, la valeur des 8 slide switches fait
*      allumer les 8 leds correspondantes au-dessus.
*    - Appuyer sur le bouton PB2 fait afficher (plusieurs fois...) une cha�ne de caract�res.
*
*  References utiles:
*  http://forums.xilinx.com/t5/Xcell-Daily-Blog/Implementing-the-Zynq-SoC-s-Private-Timer-Adam-Taylor-s-MicroZed/ba-p/402203
*  http://svenand.blogdrive.com/archive/174.html#.Vbgi4_lITap
*  http://forums.xilinx.com/t5/Xcell-Daily-Blog/Driving-the-Zynq-SoC-s-GPIO-Adam-Taylor-s-MicroZed-Chronicles/ba-p/389611
*  http://www.xilinx.com/support/documentation/xcell_articles/how-to-use-interrupts-on-zynqsoc.pdf
*  http://infocenter.arm.com/help/topic/com.arm.doc.ddi0407f/DDI0407F_cortex_a9_r2p2_mpcore_trm.pdf
*/

//ATTENTION: timer encore trop rapide.

#include <stdio.h>
#include "platform.h"
#include "xgpiops.h"
#include "xgpio.h"
#include "Xscutimer.h"
#include "xscugic.h"
#include "Xil_exception.h"

/* de loin le plus important de tous - � consulter! */
#include "xparameters.h"

/*
 * Boutons-poussoirs 1 et 2 accessibles par le MIO vers le PS
 * pour usage direct par le ARM.  Les 5 autres BTNU, BTND,
 * BTNC, BTNL et BTNR doivent passer par le PL pour etre utilises.
 */
#define PB1 50
#define PB2 51
#define LD9 7

#define IN_DIRECTION 0
#define OUT_DIRECTION 1

#define DISABLE_OUTPUT 0
#define ENABLE_OUTPUT 1

#define ALL_OUT 0
#define ALL_IN (~0u)

/*
 * Utile pour recharger le timer SCU de 32 bits
 * Normalement, le CPU est � 666 MHz.  Le timer
 * roule � la moiti� de cette valeur. La doc
 * officielle du ARM dit que le prescaler agit
 * de la fa�on suivante:
 * ( ( PRESCALAR + 1 ) * (LOAD_VALUE + 1 ) ) /  PERIPHCLOCK
 * Comme je n'ai pas employe de prescalar ici, la valeur 0x28000000
 * chargee fait que le timer devrait generer une interruption
 * au deux secondes.  A l'oeil, au terminal, c'est pas mal
 * ce que j'obtiens...  J'en conclus que c'est bon!
 *
 */
#define TIMER_LOAD_VALUE  0x28000000

/* variables globales pour les GPIO */
XGpioPs GpioMIO;
XGpio   GpioAXI;
int Status;
XGpioPs_Config *GPIO_MIOConfigPtr;
XGpio_Config   *GPIO_AXIConfigPtr;

/* variables globales pour le timer */
XScuTimer_Config *TMRConfigPtr;
XScuTimer Timer;

/* variables pour le gestionnaire d'interruptions GIC */
XScuGic InterruptController;
XScuGic_Config *GicConfigPtr;

/* configuration du GPIO � double-port en plus de celui du Zynq sur MIO */
int configGPIOs () {
  /* en premier, celui accessible par le MIO pour les PB1 et PB2 */
  GPIO_MIOConfigPtr = XGpioPs_LookupConfig(XPAR_PS7_GPIO_0_DEVICE_ID);
  Status = XGpioPs_CfgInitialize(&GpioMIO, GPIO_MIOConfigPtr, GPIO_MIOConfigPtr->BaseAddr);

  if (Status != XST_SUCCESS)
      return XST_FAILURE;

  XGpioPs_SetDirectionPin(&GpioMIO, PB1, IN_DIRECTION);
  XGpioPs_SetDirectionPin(&GpioMIO, PB2, IN_DIRECTION);
  XGpioPs_SetDirectionPin(&GpioMIO, LD9, OUT_DIRECTION);

  XGpioPs_SetOutputEnablePin(&GpioMIO, PB1, DISABLE_OUTPUT);
  XGpioPs_SetOutputEnablePin(&GpioMIO, PB2, DISABLE_OUTPUT);
  XGpioPs_SetOutputEnablePin(&GpioMIO, LD9, ENABLE_OUTPUT);

  /* en deuxieme, celui accessible du PL par le AXI */
  GPIO_AXIConfigPtr = XGpio_LookupConfig(XPAR_AXI_GPIO_0_DEVICE_ID);
  /* BaseAddress et non BaseAddr... Les deux API ne sont pas semblables! */
  Status = XGpio_CfgInitialize(&GpioAXI, GPIO_AXIConfigPtr, GPIO_AXIConfigPtr->BaseAddress);

  if (Status != XST_SUCCESS)
     return XST_FAILURE;

  XGpio_SetDataDirection(&GpioAXI, 1, ALL_OUT);
  XGpio_SetDataDirection(&GpioAXI, 2, ALL_IN);

  return XST_SUCCESS;
}

/* routine d'interruption */
void TimerIntrHandler(void *CallBackRef) {
   /*
    * juste pour compter de fa�on pas tres originale le
    * nombre de fois qu'on entre dans la routine...
    */
   static uint32_t nbr = 0;
   XScuTimer *TimerInstancePtr = (XScuTimer *) CallBackRef;
   xil_printf("===> Interruption du timer SCU: %d\n\r", nbr++);
   XScuTimer_ClearInterruptStatus(TimerInstancePtr);

}


/*
 *  ajustement de la minuterie "proche" du premier ARM
 *  et du gestionnaire d'interruption
 */
void setTimerAndIntr() {
  /* la base avec le timer */
  TMRConfigPtr = XScuTimer_LookupConfig(XPAR_XSCUTIMER_0_DEVICE_ID);
  XScuTimer_CfgInitialize(&Timer, TMRConfigPtr, TMRConfigPtr->BaseAddr);
  if (XScuTimer_SelfTest(&Timer) == XST_FAILURE) xil_printf("failure 1\n");

  /* la base avec le gestionnaire d'interruption */
  GicConfigPtr = XScuGic_LookupConfig(XPAR_SCUGIC_0_DEVICE_ID);
  XScuGic_CfgInitialize(&InterruptController, GicConfigPtr,
					    GicConfigPtr->CpuBaseAddress);
  if (XScuGic_SelfTest(&InterruptController) == XST_FAILURE) xil_printf("failure 2\n");

  Xil_ExceptionInit();
  Xil_ExceptionRegisterHandler(XIL_EXCEPTION_ID_IRQ_INT, TimerIntrHandler, &Timer);
  Xil_ExceptionEnable();

  /* connecter le timer au gestionnaire */
  if (XScuGic_Connect(&InterruptController, XPAR_SCUTIMER_INTR, TimerIntrHandler, &Timer) != XST_SUCCESS) xil_printf("failure 3\n");

  /* enable the interrupt for the Timer at GIC */
  XScuGic_Enable(&InterruptController, XPAR_SCUTIMER_INTR);

  /* on veut repartir le compteur automatiquement apres chaque interruption */
  XScuTimer_EnableAutoReload(&Timer);
  XScuTimer_EnableInterrupt(&Timer);

  /* Charger le timer et le partir */
  XScuTimer_LoadTimer(&Timer, TIMER_LOAD_VALUE);
  XScuTimer_Start(&Timer);
}


int main()
{
	int value;

    init_platform();
    configGPIOs();
    setTimerAndIntr();

    print("Debut du programme\n\r");

    while (1) {
       /* PB1: allumer la led LD9 selon la valeur de PB1 */
       value = XGpioPs_ReadPin(&GpioMIO, PB1);
       if (value == 1)
    	   XGpioPs_WritePin(&GpioMIO, LD9, 1);
       else
    	   XGpioPs_WritePin(&GpioMIO, LD9, 0);

       /* PB2: juste afficher une string, vraiment n'importe quoi comme code... */
       value = XGpioPs_ReadPin(&GpioMIO, PB2);
       if (value == 1) {
           value = XScuTimer_GetCounterValue(&Timer);
    	   xil_printf ("bouton PB2 enfonc� avec beaucoup de rebonds!!! Counter %X\n\r", value);
       }

  	   /* faie comme pour le tutoriel mais mais avec le ARM - Pas trop de changements... */
  	   value = XGpio_DiscreteRead (&GpioAXI, 2);
       XGpio_DiscreteWrite (&GpioAXI, 1, value);
    }

    print("Ne devrait jamais s'afficher\n\r");

    cleanup_platform();
    return 0;
}
