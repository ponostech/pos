/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.customers;

/**
 *
 * @author Sawmtea
 */


import java.util.List;
import javafx.concurrent.Task;
import jpa.CustomerJpa;
import ponospos.entities.Customer;

 public class FindCustomerTask extends Task<List<Customer>>{
        private String fname;
        private String lname;
        @Override
        protected List<Customer> call() throws Exception {
           
            return CustomerJpa.findCustomerByName(fname, lname);
        } 

        public void setFname(String fname) {
            this.fname = fname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }
        
    }