'''
Created on Mar 1, 2018

@author: anthonyabboud
'''

#import for using the element of a tree --api from python
from xml.etree.ElementTree import ElementTree
from xml.etree.ElementTree import Element
import xml.etree.ElementTree as etree
import os

import gestionComptes
import GUI


#to have the root element from the tree created from the xml file
root = gestionComptes.root

# for GUI
display = GUI.instance


# global variable that  holds connected users info (name, ip, type)
connectedUsers = {}


# Fonction login: verifie si les informations sont valides. Si c'est le cas, on ajoute l'usager au dictionnaire des usagers connectes
def login(username, password, ip, type):
	# Pour chaque usager dans le fichier xml des usagers..
	for user in root.findall('user'):
		# Verifie si les parametres de la fonction (user/pass) sont valides
		if(username == user.find('id').text and gestionComptes.correct_password(user, password)):
			if username in connectedUsers: # and connectedUsers[username].ip == ip:
				return True

			# Si c'est le cas, on cree un user avec ces informations
			class User:
				pass # Classe avec aucune methode
			connectedUser = User()
			connectedUser.username = username
			connectedUser.ip = ip
			connectedUser.type = type
			# On insere ce user dans le dictionnaire globale avec le username comme etant la cle
			connectedUsers[username] = connectedUser
			# on update le display
			display.addUser(connectedUser)
			return True
	return False


# Fonction logout: supprime l'usager deconnecte du dictionnaire des usagers connectes
def logout(username, ip, type):
	# On applique un verification des autres parametres afin de s'assurer que c'est le bon usager qu'on va supprimer de la liste
	if  username in connectedUsers and connectedUsers[username].ip == ip:
		# Suppression
		del connectedUsers[username]
		# on update le display
		display.removeUser(username)
		return True
	return False

# function isConnected : verifie si l'usager est connecte, base sur son ip et type
def isConnected(ip, type):
	for _, user in connectedUsers.items():
		print(user)
		if user.ip == ip and user.type == type :
			return True;
	return False


"""
gestionComptes.remove_user("Luc")
gestionComptes.add_user("Luc", "hello")
print(login("Luc", "mdp", "127.0.0.1", "type"))
print(connectedUsers)
print(login("Luc", "hello", "127.0.0.1", "type"))
print(isConnected( "127.0.0.1", "type"))
print(connectedUsers)
#print(logout("Luc",  "127.0.0.1", "type"))
#print(connectedUsers)
"""
