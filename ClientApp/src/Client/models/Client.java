package Client.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args){
        try{
            Socket s = new Socket("localhost", 1234); //Au lieu de localhost on peut: 127.0.0.1
            InputStream is = s.getInputStream(); // Lit des octets.
            OutputStream os = s.getOutputStream(); // Envoie des octets.

            // pour lire une info clavier :
            Scanner scanner = new Scanner(System.in);
            System.out.print("Donner un nombre: ");
            int nb = scanner.nextInt();

            // envoie vers le server:
            // On inverse les choses au niveau de Client/server.
            os.write(nb);
            int rep = is.read();
            // Un octet est compris entre -127 et 127. Donc si on envoit qlq. ch. de plus gr. ce sera tronqué.
            System.out.println("Resultat = "+rep);

        } catch (UnknownHostException e) { // Par exemple quand on spécifie une mauvaise adresse ip.
            e.printStackTrace();
        } catch (IOException e) { // Server n'est pas démarré (pour cela il faut que le n° de port soit utilisé qualque part au niveau du server.)
            e.printStackTrace();
        }
    }
}
