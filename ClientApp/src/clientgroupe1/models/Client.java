package clientgroupe1.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        try{
            Socket s = new Socket("localhost", 4321); //Au lieu de localhost on peut: 127.0.0.1
            InputStream is = s.getInputStream(); // Lit des octets.
            OutputStream os = s.getOutputStream(); // Envoie des octets.

            // pour lire une info clavier
            String input;
            String output;
            Scanner scanner = new Scanner(System.in);  // Create a Scanner object

            while(true) {
                System.out.println("Je suis un client");
                input = scanner.nextLine();  // Read user
                // envoie vers le server:
                os.write(Byte.parseByte(input)); // caster String en liste de Byte[]
                // On inverse les choses au niveau de Client/server.
                output = String.valueOf(is.read()); // or : output = new String(bytes);
                System.out.println(output);
                
            }

        } catch (UnknownHostException e) { // Par exemple quand on spécifie une mauvaise adresse ip.
            e.printStackTrace();
        } catch (IOException e) { // Server n'est pas démarré (pour cela il faut que le n° de port soit utilisé qualque part au niveau du server.)
            e.printStackTrace();
        }
    }
}
