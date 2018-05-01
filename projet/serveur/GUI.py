from tkinter import *
from threading import Thread
from datetime import datetime
import os.path
import os

import sync
import etat_config
import themes
import acces_fichiers

import cherrypy
import sys

# to display user and time since connection in real time
class ConnectionLabel(Label):
	def __init__(self, root, user):
		Label.__init__(self, root)
		self.user = user
		self.connectionTime = datetime.now()
		self.myUpdate()
	
	def myUpdate(self):
		elapsedTime = datetime.now() - self.connectionTime 
		self.config(text = self.user.username + " " * (25 - len(self.user.username)) + self.user.ip + " " * (25 - len(self.user.ip)) + self.user.type + "               " + str(elapsedTime).split(".")[0])
		self.after(1000, self.myUpdate)

preferences_path = "./preferences.txt"

# écrit un fichier preferences.txt avec des valeurs par défaut
def make_default_preferences():
	with open(preferences_path, "w") as f:
		f.write(themes.list[0] + "\n")
		f.write(etat_config.default_connector_type + "\n")
		f.write(etat_config.default_connector_file_ser + "\n")
		f.write(etat_config.default_connector_file_sim + "\n")
		f.write(str(etat_config.default_baudrate) + "\n")
		f.write(str(etat_config.default_server) + "\n")
		f.write(etat_config.default_rocket + "\n")
		f.write(etat_config.default_map + "\n")

# écrit des valeurs de préférence particulières dans preferences.txt
def set_preferences(*prefs):
	with open(preferences_path, "w") as f:
		for pref in prefs:
			f.write(pref + "\n")

# récupère les préférences de preferences.txt
def get_preferences():
	if not os.path.isfile(preferences_path):
		make_default_preferences()
	prefs = {}
	try_again = True
	while try_again:
		with open(preferences_path, "r") as f:
			def read_into(key):
				val = f.readline().strip()
				if len(val) == 0:
					raise Exception()
				prefs[key] = val
			try:
				read_into("theme")
				read_into("ctype")
				read_into("cfile_ser")
				read_into("cfile_sim")
				read_into("baud")
				read_into("server")
				read_into("rocket")
				read_into("map")
				try_again = False
			except:
				make_default_preferences()
	return prefs

# récupère seulement la préférence de couleur de preferences.txt
def get_theme_preference():
	if not os.path.isfile(preferences_path):
		return themes.list[0]
	with open(preferences_path, "r") as f:
		try:
			return f.readline().strip()
		except:
			return themes.list[0]

# fenêtre de base pour les options
def basic_option_window(parent):
	window = Toplevel(parent)
	window.title("Options - Groundstation Oronos")
	return window

# ajoute un champ pour changer le thème
def add_theme_field(window, row, default_value, *widgets):
	Label(window, text="GUI Theme").grid(row=row, column=0)
	theme = StringVar(window)
	theme.trace("w", lambda *x: themes.apply(theme.get(), *widgets))
	OptionMenu(window, theme, *themes.list).grid(row=row, column=1)
	theme.set(default_value)
	return theme

# apporte les changements finaux à une fenêtre d'options
def refine_option_window(window):
	for i in range(window.grid_size()[0]):
		window.grid_columnconfigure(i, pad=8)
	for i in range(window.grid_size()[1]):
		window.grid_rowconfigure(i, pad=16)
	window.grid_columnconfigure(0, minsize=128)
	window.update()
	# disposition de la fenètre sur l'écran
	window.geometry('+%d+%d' % (0, 0))

# fenêtre qui ne fait qu'afficher les options durant le fonctionnement du serveur
def option_display_window(parent, users, theme_used):
	window = basic_option_window(parent)
	
	Label(window, text=etat_config.connector_type).grid(row=0, column=0)
	Label(window, text=etat_config.connector_file).grid(row=0, column=1)
	
	row_off = 0
	if etat_config.connector_type == "serial":
		Label(window, text="Baudrate").grid(row=1, column=0)
		Label(window, text=str(etat_config.baudrate)).grid(row=1, column=1)
		row_off = 1
	
	Label(window, text="Data Port").grid(row=row_off + 1, column=0)
	Label(window, text=str(etat_config.server)).grid(row=row_off + 1, column=1)
	
	Label(window, text="Rocket Layout").grid(row=row_off + 2, column=0)
	Label(window, text=str(etat_config.rocket)).grid(row=row_off + 2, column=1)
	
	Label(window, text="GPS Map").grid(row=row_off + 3, column=0)
	Label(window, text=str(etat_config.map)).grid(row=row_off + 3, column=1)
	
	Button(window, text="Exit", command=lambda *x: os._exit(0)).grid(row=row_off + 5, column=1)
	
	add_theme_field(window, row_off + 4, theme_used, window, parent, users)
	
	refine_option_window(window)
	
	sync.confirm_gui_args.release()

# fenêtre pour sélectionner manuellement les options lorsqu'on part le serveur sans argument en ligne de commande
def option_select_window(parent, users):
	prefs = get_preferences()
	window = basic_option_window(parent)
	
	ctype_txt = StringVar(window)
	OptionMenu(window, ctype_txt, "serial", "simulation").grid(row=0, column=0)
	
	# champ de fichier pour le mode sériel
	cfile_ser_txt = StringVar(window)
	cfile_ser_txt.set(prefs["cfile_ser"])
	cfile_ser_widget = Entry(window, textvariable=cfile_ser_txt)
	cfile_ser_widget.grid(row=0, column=1)
	cfile_ser_widget.grid_remove()
	displaying_serial = False
	
	# champ de fichier pour le mode simulation
	cfile_sim_txt = StringVar(window)
	simfiles = acces_fichiers.list_files("simulation")
	if len(simfiles) == 0:
		cfile_sim_widget = Entry(window, textvariable=cfile_sim_txt)
	else:
		cfile_sim_widget = OptionMenu(window, cfile_sim_txt, *["./simulation/" + f for f in simfiles])
	cfile_sim_txt.set(prefs["cfile_sim"])
	cfile_sim_widget.grid(row=0, column=1)
	
	# change le champ de fichier quand on change le mode d'arrivée des données
	def switch_sim_ser(*x):
		nonlocal displaying_serial
		if ctype_txt.get() == "serial":
			cfile_ser_widget.grid()
			cfile_sim_widget.grid_remove()
			displaying_serial = True
		else:
			cfile_ser_widget.grid_remove()
			cfile_sim_widget.grid()
			displaying_serial = False
	ctype_txt.trace("w", switch_sim_ser)
	ctype_txt.set(prefs["ctype"])
	
	Label(window, text="Baudrate").grid(row=1, column=0)
	baudrate_txt = StringVar(window)
	Entry(window, textvariable=baudrate_txt).grid(row=1, column=1)
	baudrate_txt.set(prefs["baud"])
	
	Label(window, text="Data Port").grid(row=2, column=0)
	server_txt = StringVar(window)
	Entry(window, textvariable=server_txt).grid(row=2, column=1)
	server_txt.set(prefs["server"])
	
	Label(window, text="Rocket Layout").grid(row=3, column=0)
	rocket_txt = StringVar(window)
	rocket_files = acces_fichiers.list_files("rockets")
	# si le dossier "rockets" contient des .xml, on les affiche dans une liste pour convénience
	if len(rocket_files) == 0:
		Entry(window, textvariable=rocket_txt).grid(row=3, column=1)
	else:
		OptionMenu(window, rocket_txt, *rocket_files).grid(row=3, column=1)
	rocket_txt.set(prefs["rocket"])
	
	Label(window, text="GPS Map").grid(row=4, column=0)
	map_txt = StringVar(window)
	Entry(window, textvariable=map_txt).grid(row=4, column=1)
	map_txt.set(prefs["map"])
	
	keep_prefs_var = IntVar()
	Checkbutton(window, text="Keep Preferences", variable=keep_prefs_var).grid(row=6, column=1)
	
	theme_txt = None
	
	# lorsqu'on clique Start, enregistre les paramètres et part le serveur (change pour fenètre option_display_window)
	def on_start():
		try:
			etat_config.baudrate = int(baudrate_txt.get())
			etat_config.connector_type = ctype_txt.get()
			etat_config.connector_file = cfile_ser_txt.get() if displaying_serial else cfile_sim_txt.get()
			etat_config.server = int(server_txt.get())
			etat_config.rocket = rocket_txt.get()
			etat_config.map = map_txt.get()
			if keep_prefs_var.get() == 1:
				set_preferences(
					theme_txt.get(),
					ctype_txt.get(),
					stored_cfile_txt["serial"],
					stored_cfile_txt["simulation"],
					baudrate_txt.get(),
					server_txt.get(),
					rocket_txt.get(),
					map_txt.get()
				)
			option_display_window(parent, users, theme_txt.get())
			window.destroy()
		except:
			print("Invalid option values detected.")
			sys.stdout.flush()
	Button(window, text="Start", command=on_start).grid(row=7, column=1)
	
	theme_txt = add_theme_field(window, 5, prefs["theme"], window, parent, users)
	
	refine_option_window(window)
	
	

# the main app
# we have to do this since tkinter is bitchy about the call to Tk()
# and mainloop() is blocking
class GUI(Thread):
	def __init__(self):
		Thread.__init__(self)
		self.users = {}
	
	def run(self):
		root = Tk()
		root.title("log")
		
		# to display the log
		self.S = Scrollbar(root)
		self.T = Text(root, height=30, width=100)
		self.S.pack(side=RIGHT, fill=Y)
		self.T.pack(side=LEFT, fill=Y)
		self.S.config(command=self.T.yview)
		self.T.config(yscrollcommand=self.S.set)
		
		# to display the users
		self.userswindow = Toplevel(root)
		self.userswindow.title("users")
		self.userswindow.grid()
		
		sync.confirm_cmd_args.acquire()
		
		# to display the start window
		if etat_config.args_passed_by_cmd:
			option_display_window(root, self.userswindow, get_theme_preference());
		else:
			option_select_window(root, self.userswindow);
		
		# disposition de fenètres sur l'écran
		root.geometry('+%d+%d' % (root.winfo_screenwidth() - self.T.winfo_reqwidth(), 0))
		self.userswindow.geometry('+%d+%d' % (0, root.winfo_screenheight() / 2))
		
		root.mainloop()
	
	# for log
	def write(self,line):
		self.T.insert(END, line)
	
	# for user connection
	def addUser(self,user):
		label = ConnectionLabel(self.userswindow, user)
		label.grid()
		self.users[user.username] = label
		themes.apply(themes.last_applied, label)
	
	# for user deconnection
	def removeUser(self, username):
		user = self.users[username]
		user.destroy()
		del self.users[username]

instance = GUI()
instance.start()