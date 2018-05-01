import cherrypy
import os.path
import socket
import logging



def get_ip():
	sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	try:
		sock.connect(('111.111.111.111', 1))
		ip = sock.getsockname()[0]
	except:
		ip = '127.0.0.1'
	finally:
		sock.close()
	return ip

class HelloWorld(object):

	@cherrypy.expose
	def test1(self):
		logging.info('accessed test1')
		return "test 1 est bon..."
	
	@cherrypy.expose
	def test2(self):
		logging.info('accessed test2')
		return """<a href="http://www.polymtl.ca">Poly Mtl</a>"""
	
	@cherrypy.expose
	def test3(self):
		logging.info('accessed test3')
		return "<h1><img src='images/test.png'/> Bonjour !</h1>"
	
	def index(self):
		logging.info('accessed index')
		return "Hello World!"
	index.exposed = True

	
# the directory the server.py file is in
current_dir = os.path.dirname(os.path.abspath(__file__))


# configures cherypy :
# first line gets the server's ip, sets the port used for communication
# second line tags the ressources used staticaly (./images)
conf = {'global': {'server.socket_host': get_ip(), 'server.socket_port': 80},
        '/images': {'tools.staticdir.on': True,
                    'tools.staticdir.dir': os.path.join(current_dir, 'images')}}

# sets logs used by cherrypy to be ./access.log and ./error.log
cherrypy.config.update({
	'log.access_file': os.path.join(os.getcwd(), 'access.log'),
    'log.error_file': os.path.join(os.getcwd(), 'error.log')
})

# our log
logging.basicConfig(filename='serverTP3.log', level=logging.INFO)
logging.info('Started')
cherrypy.quickstart(HelloWorld(), '/', config=conf)
logging.info('Finished')


