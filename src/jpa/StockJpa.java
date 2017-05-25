/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Product;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class StockJpa {
    public static Stock createStock(Stock stock)throws Exception{
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(stock);
        em.getTransaction().commit();
        em.close();
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
        List<Stock> all = em.createNamedQuery("Stock.findAll",Stock.class)
                .getResultList();
        return all;
    }

    public static List<Stock> findStockByName(String param) {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Stock> all = em.createNamedQuery("Stock.findByItem",Stock.class)
                .setParameter("param", "%"+param+"%")
                .getResultList();
        return all;
    }
}
