from socket import *
import threading
import time
import etat_config
from tampon_circulaire import circular_buffer
import cherrypy
import random
import etat_config
import simulation
import com_util
import logging

# adresse ip locale
def get_ip():
	sock = socket(AF_INET, SOCK_DGRAM)
	try:
		sock.connect(('111.111.111.111', 1))
		ip = sock.getsockname()[0]
	except:
		ip = '127.0.0.1'
	finally:
		sock.close()
	return ip

#selon le chargé: réception à ~300 données/sec
#en moyenne dans les deux fichiers fournis: ~564 données/sec
#semble pouvoir monter au moins à ~1000 données/sec

#envoyé par le pcb: une donnée (24-bits) + 1 newline, répété le plus rapidement possible

# réception des données d'un vrai port COM
class receiver_t(threading.Thread):
	def __init__(self, transfer):
		threading.Thread.__init__(self, daemon=True)
		self.ser = com_util.connect(etat_config.connector_file, etat_config.baudrate)
		self.transfer = transfer
		super().start()
	
	def __del__(self):
		if hasattr(self, 'ser'):
			self.ser.close()
	
	# lecture d'une donnée à la fois, avec mécanisme pour rejetter les données de taille incorrecte 
	def read_data(self):
		n_read = 25
		# readline et readlines se comportent de manière un peu bizzare (et ne sont même pas détaillées dans la documentation de l'API de pySerial!),
		# donc je fais la lecture des messages (assume 24 octets + newline) manuellement, et je gère les messages de mauvaise longueur.
		while True:
			buf = self.ser.read(25)
			buf_term = buf[24]
			newline = b'\n'[0]
			# le dernier octet n'est pas un '\n' => donnée de taille incorrecte => avance jusqu'à la prochaine donnée de bonne taille
			while buf_term != newline:
				newline_pos = buf.find(b'\n')
				if newline_pos != -1:
					buf = buf[newline_pos + 1:] + self.ser.read(newline_pos + 1)
				else:
					buf = self.ser.read(25)
				buf_term = buf[24]
			yield buf[0:24]
	
	def run(self):
		# place les données une à une dans une file circulaire
		for datum in self.read_data():
			self.transfer.push(datum)

receiver = None

# réception des données simulée à partir d'un log .csv
class simulated_receiver_t(threading.Thread):
	def __init__(self, transfer):
		threading.Thread.__init__(self, daemon=True)
		self.transfer = transfer
		super().start()
	def run(self):
		# place les données une à une dans une file circulaire
		for datum in simulation.play(etat_config.connector_file):
			self.transfer.push(datum)

simulated_receiver = None

# réponse au client pour l'envoi des données CAN
class sender_t(threading.Thread):
	def __init__(self, transfer):
		threading.Thread.__init__(self, daemon=True)
		self.sock = socket(AF_INET, SOCK_DGRAM)
		self.sock.bind((get_ip(), etat_config.server))
		self.transfer = transfer
		super().start()
	
	def __del__(self):
		self.sock.close()
		threading.Thread.__del__(self)
	
	# communique avec le client selon le protocole spécifié dans ../communication.txt
	def run(self):
		# limite sur le nombre de données car il y a une limite sur le nombre d'octets qu'on peut envoyer dans un paquet UDP
		n_CANs_sent_ceiling = 2048
		if n_CANs_sent_ceiling * 25 + 64 >= 65536:
			raise Exception("The value of n_CANs_sent_ceiling must be such that the number of bytes sent in one UDP packet is under 65536.")
		while True:
			# recoit et lit une requête du client
			data, addr = self.sock.recvfrom(64)
			try:
				lines = data.strip().split(b'\n')
				if len(lines) != 2:
					raise Exception()
				seq_num_start = int(lines[0].decode('UTF-8'))
				n_to_send = int(lines[1].decode('UTF-8'))
				if seq_num_start < 0 or n_to_send < 0:
					raise Exception()
			except:
				self.sock.sendto(b"FAIL", addr)
			else:
				# récupère le nombre de données approprié de la file circulaire
				data_to_send, seq_num_end = self.transfer.read(n_to_send if n_to_send < n_CANs_sent_ceiling else n_CANs_sent_ceiling - 1, seq_num_start)
				# envoi des données au client
				self.sock.sendto(bytes("OK\n{}\n{}\n".format(seq_num_end, len(data_to_send)), 'UTF-8') + b'\n'.join(data_to_send) + b'\n', addr)

sender = None

# pour tester localement l'intéraction avec un client; voir le code de la tablette pour le vrai client
class test_client_t(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self, daemon=True)
		self.sock = socket(AF_INET, SOCK_DGRAM)
		super().start()
	
	def __del__(self):
		self.sock.close()
		threading.Thread.__del__(self)
	
	def run(self):
		server_addr = get_ip()
		seq_num = 0
		n_to_get = 2048
		with open("test_client_recv.txt", "w") as f:
			while True:
				time.sleep(1)
				self.sock.sendto(bytes("{}\n{}".format(seq_num, n_to_get), 'UTF-8'), (server_addr, etat_config.server))
				cherrypy.log("Asked for {} data, from sequence number {}.".format(n_to_get, seq_num))
				data = self.sock.recv(n_to_get * 25 + 64)
				lines = data.strip().split(b'\n')
				if len(lines) > 1:
					seq_num = int(lines[1].decode('UTF-8'))
					cherrypy.log("Received {}!".format(lines[2].decode('UTF-8')))
				else:
					cherrypy.log("FAIL!!!!!!!\n")
				f.write(data.decode('UTF-8') + "\n")
				f.flush()

test_client = None

# initialisation des structures et threads
def start():
	transfer = circular_buffer(12) #garde 4096 données, mais ne permettra d'en envoyer que moins de 2048 à la fois (~50000 octets, en-dessous de la limite de 2^16 octets pour le contenu d'un message UDP)
	global sender
	sender = sender_t(transfer)
	global receiver
	receiver = receiver_t(transfer) if etat_config.connector_type == "serial" else simulated_receiver_t(transfer)
	#global test_client
	#test_client = test_client_t()
	