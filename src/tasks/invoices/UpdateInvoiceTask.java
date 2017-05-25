/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.invoices;

import javafx.concurrent.Task;
import jpa.InvoiceJpa;
import jpa.StockJpa;
import ponospos.entities.Invoice;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class UpdateInvoiceTask extends Task<Invoice>{

    private Invoice invoice;
    
    @Override
    protected Invoice call() throws Exception {
        return InvoiceJpa.updateInvoice(invoice);
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    
    
    
   
    
}
