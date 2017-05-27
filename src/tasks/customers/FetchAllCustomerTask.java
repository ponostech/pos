/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.customers;

import java.util.List;
import javafx.concurrent.Task;
import jpa.CustomerJpa;
import ponospos.entities.Customer;

/**
 *
 * @author Sawmtea
 */
public class FetchAllCustomerTask extends Task<List<Customer>>{
    @Override
        protected List<Customer> call() throws Exception {
            return CustomerJpa.getAllCustomers();
        } 
}
