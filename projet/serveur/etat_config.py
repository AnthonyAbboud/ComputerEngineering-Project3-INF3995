# est-ce que les paramètres ont été passés par ligne de commande?
args_passed_by_cmd = False

# paramètres conservés
baudrate = None
connector_type = None
connector_file = None
server = None
rocket = None
map = None

# paramètres par défaut
default_baudrate = 921600
default_connector_type = "simulation"
default_connector_file_ser = "COM3"
default_connector_file_sim = "./simulation/valkyrie_ii.csv"
default_server = 3000
default_rocket = "11_valkyrieM2.xml"
default_map = "spaceport_america"

# lorsque les paramètres sont passés par ligne de commande, les extrait de l'objet "namespace" et les enregistre
def store_params(namespace):
	global baudrate
	global connector_type
	global connector_file
	global server
	global rocket
	global map
	global args_passed_by_cmd
	
	baudrate = namespace.baudrate
	connector_type = namespace.connector_type
	if namespace.connector_file == "*":
		connector_file = (default_connector_file_ser if connector_type == "serial" else default_connector_file_sim)
	else:
		connector_file = namespace.connector_file
	if namespace.server == None:
		server = default_server
	else:
		server = namespace.server
	rocket = namespace.rocket
	map = namespace.map
	
	args_passed_by_cmd = True