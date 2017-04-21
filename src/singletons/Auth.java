/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import ponospos.entities.User;



/**
 *
 * @author Sawmtea
 */
public class Auth {
    private User user;
    private boolean isLogged;
    private Auth() {
    }
    
    public static Auth getInstance() {
        return AuthHolder.INSTANCE;
    }
    
    private static class AuthHolder {

        private static final Auth INSTANCE = new Auth();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isIsLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }
    
}
