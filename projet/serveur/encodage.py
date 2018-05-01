import binascii
import struct
import base64

from CANSid import CANDataType, CANSid, CANMsgDataTypes

# reprend l'enum spécifiée dans l'énoncé
module_type = {
	"ADM" : 0,
	"ADIRM" : 3,
	"DLM" : 4,
	"PUM" : 6,
	"NUC" : 7,
	"GS" : 7,
	"MCD" : 15,
	"AGRUM" : 16,
	"ADRMSAT" : 17,
	"ATM_MASTER" : 18,
	"ATM_SLAVE" : 19,
	"UNKNOWN_MODULE" : 0x1E,
	"ALL_MODULES" : 0x1F,
	"IOM" : 7 #valeur indiquée par le chargé
}

def serial_value(x):
	return 0xF if x == "ALL_SERIAL_NBS" else int(x)

# prend une valeur en texte et utilise le CANDataType correspondant pour la convertir en format binaire
def can_data_text_to_bytes(text, type):
	fmt, val = {
		CANDataType.UNKNOWN : lambda t: ('I', 0),
		CANDataType.INT : lambda t: ('i', int(t)),
		CANDataType.FLOAT : lambda t: ('f', float(t)),
		CANDataType.UNSIGNED : lambda t: ('I', int(t)),
		CANDataType.TIMESTAMP : lambda t: ('I', int(t)),
		CANDataType.MAGIC : lambda t: ('I', int(t)),
		CANDataType.NONE : lambda t: ('I', 0)
	}[type](text)
	return struct.pack(fmt, val)

# prend une ligne d'un log .csv et la convertit dans le format binaire demandé, encodé en base64 
def encode(csv_log_line):
	# extrait chaque champ
	fields = csv_log_line.split(';')
	time = float(fields[0])
	# valeurs masquées pour garantir qu'elles vont chacune rentrer dans le nombre de bits spécifié
	src_type = module_type[fields[2]] & ~(~0 << 5)
	src_serial = serial_value(fields[3]) & ~(~0 << 4)
	dst_type = module_type[fields[4]] & ~(~0 << 5)
	dst_serial = serial_value(fields[5]) & ~(~0 << 4)
	# utilise les enums de CANSid pour savoir comment interpréter les deux données
	can_msgid = CANSid[fields[6]]
	can_msgid_val = can_msgid.value & ~(~0 << 11)
	types = CANMsgDataTypes[can_msgid]
	data1 = can_data_text_to_bytes(fields[7], types[0])
	data2 = can_data_text_to_bytes(fields[8], types[1])
	# paquetage des valeurs + CRC32 dans la struct, encodage de la struct en base64
	bits_0_32 = struct.pack('I', can_msgid_val | (dst_serial << 11) | (dst_type << 15) | (src_serial << 20) | (src_type << 24))
	first_part = bits_0_32 + data1 + data2
	return base64.b64encode(first_part + struct.pack('I', binascii.crc32(first_part))), time
	