/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;
import ponospos.entities.Payment;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class InvoiceJpa {
     public static Invoice createInvoice(Invoice invoice)throws Exception{
         EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
         List<Stock> stocks = invoice.getStocks();
         List<InvoiceItem> items = invoice.getInvoiceItem();
         Payment payment = invoice.getPayment();
         for (InvoiceItem item : items) {
             em.persist(item);
         }
         for (Stock stock : stocks) {
             em.persist(stock);
         }
         
        em.persist(invoice);
         for (InvoiceItem item : items) {
             em.persist(item);
         }
        em.flush();
        em.getTransaction().commit();
        em.close();
        
        return invoice;
    }
    public static Invoice updateInvoice(Invoice invoice)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(invoice);
        em.getTransaction().commit();
        em.close();
        return invoice;
    }
    public static Invoice deleteInvoice(Invoice invoice)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Invoice c = em.merge(invoice);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllInvoice()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        List<Invoice> all = em.createNamedQuery("Invoice.findAll",Invoice.class)
                .getResultList();
        em.flush();
        em.getTransaction().commit();
        em.close();
        return all;
    }

    public static List<Invoice> findBetweenDates(Date from, Date to) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        List<Invoice> all = em.createNamedQuery("Invoice.findBetweenDates", Invoice.class)
                .setParameter("from", from,TemporalType.DATE)
                .setParameter("to", to,TemporalType.DATE)
                .getResultList();
        em.flush();
        em.getTransaction().commit();
        em.close();
        return all;
    }
}
