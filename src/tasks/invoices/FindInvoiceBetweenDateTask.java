/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.invoices;

import java.util.Date;
import java.util.List;
import javafx.concurrent.Task;
import jpa.InvoiceJpa;
import ponospos.entities.Invoice;

/**
 *
 * @author Sawmtea
 */
public class FindInvoiceBetweenDateTask extends Task<List<Invoice>>{

    private Date from;
    private Date to;
    @Override
    protected List<Invoice> call() throws Exception {
        
        return InvoiceJpa.findBetweenDates(from,to);
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public void setTo(Date to) {
        this.to = to;
    }
    
    
}
