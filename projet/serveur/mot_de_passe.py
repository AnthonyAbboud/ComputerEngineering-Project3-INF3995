# Date	   : 14 / 02 / 2018
# Auteurs	: Equipe 09 - INF3995
# References : https://openclassrooms.com/courses/apprenez-a-programmer-en-python/gestion-des-mots-de-passe
#			: https://docs.python.org/3/library/hashlib.html
#			: https://stackoverflow.com/questions/5293959/creating-a-salt-in-python
#			: https://docs.python.org/3/library/secrets.html
# Note	   : Il faudra discuter pour quèon sèentende sur le salt

import hashlib
#import secrets
import binascii
from random import randrange

hexarray = [''] * 16
hexarray[0]  = '0'
hexarray[1]  = '1'
hexarray[2]  = '2'
hexarray[3]  = '3'
hexarray[4]  = '4'
hexarray[5]  = '5'
hexarray[6]  = '6'
hexarray[7]  = '7'
hexarray[8]  = '8'
hexarray[9]  = '9'
hexarray[10] = 'a'
hexarray[11] = 'b'
hexarray[12] = 'c'
hexarray[13] = 'd'
hexarray[14] = 'e'
hexarray[15] = 'f'

class MotDePasse:
	"""
	Permet d'assigner une chaine de caractères comme mot de passe et de récupérer sa valeur chiffrée
	selon les spécifications du projet INF3995.
	
	le mot de passe, le sel et la valeur chiffrée sont des string en entrée comme en sortie
	
	"""

	def __init__(self, valeur = "", sel = None):
		
		if sel == None :
			global hexarray
			sel = ''
			i = 0
			while i < 48:
				sel = sel + hexarray[randrange(0, 16)]
				i = i + 1

			#sel =   #secrets.token_hex(24)
		
		# Chiffrer la chaine d'octets une première parmi 0xCAFE (51966 en décimal) fois
		chaineChiffree = hashlib.pbkdf2_hmac(hash_name='sha512', password=valeur.encode(), salt=sel.encode(), iterations=51966)

		# Mémoriser la valeur chiffrée dans un attribut en format portable
		self.valeur = binascii.hexlify(chaineChiffree).decode("utf-8")
		self.sel = sel
