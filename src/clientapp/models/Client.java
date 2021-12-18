package clientapp.models;

import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {
    private static boolean connected;

    private static Socket socket;

    Client() {
        try {
            this.socket = new Socket("localhost", 4321); //Au lieu de localhost on peut: 127.0.0.1;
        } catch (Exception e) { // Par exemple quand on spécifie une mauvaise adresse ip.
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        // We are reading and showing a message.
        Client client = new Client();
        connected = true;
        client.start();
        // We are sending a message.
        try{
            PrintWriter os = new PrintWriter(socket.getOutputStream(), true);
            String input;
            Scanner scanner = new Scanner(System.in);  // Create a Scanner object
            while(connected) {
                input = scanner.nextLine();  // Read user
                System.out.println("salut je suis dans main");
                if (input.equals("exit")){
                    connected=false;
                    System.out.println("you have been loged out, have a nice day!");
                }
                // envoie vers le server:
                os.println(input);
            }
        } catch (IOException e) { // Server n'est pas démarré (pour cela il faut que le n° de port soit utilisé qualque part au niveau du server.)
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        try{
            //Socket s = new Socket("localhost", 4321); //Au lieu de localhost on peut: 127.0.0.1

            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String output;

            while(connected) {
                // On inverse les choses au niveau de Client/server.
                output = is.readLine();
                System.out.println("salut je suis dans run ");
                if (output.isEmpty()){
                    System.out.println("Server has stopped unexpectedly, you have thus been logged out, have a nice day !");
                    connected=false;
                }
                //output = String.valueOf(is.read()); // or :
                System.out.println(output);
            }
        } catch (IOException e) { // Server n'est pas démarré (pour cela il faut que le n° de port soit utilisé qualque part au niveau du server.)
            e.printStackTrace();
        }

    }
}
