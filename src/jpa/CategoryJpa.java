/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Category;
import ponospos.entities.Customer;

/**
 *
 * @author Sawmtea
 */
public class CategoryJpa {
 
    
    public static Category createCategory(Category category)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(category);
        em.getTransaction().commit();
        em.close();
        return category;
    }
    public static Category updateCategory(Category category)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(category);
        em.getTransaction().commit();
        em.close();
        return category;
    }
    public static Category deleteCategory(Category category)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Category c = em.merge(category);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllCategory()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Category> all = em.createNamedQuery("Category.findAll",Category.class)
                .getResultList();
        em.close();
        return all;
    }
    public static List findCategoryName(String name){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Category> founds = em.createNamedQuery("Category.findByName", Category.class)
                .setParameter("name", name+"%")
                .getResultList();
        em.close();
        return founds;
    }
    

}
