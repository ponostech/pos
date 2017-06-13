/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.controls;

import controllers.modals.ExceptionDialog;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JFrame;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JRViewer;
import ponospos.entities.Customer;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.Payment;
import singletons.Auth;

/**
 *
 * @author Sawmtea
 */
public class PrintReport extends JFrame {
    private static final long serialVersionUID = 2L;


    public void printInvoices(Invoice invoice) {

        InputStream receipt = getClass().getResourceAsStream("/resource/reports/receipt.jasper");

        String discount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getDiscount().doubleValue());
        String tax = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getTax().doubleValue());
        String amount = NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(invoice.getTotal().doubleValue());

        Map parameters = new HashMap();

        Customer c=invoice.getCustomer();
        if (c!=null) 
            parameters.put("CustomerName", c.getFirstName());
        else
            parameters.put("CustomerName", "NA");

        parameters.put("Discount", discount);
        parameters.put("Tax", tax);
        parameters.put("Total", amount);
        parameters.put("REPORT_LOCALE", new Locale("en","in"));

        parameters.put("StoreName", invoice.getStore().getName());
        parameters.put("StoreContact", invoice.getStore().getContact());
        parameters.put("StoreAddress", invoice.getStore().getAddress());
        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(receipt, parameters,
                    new JRBeanCollectionDataSource(invoice.getInvoiceItem()));
            JRViewer viewer = new JRViewer(jasperPrint);
            viewer.setOpaque(true);
            viewer.setVisible(true);
            this.add(viewer);
            this.setSize(1024, 800);
            this.setVisible(true);
        } catch (Exception e1) {
            new ExceptionDialog(e1);
        }

    }
    public void printCustomerInvoice(Customer customer,List<Payment>payments){
        
        List<InvoiceItem> items=new ArrayList<InvoiceItem>();
        for (Payment payment : payments) {
            items.addAll(payment.getInvoice().getInvoiceItem());
        }
        InputStream receipt = getClass().getResourceAsStream("/resource/reports/invoice.jasper");

        Map parameters = new HashMap();

        parameters.put("CustomerName", customer.getFirstName());
        parameters.put("CustomeAddress", customer.getAddress());
        parameters.put("CustomerContact", customer.getContact());
        
        BigDecimal totalDiscount=new BigDecimal(0);
        BigDecimal totalAmount=new BigDecimal(0);
        BigDecimal totalTax=new BigDecimal(0);
                
        for (Payment p : payments) {
            totalDiscount.add(p.getInvoice().getDiscount());
            totalAmount.add(p.getInvoice().getTotal());
            totalTax.add(p.getInvoice().getTax());
        }

        parameters.put("Discount", totalDiscount);
        parameters.put("Tax", totalTax);
        parameters.put("Total", totalAmount);
        parameters.put("REPORT_LOCALE", new Locale("en", "in"));

        JasperPrint jasperPrint;
        try {
            jasperPrint = JasperFillManager.fillReport(receipt, parameters,
                    new JRBeanCollectionDataSource(items));
            JRViewer viewer = new JRViewer(jasperPrint);
            viewer.setOpaque(true);
            viewer.setVisible(true);
            this.add(viewer);
            this.setSize(1024, 800);
            this.setVisible(true);
        } catch (Exception e1) {
            new ExceptionDialog(e1);
        }
    }
}
