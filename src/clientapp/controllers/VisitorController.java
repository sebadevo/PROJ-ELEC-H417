package clientapp.controllers;


import clientapp.controllers.visitor.ConditionsController;
import clientapp.controllers.visitor.LogInController;
import clientapp.controllers.visitor.RegisterController;
import clientapp.models.User;

import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static clientapp.MainClient.showError;

public class VisitorController implements ConditionsController.ConditionsListener, LogInController.LogInListener,
        RegisterController.RegisterListener {

    private final VisitorListener listener;
    private final Stage stage;
    private LogInController logInController;
    private RegisterController registerController;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;


    public VisitorController(VisitorListener listener, Stage stage, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.listener = listener;
        this.stage = stage;
        this.socket = socket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
    }

    /**
     * Ask to LogInController class to show the connection page.
     */
    public void show() {
        logInController = new LogInController(this, stage, socket, printWriter, bufferedReader);
        try {
            logInController.show();
        } catch (Exception e) {
            System.out.println("je suis arrivé ici"); // TODO probleme quand on lance via ligne de commande.
            showError(LogInController.LOAD_LOGIN_PAGE_ERROR);
        }
    }

    /**
     * Ask Main to connect the user.
     */
    @Override
    public void onLogInAsked(User user) {
        listener.logIn(user);
    }

    /**
     * Ask the RegisterController class to show the register interface.
     */
    @Override
    public void onRegisterLinkAsked() {
        registerController = new RegisterController(this, stage, socket, printWriter, bufferedReader);
        try {
            registerController.show();
        } catch (IOException e) {
            showError(RegisterController.LOAD_REGISTER_PAGE_ERROR);
        }
    }

    /**
     * Ask the LogInController to show the connection interface
     */
    @Override
    public void onRegisterAsked(User user) {
        listener.logIn(user);
    }

    /**
     * Ask the LogInController class to show the connection interface.
     */
    @Override
    public void onBackToLogInAsked() {
        try {
            logInController.show();
        } catch (IOException e) {
            showError(LogInController.LOAD_LOGIN_PAGE_ERROR);
        }
    }

    /**
     * Ask to ConditionsController class to show the terms of use interface.
     */
    @Override
    public void onConditionsAsked() {
        ConditionsController conditionsController = new ConditionsController(this, stage);
        try {
            conditionsController.show();
        } catch (IOException e) {
            showError(ConditionsController.LOAD_CONDITIONS_PAGE_ERROR);
        }
    }

    /**
     * Ask to RegisterController class to show the register interface.
     */
    @Override
    public void onBackToRegisterAsked() {
        try {
            registerController.show();
        } catch (IOException e) {
            showError(RegisterController.LOAD_REGISTER_PAGE_ERROR);
        }
    }

    public interface VisitorListener {
        void logIn(User user);
    }
}

