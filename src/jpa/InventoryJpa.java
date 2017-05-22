/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Inventory;
import ponospos.entities.Product;

/**
 *
 * @author Sawmtea
 */
public class InventoryJpa {
    public static Inventory createProduct(Inventory inventory)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(inventory);
        em.getTransaction().commit();
        em.close();
        return inventory;
    }
    public static Inventory updateProduct(Inventory inventory)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(inventory);
        em.getTransaction().commit();
        em.close();
        return inventory;
    }
    public static Inventory deleteProduct(Inventory inventory)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Inventory c = em.merge(inventory);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List<Inventory> getAllProductInInventory()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Inventory> all = em.createNamedQuery("Inventory.findAll",Inventory.class)
                .getResultList();
        return all;
    }
   
}
