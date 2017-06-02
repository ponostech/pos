/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.jpa2;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jpa.jpa2.exceptions.NonexistentEntityException;
import ponospos.entities.Product;
import ponospos.entities.Invoice;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class StockJpaController implements Serializable {

    public StockJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Stock stock) {
        EntityManager em = null;
        try {
            em = getEntityManager();
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
    }

    public void edit(Stock stock) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Stock persistentStock = em.find(Stock.class, stock.getId());
            Product productOld = persistentStock.getProduct();
            Product productNew = stock.getProduct();
            Invoice invoiceOld = persistentStock.getInvoice();
            Invoice invoiceNew = stock.getInvoice();
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getId());
                stock.setProduct(productNew);
            }
            if (invoiceNew != null) {
                invoiceNew = em.getReference(invoiceNew.getClass(), invoiceNew.getId());
                stock.setInvoice(invoiceNew);
            }
            stock = em.merge(stock);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getStocks().remove(stock);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getStocks().add(stock);
                productNew = em.merge(productNew);
            }
            if (invoiceOld != null && !invoiceOld.equals(invoiceNew)) {
                invoiceOld.getStocks().remove(stock);
                invoiceOld = em.merge(invoiceOld);
            }
            if (invoiceNew != null && !invoiceNew.equals(invoiceOld)) {
                invoiceNew.getStocks().add(stock);
                invoiceNew = em.merge(invoiceNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = stock.getId();
                if (findStock(id) == null) {
                    throw new NonexistentEntityException("The stock with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Stock stock;
            try {
                stock = em.getReference(Stock.class, id);
                stock.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The stock with id " + id + " no longer exists.", enfe);
            }
            Product product = stock.getProduct();
            if (product != null) {
                product.getStocks().remove(stock);
                product = em.merge(product);
            }
            Invoice invoice = stock.getInvoice();
            if (invoice != null) {
                invoice.getStocks().remove(stock);
                invoice = em.merge(invoice);
            }
            em.remove(stock);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Stock> findStockEntities() {
        return findStockEntities(true, -1, -1);
    }

    public List<Stock> findStockEntities(int maxResults, int firstResult) {
        return findStockEntities(false, maxResults, firstResult);
    }

    private List<Stock> findStockEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Stock.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Stock findStock(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Stock.class, id);
        } finally {
            em.close();
        }
    }

    public int getStockCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Stock> rt = cq.from(Stock.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
