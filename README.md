# PROJ-ELEC-H417
Project du cours de Network protocols de l'ULB

### Pour qu'un serveur puisse communiquer avec plusieurs clients en même temps:  
* Il faut que le serveur soit capable d'attendre une connexion à tout moment. 
  * Pour que le serveur puisse attendre une connection on fait appel à la méthode *accept()*
  * Cette méthode *accept()* doit être dans une boucle infinie. 
* A chaque fois qu'il y a une connexion il faut créer un nouveau thread associé à la socket du client connecté, puis attendre à nouveau une nouvelle connexion. 
* Le thread créé doit s'occuper des opérations d'entrées/sorties ( *read()/write()* ) pour communiquer avec le client indépendamment des autres activités du serveur.

### Fonctionnement d'un serveur. 

Pour démarrer il faut commencer par démarrer un objet *serveurSocket*. Ensuite on démarre un thread. Ce thread ve s'occuper de la communication clientgroupe1. 
Pour qu'un client puisse se connecter au serveur, il va créer une *Socket*. Qaund le client demande une connexion, l'objet *Socket* crée une Socket et un thread et ce thread va se charger de lire les entrées et sorties. 
Donc ce sera ce thread qui lira la requête et enverra la réponse vers le client. Pendant ce temps le premier thread attend toujours la connectiond un nouveau client. 
Donc si on a un autre client qui veut se connecter l'objet *Socket* va créer un socket et autre thread (qui gère entrées et sorties pour ce client). 

### Qu'est ce qu'un thread. 

Dans une machine on a un/plusieurs CPU (en fonction du type de machine)
Si on veut lancer plusieurs tâches en même temps pour une application on doit créer une notion de thread. 
On peut avoir plusieurs applications qui font cela en même temps. 
Tous les threads crées vont ariver dans une file d'attente dans le système, ils attendent de pouvoir utiliser le CPU. On a une opération d'ordonnacement que le système doit gérer. 
Chacun va accéder au CPU pour une petite durée et c'est comme ça qu'on a l'impression qu'on a plusieurs tâches qui s'exécutent en même temmps. 
Pour que le thread puisse entrer dans la file d'attente il faut le démarrer. 
Pour démarrer un thread on fait appel à la méthode *start()* qui exécute automatiquement la méthode *run()*. 


### Choix du prototcol 

On utilise telnet installé par défaut avec Windows, pour l'activer: 
* Panneau de configuration. 
* Programmes
* Fonctionnalités Windows
* Cocher case clientgroupe1 Telnet. 
 

Pour utiliser telnet : on doit entrer en premier l'adresse IP (localhost), ensuite le port.  


### Remarques 

* Ce qui fait la différence entre différents serveurs c'est le protocol. 


