import encodage
import time
import cherrypy

# lit les lignes d'un log de fusée .csv une par une et génère pour chaque ligne une donnée binaire encodée en base64
def play(filename):
	start_time = time.clock()
	with open(filename) as f:
		f.readline() #saute header
		line = f.readline()
		while line:
			encoded, sent_time = encodage.encode(line)
			abs_sent_time = start_time + sent_time
			#utilise une boucle pour attendre, car time.sleep fonctionne vraiment mal sur les ordis de l'école,
			#probablement car ces derniers sont bloatés avec un tas de processus d'arrière-plan
			while time.clock() < abs_sent_time:
				pass
			yield encoded
			line = f.readline()