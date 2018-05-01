INF3995 - TP3 - README
-------------------------------------------------
Pr�paration du serveur

1. Rendre inactif le Network Manager
	- Ouvrir le fichier /etc/NetworkManager/NetworkManager.conf
	- S'assurer que la variable "managed" est � "false".

2. Configuration des param�tres r�seau importants
	- Ouvrir le fichier /etc/network/interfaces
	- Modifier les informations du r�seau comme suit:
		auto eth0
		iface eth0 inet static
		address 132.207.89.[90-99] (au choix)
		netmask 255.255.255.128
		gateway 132.207.89.1

3. Configuration pour acc�der aux serveurs DNS
	- Ouvrir le fichier /etc/resolv.conf
	- Ajouter les deux lignes suivantes:
		nameserver 8.8.8.8
		nameserver 8.8.4.4

4. Repartir Linux

5. Utiliser la commande "ifconfig -a" pour  v�rifier si la configuration r�seau est la bonne pour eth0

6. Configurer un service FTP
	- Installer le "daemon" vsftpd avec la commande "sudo apt install vsftpd"
	- Ouvrir le fichier de configuration /etc/vsftpd.conf
	- D�commenter la ligne "write_enable=YES" et "local_enable=YES"
	- Cr�er un utilisateur FTP que nous utilisons pour faire un transfert de fichier: username: ftp_user, pass: ftp
	- Repartir le service vsftpd avec "sudo restart vsftpd"
	- Ouvrir FileZilla, s'authentifier avec les bonnes informations (username, pass et addresse IP du serveur), et transf�rer les fichiers

7. Installer pip avec "sudo apt-get install -y python3-pip"

8. Installer CherryPy avec "pip install cherrypy"
------------------------------------------------------

En utilisant nos fichiers, le serveur peut ainsi �tre d�marr� et fonctionnel

------------------------------------------------------
Ex�cution des tests

1. Ouvrir un Web Browser

2. Aller aux addresses suivantes pour les 3 tests:
	- localhost:132.207.89.[90-99]/test1
	- localhost:132.207.89.[90-99]/test2
	- localhost:132.207.89.[90-99]/test3