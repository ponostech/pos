/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Variant;

/**
 *
 * @author Sawmtea
 */
public class VariantJpa {
 
    
    public static Variant createVariant(Variant variant)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(variant);
        em.getTransaction().commit();
        em.close();
        return variant;
    }
    public static Variant updateVariant(Variant variant)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(variant);
        em.getTransaction().commit();
        em.close();
        return variant;
    }
    public static Variant deleteVariant(Variant variant)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Variant c = em.merge(variant);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllVariant()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Variant> all = em.createNamedQuery("Variant.findAll",Variant.class)
                .getResultList();
        return all;
    }
   
    

}
