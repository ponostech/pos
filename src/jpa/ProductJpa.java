/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Category;
import ponospos.entities.Product;

/**
 *
 * @author Sawmtea
 */
public class ProductJpa {
 
    
    public static Product createProduct(Product product)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(product);
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
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllProduct()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> all = em.createNamedQuery("Product.findAll",Product.class)
                .getResultList();
        return all;
    }
    public static List findProductName(String name){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> founds = em.createNamedQuery("Product.findByName", Product.class)
                .setParameter("name", name+"%")
                .getResultList();

        return founds;
    }
    public static List findProductNameOrBarcode(String name){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Product> founds = em.createNamedQuery("Product.find", Product.class)
                .setParameter("param", name+"%")
                .getResultList();

        return founds;
    }
    

}
