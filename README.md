# General project description.

Implement an application that enables private communication between 2 users (at least). 

# Project status
The project was brought to its end. Unfortunately because of the small time left before the deadline
one of the necessary features could not be implemented: Save a conversation in the database between two 
users. 

# Requirement to launch the program (how to run the code)

You need to compile java with javafx, the best method is to simply use Intellij IDEA. Other methods 
to compile seem to be useless. 

No compatibility issues. 

# Main features 

- Possibility to launch a conversation with as many users (you can launch as many as you want) as you want. 
- The conversations are private as the messages are encrypted using the Diffie-Hellman procedure.
- As a User you can log out from the app and come back later on (log in) without having to register again as all
your information (firstname, lastname, email, password, username) have been saved in the database. 
- An automatic check is run when you want to log in, you will not be able to  if you use wrong information (wrong username for instance)
- An already connected username can't log in if he is already logged in. 
- You can choose the recipient of your message. 
- To "kill" all the users we may just push the "RIP" button, appearing on the Server interface. This is equivalent to turn off the app. 
- You can send messages to yourself (if you feel lonely), these messages will be encrypted following the same procedure.


### Fonctionnement d'un serveur. 

Pour démarrer il faut commencer par démarrer un objet *serveurSocket*. Ensuite on démarre un thread. Ce thread ve s'occuper de la communication serverapp.clientgroupe1. 
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
* Cocher case serverapp.clientgroupe1 Telnet. 
 

Pour utiliser telnet : on doit entrer en premier l'adresse IP (localhost), ensuite le port.  


### Remarques 

* Ce qui fait la différence entre différents serveurs c'est le protocol. 


