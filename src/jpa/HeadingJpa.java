/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.ExpenseHeading;

/**
 *
 * @author Sawmtea
 */
public class HeadingJpa {
    public static ExpenseHeading createExpenseHeading(ExpenseHeading head) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(head);
        em.getTransaction().commit();
        em.close();
        return head;
    }

    public static ExpenseHeading updateExpenseHeading(ExpenseHeading head) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(head);
        em.getTransaction().commit();
        em.close();
        return head;
    }

    public static ExpenseHeading deleteExpenseHeading(ExpenseHeading head) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        ExpenseHeading c = em.merge(head);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }

    public static List getAllExpenseHeading() {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<ExpenseHeading> all = em.createNamedQuery("ExpenseHeading.findAll", ExpenseHeading.class)
                .getResultList();
        em.close();
        return all;
    }
    public static List getAllExpenseHeadingByName(String name) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<ExpenseHeading> all = em.createNamedQuery("ExpenseHeading.findByName", ExpenseHeading.class)
                .setParameter("param", "%"+name+"%")
                .getResultList();
        em.close();
        return all;
    }
}
