import cherrypy
import simplejson
import etat_config
import acces_fichiers
import os
from acces_comptes import login, logout, isConnected
import re

def get_json_message(body):
	try:
		json = simplejson.loads(body)
	except:
		raise cherrypy.HTTPError(400, "Invalid JSON")
	return json

def http_code_from(success):
	if success:
		cherrypy.response.headers["Status"] = "200"
		return "OK"
	else:
		raise cherrypy.HTTPError(401, 'Unauthorized')

def prepare_list_of_files(list):
	res = {}
	for idx, elem in enumerate(list):
		res["file" + str(idx)] = elem
	return res

# extracts the device type from the user-agent string
android_regex = re.compile("android", re.IGNORECASE)
ios_regex = re.compile("iPad|iPhone|iPod")
def get_device():
	global android_regex
	global ios_regex
	user_agent = cherrypy.request.headers.get('User-Agent')
	if user_agent != None:
		if re.search(android_regex, user_agent) != None:
			return "Android"
		if re.search(ios_regex, user_agent) != None:
			return "iOS"
	return "unknown"

def check_if_logged_in():
	if not isConnected(cherrypy.request.remote.ip, get_device()):
		raise cherrypy.HTTPError(401, 'Unauthorized')

@cherrypy.expose
class access(object):
		
	@cherrypy.expose
	class users_t(object):
			
		@cherrypy.expose
		class login_t(object):
			
			def POST(self, user):
				json = get_json_message(user)
				return http_code_from(login(json["username"], json["password"], cherrypy.request.remote.ip, get_device()))
		
		login = login_t()
		
		@cherrypy.expose
		class logout_t(object):
			
			def POST(self, user):
				json = get_json_message(user)
				return http_code_from(logout(json["username"], cherrypy.request.remote.ip, get_device()))
		
		logout = logout_t()
	
	users = users_t()
	
	@cherrypy.expose
	class config_t(object):
		
		@cherrypy.expose
		class basic_t(object):
			
			@cherrypy.tools.accept(media='application/json')
			def GET(self):
				check_if_logged_in()
				return simplejson.dumps({
					"otherPort": etat_config.server,
					"layout": etat_config.rocket,
					"map": etat_config.map,
				})
		
		basic = basic_t()
		
		@cherrypy.popargs('name')
		@cherrypy.expose
		class rockets_t(object):
			
			@cherrypy.tools.accept(media='application/json')
			def GET(self, name=None):
				check_if_logged_in()
				if name == None:
					return simplejson.dumps(prepare_list_of_files(acces_fichiers.list_files("rockets")))
				else:
					return acces_fichiers.serve_file("rockets", name if name.endswith(".xml") else name + ".xml")
		
		rockets = rockets_t()
		
		@cherrypy.expose
		class map_t(object):
			@cherrypy.tools.accept(media='application/json')
			def GET(self):
				check_if_logged_in()
				return simplejson.dumps({
					"map": etat_config.map,
				})
		
		map = map_t()
		
		@cherrypy.popargs('name')
		@cherrypy.expose
		class miscFiles_t(object):
			
			@cherrypy.tools.accept(media='application/json')
			def GET(self, name=None):
				check_if_logged_in()
				if name == None:
					files = prepare_list_of_files(acces_fichiers.list_files("misc"))
					files["nFiles"] = len(files)
					return simplejson.dumps(files)
				else:
					return acces_fichiers.serve_file("misc", name if name.endswith(".pdf") else name + ".pdf")
		
		miscFiles = miscFiles_t()
	
	config = config_t()