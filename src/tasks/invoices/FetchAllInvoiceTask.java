/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.invoices;

import java.util.List;
import javafx.concurrent.Task;
import jpa.InvoiceJpa;
import ponospos.entities.Invoice;

/**
 *
 * @author Sawmtea
 */
public class FetchAllInvoiceTask extends Task<List<Invoice>>{

    @Override
    protected List<Invoice> call() throws Exception {
        return InvoiceJpa.getAllInvoice();
    }
    
}
