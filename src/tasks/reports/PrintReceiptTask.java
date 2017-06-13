/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tasks.reports;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.concurrent.Task;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ponospos.entities.Invoice;

/**
 *
 * @author Sawmtea
 */
public class PrintReceiptTask extends Task<JasperPrint>{

    public Invoice invoice;
    @Override
    protected JasperPrint call() throws Exception {
        InputStream receipt = getClass().getResourceAsStream("/resource/reports/receipt.jasper");

        String discount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getDiscount().doubleValue());
        String tax = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getTax().doubleValue());
        String amount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getTotal().doubleValue());

        Map parameters = new HashMap();
        parameters.put("CustomerName", invoice.getCustomer().getFirstName());

        parameters.put("Discount", discount);
        parameters.put("Tax", tax);
        parameters.put("Total", amount);
        parameters.put("REPORT_LOCALE", new Locale("en", "in"));

        parameters.put("StoreName", "");
        parameters.put("StoreContact", "fasdf");
        parameters.put("StoreAddress", "fasdf");
        JasperPrint jasperPrint;
        
            jasperPrint = JasperFillManager.fillReport(receipt, parameters,
                    new JRBeanCollectionDataSource(invoice.getInvoiceItem()));
            return jasperPrint;
        
    }
    
}
