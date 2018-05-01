from threading import Semaphore

# sémaphores pour synchroniser le thread principal et le thread du GUI au démarrage (pour l'enregistrement des paramètres de départ)
confirm_cmd_args = Semaphore(0)
confirm_gui_args = Semaphore(0)