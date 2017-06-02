/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.payments;

import java.util.List;
import javafx.concurrent.Task;
import jpa.PaymentJpa;
import ponospos.entities.Payment;

/**
 *
 * @author Sawmtea
 */
public class FetchAllPaymentTask extends Task<List<Payment>>{

    @Override
    protected List<Payment> call() throws Exception {
        return PaymentJpa.getAllPayments();
    }
    
}
