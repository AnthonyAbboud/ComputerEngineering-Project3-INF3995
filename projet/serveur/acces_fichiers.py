from os import listdir, getcwd, makedirs
from os.path import isfile, join, exists
import cherrypy

# s'assure que les répertoires nécessaires existent
for dir in ["./misc", "./rockets", "./simulation"]:
	if not exists(dir):
		makedirs(dir)

# liste les fichiers d'un répertoire
def list_files(path):
	fullpath = join(getcwd(), path)
	return [file for file in listdir(path) if isfile(join(fullpath, file))]

# récupère le contenu d'un fichier
def serve_file(path, name):
	return cherrypy.lib.static.serve_file(join(getcwd(), path, name))

