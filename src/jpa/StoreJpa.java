/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Category;
import ponospos.entities.Stores;

/**
 *
 * @author Sawmtea
 */
public class StoreJpa {
 
    
    public static Stores createStore(Stores store)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(store);
        em.getTransaction().commit();
        em.close();
        return store;
    }
    public static Stores updateStore(Stores store)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(store);
        em.getTransaction().commit();
        em.close();
        return store;
    }
    public static Stores deleteStore(Stores stores)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Stores c = em.merge(stores);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllStore()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Stores> all = em.createNamedQuery("Stores.findAll",Stores.class)
                .getResultList();
        return all;
    }
    public static List findStoreByName(String name){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Stores> founds = em.createNamedQuery("Stores.findByName", Stores.class)
                .setParameter("name", name+"%")
                .getResultList();

        return founds;
    }

}
