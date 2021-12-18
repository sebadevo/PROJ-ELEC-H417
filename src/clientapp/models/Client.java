package clientapp.models;

import java.io.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private static Socket socket;

    Client() {
        try {
            this.socket = new Socket("localhost", 4321); //Au lieu de localhost on peut: 127.0.0.1;
        } catch (Exception e) { // Par exemple quand on spécifie une mauvaise adresse ip.
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        Client client = new Client();
        client.start();
        try{
            //Socket s = new Socket("localhost", 4321); //Au lieu de localhost on peut: 127.0.0.1

            BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String output;

            while(true) {
                // On inverse les choses au niveau de Client/server.
                output = is.readLine();
                //output = String.valueOf(is.read()); // or : 
                System.out.println(output);
            }
        } catch (IOException e) { // Server n'est pas démarré (pour cela il faut que le n° de port soit utilisé qualque part au niveau du server.)
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            PrintWriter os = new PrintWriter(this.socket.getOutputStream(), true);
            String input;
            Scanner scanner = new Scanner(System.in);  // Create a Scanner object
            while(true) {
                    input = scanner.nextLine();  // Read user
                    // envoie vers le server:
                    os.println(input);     
            }
        } catch (IOException e) { // Server n'est pas démarré (pour cela il faut que le n° de port soit utilisé qualque part au niveau du server.)
            e.printStackTrace();
        }
    }
}
