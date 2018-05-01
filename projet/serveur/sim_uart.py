#! /usr/bin/env python3

# Note: was meant to do simulation over USB-UART, but was not needed in the end.

import serial
import simulation
import argparse
import sys

parser = argparse.ArgumentParser(add_help=True, formatter_class=argparse.RawTextHelpFormatter, description="""Description:
  Outil pour envoyer des données de simulation sur UART.""")
parser.add_argument("-f", "--file", type=str, metavar="FILE", default="./simulation/valkyrie_ii.csv",
					help="""Fichier csv source de la simulation ("./simulation/valkyrie_ii.csv" par défaut).""")
parser.add_argument("-p", "--port", type=str,  metavar="PORT", default="COM3", help="""Port COM utilisé (COM3 par défaut).""")
parser.add_argument("-b", "--baudrate", type=int, metavar="BAUDRATE", default=921600,
					help="""Baudrate du port série (921600 par défaut).""")
ns = parser.parse_args(sys.argv[1:])

ser = com_util.connect(ns.source, ns.baudrate)
for datum in simulation.play(ns.source):
	ser.write(datum)