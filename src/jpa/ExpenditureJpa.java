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
import ponospos.entities.Category;
import ponospos.entities.Expenditure;

/**
 *
 * @author Sawmtea
 */
public class ExpenditureJpa {
 
    
    public static Expenditure createExpenditure(Expenditure category)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(category);
        em.getTransaction().commit();
        em.close();
        return category;
    }
    public static Expenditure updateExpenditure(Expenditure exp)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(exp);
        em.getTransaction().commit();
        em.close();
        return exp;
    }
    public static Expenditure deleteExpenditure(Expenditure exp)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Expenditure c = em.merge(exp);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllExpenditure()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Expenditure> all = em.createNamedQuery("Expenditure.findAll",Expenditure.class)
                .getResultList();
        em.close();
        return all;
    }
    public static List findExpenditureByDate(Date date){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Expenditure> founds = em.createNamedQuery("Expenditure.findByDate", Expenditure.class)
                .setParameter("param", date,TemporalType.DATE)
                .getResultList();
        em.close();
        return founds;
    }
    

}
