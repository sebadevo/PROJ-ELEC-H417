# General project description.

Implement an application that enables private communication between 2 users (at least). 

# Project status
The project was brought to its end. Unfortunately because of the small-time left before the deadline
one of the necessary features could not be implemented: Save a conversation in the database between two 
users. We more time we could have implemented Group Chats (thus communication with multiple people at teh same time).


# Requirement to launch the program (how to run the code)

This project runs on JavaFX 1.8. Please download this version if you want the app to work on your device.
If you want to Compile yourself (.jar file are already prepared, read en section of requirements to see how to launch 
app): You need to compile java with javafx, the best method is to simply use Intellij IDEA. Other methods 
to compile seem to be more complicated. We tried making them with makefiles, but the compilation never included
the fxml files with the .class files, we thus had error. To overcome this problem we let Intellij IDEA take
care of the compilation and just enabled multiple instances of a given code to run. To do this left click on the application
you want to run in the top right corner next to the green arrow "run" (on its left) and select "edit information".
in the menu, for the client app only go on the same level as "build and run" there is a modify button. left click on it
a thick to allow multiple instances box. Once this done, apply the changes, and you can now run multiple instances 
of client in IntelIJ IDEA.

No compatibility issues. This project was tested on Ubuntu 20.04 and Windows 10+11

To Make it easier for a client to install the app, .jar files have been Created. They can be launched using the makefile
command "make server" to launch the server and "make client" to launch the client. They have to be launch in SEPARATE 
terminal. By default, the command line make launches the server.

# Main features 

- Possibility to launch a conversation with as many users (you can launch as many as you want) as you want. 
- The conversations are private as the messages are encrypted using the Diffie-Hellman procedure.
- As a User you can log out from the app and come back later on (log in) without having to register again as all 
your information (firstname, lastname, email, password, username) have been saved in the database. 
- An automatic check is run when you want to log in, you will not be able to  if you use wrong information (wrong username for instance)
- An already connected username can't log in if he is already logged in. 
- You can choose the recipient of your message. If the recipient is not connected, a security feature is built in to
prevent the exchange of key of a not connected user. There is a timer built in to wait 10 seconds for the key to be exchanged. 
If this time is exceeded then the recipient will be forgotten and the message not sent.
- To "kill" all the users we may just push the "RIP" button, appearing on the Server interface. This is equivalent to turn off the app and
disconnect all user currently connected.
- You can send messages to yourself (if you feel lonely), these messages will be encrypted following the same procedure.


### How a server works. 

To start we first creat a ServerSocket object. We then start a Thread which will take care off the reception of 
connection of all new client connected. For a client to connect to the server he will have to create a socket with the 
server port number. Once a client is connected to the server, a new thread and socket on the server will start just for the 
specific client to verify its login/register information. This enables the server to have multiple client connect at 
once. Once a client enter valid information, the thread taking care of the correct connection will be terminated and
a new one will be created in the conversation controller to take care of all the conversation this client may have with
other client or the server directly (ex : disconnect message, key exchange message, etc...).


### Protocol Choice 

We used th socket library provided by java. Since it is directly implemented in java it was much easier to implement
in our code and troubleshoot if any problem was encountered.
 
To use the sockets provided by java, 2 things to be considered:
- the server socket receives 1 argument, its port. ex : "4321"
- the client socket receives 2 arguments, the first is its address (to make it easier we always put localhost, and it 
will automatically assign an available local ip address.) and the second is the server port, so in this case : "4321".
  


