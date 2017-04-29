/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.users;

import javafx.concurrent.Task;
import jpa.UserJpa;
import ponospos.entities.User;

/**
 *
 * @author Sawmtea
 */
public class  CreateTask extends Task<User>{

        private User user;
        @Override
        protected User call() {
            
            return UserJpa.createUser(user);
            
        }
        public void setUser(User user){
            this.user=user;
        }
    
    }