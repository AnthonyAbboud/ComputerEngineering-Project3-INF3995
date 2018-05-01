import re
import cherrypy
import serial
from serial.tools import list_ports
import sys
import os

# essaie de se connecter à un port COM
check_for_com = re.compile("COM([1-9])")
def connect(com, baud):
	try:
		available = list_ports.comports()
		# si le port spécifié existe, on l'utilise
		if com in map(lambda x: x.device, list_ports.comports()):
			return serial.Serial(com, baud, timeout=None)
		# si le port n'existe pas, mais c'est un port COM_, on réessaie avec un port /dev/ttyS_ (pourrait être remplacé par /dev/ttyUSB_)
		m = check_for_com.match(com)
		if m:
			alternative = "/dev/ttyS" + str(int(m.group(1)) - 1)
			cherrypy.log(com + " doesn't exist; trying alternative port " + alternative + "...")
			return connect(alternative, baud)
		# si le port n'existe toujours pas, on ne peut pas continuer et donc on abandonne
		print("Failed to find port " + com + "; aborting now.")
		sys.stdout.flush()
		os._exit(1)
	except Exception as e:
		print("Received exception \"" + str(e) + "\"; aborting now.")
		sys.stdout.flush()
		os._exit(1)