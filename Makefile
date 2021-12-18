all:
	java -jar dist/clientapp.jar

client:
	javac -d bin -cp src/ src/clientapp/models/Client.java
	jar cfe dist/clientapp.jar clientapp/models/Client -C bin .
	java -jar dist/clientapp.jar

server:
	javac -d bin -cp src/ src/serverapp/MainServer.java
	jar cfe dist/serverapp.jar serverapp/MainServer -C bin .
	java -jar dist/serverapp.jar
