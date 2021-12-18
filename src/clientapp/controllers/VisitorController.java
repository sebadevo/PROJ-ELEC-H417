package clientapp.controllers;


import clientapp.controllers.visitor.ConditionsController;
import clientapp.controllers.visitor.LogInController;
import clientapp.controllers.visitor.RegisterController;
import clientapp.models.User;
import javafx.stage.Stage;

import java.io.IOException;

import static clientapp.MainClient.showError;

public class VisitorController implements ConditionsController.ConditionsListener, LogInController.LogInListener,
        RegisterController.RegisterListener {

    private final VisitorListener listener;
    private final Stage stage;
    private LogInController logInController;
    private RegisterController registerController;


    public VisitorController(VisitorListener listener, Stage stage) {
        this.listener = listener;
        this.stage = stage;
    }

    /**
     * Demande à la classe LogInController d'afficher la page de connexion.
     */
    public void show() {
        logInController = new LogInController(this, stage);
        try {
            logInController.show();
        } catch (Exception e) {
            showError(LogInController.LOAD_LOGIN_PAGE_ERROR);
        }
    }

    /**
     * Demande au groupe1.Main de connecter le user.
     * @param user user qui va se connecter.
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
        registerController = new RegisterController(this, stage);
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
    public void onRegisterAsked() {
        try {
            logInController.show();
        } catch (IOException e) {
            showError(LogInController.LOAD_LOGIN_PAGE_ERROR);
        }
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

