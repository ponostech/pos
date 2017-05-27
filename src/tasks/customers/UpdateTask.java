/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.customers;

import javafx.concurrent.Task;
import jpa.CustomerJpa;
import ponospos.entities.Customer;

/**
 *
 * @author Sawmtea
 */
 public class UpdateTask extends Task<Customer>{
         private Customer customer;
        @Override
        protected Customer call() throws Exception {
            return CustomerJpa.updateCustomer(customer);
        }
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
    
    }