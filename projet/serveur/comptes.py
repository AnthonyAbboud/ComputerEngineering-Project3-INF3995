# Date       : 22 / 02 / 2018
# Auteurs    : Equipe 09 - INF3995
# References :
# Note       :  gestionnaire en ligne de commande seulement



import sys
import argparse
import gestionComptes


# pretty self-explanatory
parser = argparse.ArgumentParser(add_help=True, formatter_class=argparse.RawTextHelpFormatter, description="""Description: Programme de  gestion des comptes utilisateurs""",  epilog="""Copyright 2018. Oronos Polytechnique. Tous droits rserves""")
parser._optionals.title = "Arguments optionnels"
parser.add_argument("-s", "--show", action='store_true', help="""affiche une liste des utilisateurs.""")
parser.add_argument("-a", "--add", nargs = 2, type=str, metavar=("nom", "mot_de_passe"), help="""mot de passe du nouvel utilisateur.""")
parser.add_argument("-r", "--remove", type = str, metavar="nom", help="""Nom de l'utilisateur a supprimer.""")
parser.add_argument("-m", "--modify",  nargs = 3,  type=str, metavar=("nom", "ancien_mot_de_passe", "nouveau_mot_de_passe"),	help="""Nom, ancien et nouveau mot de passe de l'utilisateur.""")
args = parser.parse_args(sys.argv[1:])


#print(args)


############
# voir gestionComptes pour implementation de ces fonctions
############

if args.show:
	gestionComptes.read_all_users()

if args.add:
	gestionComptes.add_user(args.add[0], args.add[1])

if args.remove:
	gestionComptes.remove_user(args.remove)

if args.modify:
	gestionComptes.edit_password(args.modify[0], args.modify[1], args.modify[2])