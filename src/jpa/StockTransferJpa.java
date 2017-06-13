/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Stock;
import ponospos.entities.StockTransfer;
import ponospos.entities.StockTransferItem;
import ponospos.entities.Stores;

/**
 *
 * @author Sawmtea
 */
public class StockTransferJpa {
    public static void transfer(StockTransfer stocktransfer){
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        Stores from = stocktransfer.getFrom();
        Stores to = stocktransfer.getTo();
        
        List<StockTransferItem> items = stocktransfer.getItems();
        
        List<Stock> fromStock = from.getStocks();
        List<Stock> toStock = to.getStocks();
        em.getTransaction().begin();
        for (StockTransferItem item : items) {
            
        }
        em.persist(stocktransfer);
        
        em.getTransaction().commit();
        em.close();
    }
}
