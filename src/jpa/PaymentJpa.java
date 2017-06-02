/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import ponospos.entities.Customer;
import ponospos.entities.Payment;

/**
 *
 * @author Sawmtea
 */
public class PaymentJpa {
    public static List getAllPayments() throws Exception {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        
        List<Payment> all = em.createNamedQuery("Payment.findAll", Payment.class)
                .getResultList();
        
        em.close();
        return all;
    }
    public static List getCustomerPayments(Customer c,Date from,Date to) throws Exception {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        
        List<Payment> all = em.createNamedQuery("Payment.findCustomerPayment", Payment.class)
                .setParameter("customer", c)
                .setParameter("from", from,TemporalType.DATE)
                .setParameter("to", to,TemporalType.DATE)
                .getResultList();
        
        em.close();
        return all;
    }
}
