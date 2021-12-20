all:
	java -jar dist/clientapp.jar

client:
	javac -d bin -cp src/ src/clientapp/MainClient.java
	jar cfe dist/clientapp.jar clientapp.MainClient -C bin .
	java -cp src/ -jar dist/clientapp.jar

server:
	javac -d bin -cp src/ src/serverapp/MainServer.java
	jar cfe dist/serverapp.jar serverapp.MainServer -C bin .
	java -cp src/serverapp/ -jar dist/serverapp.jar

