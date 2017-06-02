/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.invoices;

import javafx.concurrent.Task;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.InvoiceJpa;
import jpa.JpaSingleton;
import jpa.StockJpa;
import jpa.jpa2.InvoiceJpaController;
import ponospos.entities.Invoice;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class CreateInvoiceTask extends Task<Invoice>{

    private Invoice invoice;

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

   
    
    @Override
    protected Invoice call() throws Exception {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PonosPosPU");
//        InvoiceJpaController jpa=new InvoiceJpaController(emf);
//        return jpa.create(invoice);
        return InvoiceJpa.createInvoice(invoice);
    }
    
}
