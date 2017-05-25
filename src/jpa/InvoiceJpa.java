/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Invoice;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class InvoiceJpa {
     public static Invoice createInvoice(Invoice invoice)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(invoice);
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
        List<Invoice> all = em.createNamedQuery("Product.findAll",Invoice.class)
                .getResultList();
        return all;
    }
}
