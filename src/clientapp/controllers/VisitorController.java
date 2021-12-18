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
     * Demande à la classe LogInController d'afficher la page de connexion.
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
     * Demande au Main de connecter le user.
     */
    @Override
    public void onLogInAsked(User user) {
        listener.logIn(user);
    }

    /**
     * Demande à la classe RegisterController d'afficher la page de register.
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
     * Deamnde à la classe LoginController d'afficher la page de connexion.
     */
    @Override
    public void onRegisterAsked(User user) {
        listener.logIn(user);
    }

    /**
     * Demande à la classe LoginController d'afficher la page de connexion.
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
     * Demande à la classe ConditionsController d'afficher la page de conditions d'utilisation.
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
     * Demande à la classe RegisterController d'afficher la page de register.
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

