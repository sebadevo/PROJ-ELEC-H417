package serverapp;

import serverapp.controllers.ServerController;



public class MainServer extends Thread implements ServerController.ServerListener{
    private ServerController serverController;


    public static void main(String[] args){
        new MainServer().start();
    }

    @Override
    public void run() {
        // try {
        //     UserDatabase.getInstance().load();
        // } catch (DatabaseLoadException e){
        //     System.out.println(Database.LOAD_ERROR);
        // }
        serverController = new ServerController(this);
        serverController.startServer();
    }
}
