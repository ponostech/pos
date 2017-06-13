/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Customer;

/**
 *
 * @author Sawmtea
 */
public class CustomerJpa {
    
    public static Customer createCustomer(Customer customer){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(customer);
        em.getTransaction().commit();
        em.close();
        return customer;
    }
    public static Customer updateCustomer(Customer customer){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(customer);
        em.getTransaction().commit();
        em.close();
        return customer;
    }
    public static Customer deleteCustomer(Customer customer){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Customer c = em.merge(customer);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllCustomers(){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Customer> all = em.createNamedQuery("Customer.findAll",Customer.class)
                .getResultList();
        em.close();
        return all;
    }
    public static List getAllCustomerPayment(){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Customer> all = em.createNamedQuery("Customer.findAll",Customer.class)
                .getResultList();
        em.close();
        return all;
    }
    public static List findCustomerByName(String fname,String lname){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Customer> founds = em.createNamedQuery("Customer.findByNames", Customer.class)
                .setParameter("fname", fname+"%")
                .setParameter("lname", lname+"%")
                .getResultList();
        em.close();

        return founds;
    }
}
