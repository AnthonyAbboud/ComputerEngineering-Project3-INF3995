themes = {}
last_applied = None
list = None

# retourne une couleur rgb dans le format que tkinter aime
def color(r, g, b):
	return "#%02x%02x%02x" % (r, g, b)

# applique un thème en appellant le callback correspondant dans "themes"
# conserve le nom du thème dans last_applied pour avoir un moyen de connaître le thème courant pour l'appliquer à de nouveaux widgets
def apply(name, *widgets):
	global themes
	global last_applied
	global list
	last_applied = name
	try:
		return themes[name](*widgets)
	except:
		return themes[list[0]](*widgets)

# enregistre dans le dict "themes" un callback qui fait l'application récursive sur les widgets des propriétés spécifiées pour le thème
def define(name, **settings):
	global themes
	def callback(*widgets):
		def theme_rec(cur_widget):
			for k, v in settings.items():
				try:
					cur_widget.config(**{k: v})
				except:
					pass
			for child in cur_widget.winfo_children():
				theme_rec(child)
		for widget in widgets:
			theme_rec(widget)
	themes[name] = callback
	return name

# définition des thèmes disponibles; "list" contient ensuite les noms des thèmes
list = [
	define("dark",
		background = "Black",
		activebackground = color(63, 63, 63),
		foreground = "White",
		activeforeground = "White",
		highlightbackground = color(31, 31, 31),
		selectcolor = "Black",
		insertbackground = "White",
	),
	define("bright",
		background = "White",
		activebackground = color(191, 191, 191),
		foreground = "Black",
		activeforeground = "Black",
		highlightbackground = color(223, 223, 223),
		selectcolor = "White",
		insertbackground = "Black",
	),
]