/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Invoice;
import ponospos.entities.Product;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class StockJpa {
    public static Stock createStock(Stock stock)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
//        em.getTransaction().begin();
//        em.persist(stock);
//        em.getTransaction().commit();
//        em.refresh(stock);
//        em.close();
try {
           
            em.getTransaction().begin();
            Product product = stock.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getId());
                stock.setProduct(product);
            }
            Invoice invoice = stock.getInvoice();
            if (invoice != null) {
                invoice = em.getReference(invoice.getClass(), invoice.getId());
                stock.setInvoice(invoice);
            }
            em.persist(stock);
            if (product != null) {
                product.getStocks().add(stock);
                product = em.merge(product);
            }
            if (invoice != null) {
                invoice.getStocks().add(stock);
                invoice = em.merge(invoice);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return stock;
    }
    public static Stock updateStock(Stock stock)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(stock);
        em.getTransaction().commit();
        em.close();
        return stock;
    }
    public static Stock deleteStock(Stock stock)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Stock c = em.merge(stock);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    
    public static List getAllStock()throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        List<Stock> all = em.createNamedQuery("Stock.findAll",Stock.class)
                .getResultList();
        em.clear();
        em.getTransaction().commit();
        em.close();
        
        return all;
    }

    public static List<Stock> findStockByName(String param) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        List<Stock> all = em.createNamedQuery("Stock.findByItem",Stock.class)
                .setParameter("param", "%"+param+"%")
                .getResultList();
        em.flush();
        em.getTransaction().commit();
        em.close();
        return all;
    }
}
