#! /usr/bin/env python3
import sys
import argparse
import cherrypy
import os
import os.path
import socket
import interface_rest
import etat_config
import flot_donnees
import log
import acces_comptes
import sync

def parse_args():
	if len(sys.argv) < 2:
		return
	parser = argparse.ArgumentParser(add_help=False, formatter_class=argparse.RawTextHelpFormatter, description="""Description:
  Programme serveur permettant d'avoir la communication avec
  une fusée d'Oronos et des PC/tablettes client.""",
									 epilog="""Copyright 2018. Oronos Polytechnique. Tous droits réservés""")
	parser._optionals.title = "Arguments optionnels"
	parser.add_argument("-h", "--help", action="help", default=argparse.SUPPRESS, help="""
Message d'aide et sortie immediate.""")
	parser.add_argument("-b", "--baudrate", type=int, metavar="BAUDRATE", default=etat_config.default_baudrate,
						help="""Baudrate du port série (connecteur sériel seulement).""")
	parser.add_argument("-c", "--connector_type", type=str, choices=["serial", "simulation"], default=etat_config.default_connector_type,
						help="""Source des données à traiter. (défaut: simulation)""")
	parser.add_argument("-f", "--connector_file", type=str,  metavar="CONNECTOR_FILE", default="*", help="""Argument pour le \033[1mtype\033[0m de connecteur c'est-à-dire le
port série COM si en mode serial ou le fichier de
données CSV pour le mode simulation)""")
	parser.add_argument("-s", "--server", type=int, nargs='?', metavar="PORT", default=etat_config.default_server, help="""Active un server pour avoir plusieurs stations au sol.
Si besoin, spécifiez un port (3000 par défaut)""")
	parser.add_argument("-r", "--rocket", type=str, metavar="ROCKET", default=etat_config.default_rocket, help="""Le nom du fichier XML. Ex:
10_polaris.xml. (Default: 11_valkyrieM2.xml)""")
	parser.add_argument("-m", "--map", type=str, metavar="MAP", default=etat_config.default_map, help="""Le nom de la carte qui est contenu dans
/Configs/Other/Maps.xml. Ex: motel_6.
(Default: spaceport_america)""")
	etat_config.store_params(parser.parse_args(sys.argv[1:]))

# the directory the main.py file is in
current_dir = os.path.dirname(os.path.abspath(__file__))

# configures cherypy :
# first part gets the server's ip, sets the port used for communication
# second part allows dispatch to REST methods
conf = {'global': {
			'server.socket_host': flot_donnees.get_ip(),
			'server.socket_port': 80,
			'log.screen': False,
		},
		'/': {
			'request.dispatch': cherrypy.dispatch.MethodDispatcher(),
		}
}
try:
	parse_args()
except:
	pass #on failure to parse args, args_passed_by_cmd will still be False and therefore the option selection GUI will be displayed
sync.confirm_cmd_args.release()
sync.confirm_gui_args.acquire()
flot_donnees.start()
cherrypy.quickstart(interface_rest.access(), '/', config=conf)