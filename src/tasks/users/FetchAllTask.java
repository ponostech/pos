/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.users;

import java.util.List;
import javafx.concurrent.Task;
import jpa.UserJpa;
import ponospos.entities.User;

/**
 *
 * @author Sawmtea
 */
public class FetchAllTask extends Task<List<User>>{

        @Override
        protected List<User> call() throws Exception {
            List<User> all = UserJpa.getAllUsers();
            return all;
        }
        
    }