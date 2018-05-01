# Date       : 26 / 02 / 2018
# Auteurs    : Equipe 09 - INF3995
# References :		https://docs.python.org/2/howto/logging.html
#					https://www.saltycrane.com/blog/2008/06/how-to-get-current-date-and-time-in/
#					https://stackoverflow.com/questions/19479504/how-can-i-open-two-consoles-from-a-single-script
#					https://coderwall.com/p/_fg97w/creating-a-python-logger-to-stdout		
# Note       :	just needs to be imported before application start  in main; in other files just import logging and it'll be fine
#				also, cherrypy's option log.screen must be set to False so it doesn't print to original console

import logging
from datetime import datetime

import sys
import GUI
import os
import os.path

# creates the path ro logs if it doesn't exist
if not os.path.exists("./logs"):
	os.makedirs("./logs")

# a handler around the new console
outConsole = logging.StreamHandler(GUI.instance)
#outConsole = logging.StreamHandler(sys.stdout)

# we get the string representation of time in format "day-month-year_hourhminute"
now = datetime.now().strftime("%d-%m-%Y_%Hh%M")

# create the affiliated file
logFile = logging.FileHandler("./logs/" + now + ".log")

# each entry is in the format "day/month/year hour:minute loglevel : message"
logging.basicConfig(handlers = [outConsole, logFile], format='%(asctime)s %(levelname)s : %(message)s', datefmt='%d/%m/%Y %H:%M:%S', level=logging.DEBUG)

# to test
#logging.debug("I'm a stegosaurus!")
