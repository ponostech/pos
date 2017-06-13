/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Product;
import ponospos.entities.Stores;

/**
 *
 * @author Sawmtea
 */
public class ProductJpa {
 
    
    public static Product createProduct(Product product)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(product);
        em.flush();
        em.getTransaction().commit();
        em.close();
        return product;
    }
    public static Product updateProduct(Product product)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(product);
        em.flush();
        em.getTransaction().commit();
        em.close();
        return product;
    }
    public static Product deleteProduct(Product product)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Product c = em.merge(product);
        em.remove(c);
        em.flush();
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List<Product> getAllProduct()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> all = em.createQuery("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.stocks")
                .getResultList();
       
        em.close();
        return all;
    }
    public static List findProductName(String name){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        
        List<Product> founds = em.createNamedQuery("Product.findByName", Product.class)
                .setParameter("name", name+"%")
                .getResultList();
        em.close();
        return founds;
    }
    public static List findProductNameOrBarcode(String name){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> founds = em.createNamedQuery("Product.find", Product.class)
                .setParameter("param", name+"%")
                .getResultList();
       
        em.close();
        return founds;
    }

    public static List<Product> findStockInStore(Stores store) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        try{
            List<Product> founds = em.createNamedQuery("Stock.findProduct", Product.class)
                    .setParameter("store", store)
                    .getResultList();
            return founds;
        }finally{
            if (em!=null) {
                
                em.close();
            }
        }
    }

    public static List<Product> findAvailableByName(Stores store,String param) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> founds = em.createNamedQuery("Stock.findProductByName", Product.class)
                .setParameter("store", store)
                .setParameter("param", param+"%")
                .getResultList();

        return founds;
    }
    

    public static boolean existBarcode(String barcode){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> founds = em.createNamedQuery("Product.findByBarcode", Product.class)
                .setParameter("barcode", barcode)
                .getResultList();
        em.close();
        if (founds.isEmpty()) {
            return false;
        }else{
            return true;
        }
    }
}
