from threading import Lock

# file circulaire thread-safe; garde 2^N données et associe à chacune un "numéro de séquence" croissant partant de 0
class circular_buffer(object):
	def __init__(self, logsz):
		self.seq_num = 0
		self.tail = 0
		self.size = 0
		self.capacity = 2**logsz
		self.mask = ~((~0) << logsz)
		self.buf = [None] * self.capacity
		self.lock = Lock()
	# ajoute un élément à la fois à l'arrière de la file; écrase la plus ancienne valeur lorsque la taille atteint la capacité
	def push(self, elem):
		with self.lock:
			self.seq_num = self.seq_num + 1
			self.buf[(self.tail + self.size) & self.mask] = elem
			if self.size == self.capacity:
				self.tail = (self.tail + 1) & self.mask
			else:
				self.size = self.size + 1
	# récupère un bloc de nn données (ou moins si nn > size) partant du "numéro de séquence" seq_num (ou plus loin si la donnée de seq_num a déjà été écrasée)
	# jusqu'à l'avant de la file; retourne aussi le "numéro de séquence" de la prochaine donnée qui sera insérée
	def read(self, nn, start_pos):
		with self.lock:
			n = min(nn, self.seq_num - start_pos)
			n = self.size if n > self.size else n
			end = self.tail + self.size
			beg = end - n
			if (beg < self.capacity) == (end < self.capacity):
				return self.buf[beg & self.mask:end & self.mask], self.seq_num
			else:
				return self.buf[beg:] + self.buf[:end & self.mask], self.seq_num