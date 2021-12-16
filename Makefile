all:
	javac -d bin -cp ClientApp/src/ ClientApp/src/Client/models/Client.java
	jar cfe dist/client.jar Client -C bin .
	java -jar dist/client.jar

client:
	javac -d bin -cp ClientApp/src/ ClientApp/src/clientgroupe1/models/Client.java
	jar cfe dist/clientapp.jar clientgroupe1/models/Client -C bin .
	java -jar dist/clientapp.jar

server:
	javac -d bin -cp ServerApp/src/ ServerApp/src/servergroupe1/MainServer.java
	jar cfe dist/serverapp.jar servergroupe1/MainServer -C bin .
	java -jar dist/serverapp.jar
