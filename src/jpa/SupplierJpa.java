/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Customer;
import ponospos.entities.Supplier;
import ponospos.entities.User;

/**
 *
 * @author Sawmtea
 */
public class SupplierJpa {
    public static Supplier createSupplier(Supplier supplier)throws Exception{
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager();
        
        em.getTransaction().begin();
        em.persist(supplier);
        em.getTransaction().commit();
        em.close();
        return supplier;
    }
    public static Supplier updateSupplier(Supplier supplier)throws Exception{
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(supplier);
        em.getTransaction().commit();
        em.close();
        return supplier;
    }
    public static Supplier deleteSupplier(Supplier supplier){
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager(); 
        em.getTransaction().begin();
        Supplier s = em.merge(supplier);
        em.remove(s);
        em.getTransaction().commit();
        em.close();
        return s;
    }
    public static List<Supplier> getAllSupplier(){
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager();
        List<Supplier> resultList = em.createQuery("SELECT s FROM Supplier s",Supplier.class).getResultList();
        em.close();
        System.out.println("alll supplier:"+resultList);
        return resultList;
    }
    public static List findCustomerByName(String name  ){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Supplier> founds = em.createNamedQuery("Supplier.findByName", Supplier.class)
                .setParameter("name", name+"%")
                .getResultList();

        return founds;
    }
}
