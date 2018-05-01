'''
Created on Feb 22, 2018

@author: khalilbennani
'''
	
#import for using the element of a tree --api from python
import xml.etree.ElementTree as etree
from mot_de_passe import MotDePasse as MDP

#creating a tree from the xml file
tree = etree.parse('users.xml')
#to have the root element from the tree created from the xml file
root = tree.getroot()

#function to read all users and their password
def read_all_users():
	for user in root.findall('user'):
		print (user.find('id').text)
		#print(user.find('password').text)
	return


#remove a user
def remove_user(userToRemove):
	for user in root.findall('user'):
		if userToRemove ==  user.find('id').text:
			root.remove(user)
			tree.write('users.xml')
	return


#add new user
def add_user(idUser, passwordUser):
	#creating the sub element
	newUser = etree.SubElement(root,"user")
	id = etree.SubElement(newUser,"id")
	password = etree.SubElement(newUser,"password")
	salt = etree.SubElement(newUser,"salt")
	#editing the sub element of the tree
	id.text = idUser
	generatedMDP = MDP(passwordUser)
	password.text = generatedMDP.valeur
	salt.text= generatedMDP.sel
	tree.write('users.xml') 
	return  
	
	
#edit password
def edit_password(userX, oldPassword, newPassword):
	for user in root.findall('user'):
		if(userX ==  user.find('id').text and correct_password(user, oldPassword)):
			generatedMDP = MDP(newPassword)
			user.find('password').text = generatedMDP.valeur
			user.find('salt').text = generatedMDP.sel
			user.find('password').set('updated', 'yes')
			tree.write('users.xml')
	return


def correct_password(user, inputPassword):
	hashedMDP = MDP(inputPassword, user.find('salt').text)
	return hashedMDP.valeur ==  user.find('password').text


#retour if succes
#ajouter buuton save info login

#For testing 

#add_user("reph","1234567")

#remove_user("reph")

#edit_password("reph", "sdssdsdd", "1")



