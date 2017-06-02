/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.payments;

import java.util.Date;
import java.util.List;
import javafx.concurrent.Task;
import jpa.PaymentJpa;
import ponospos.entities.Customer;
import ponospos.entities.Payment;

/**
 *
 * @author Sawmtea
 */
public class FindCustomerPaymentTask extends Task<List<Payment>>{

    public Customer customer;
    public Date from;
    public Date to;
    @Override
    protected List<Payment> call() throws Exception {
        return PaymentJpa.getCustomerPayments(customer, from, to);
    }
    
}
