/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa.jpa2;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ponospos.entities.Attribute;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.jpa2.exceptions.IllegalOrphanException;
import jpa.jpa2.exceptions.NonexistentEntityException;
import ponospos.entities.Product;
import ponospos.entities.Stock;

/**
 *
 * @author Sawmtea
 */
public class ProductJpaController implements Serializable {

    public ProductJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Product product) {
        if (product.getAttributes() == null) {
            product.setAttributes(new ArrayList<Attribute>());
        }
        if (product.getStocks() == null) {
            product.setStocks(new ArrayList<Stock>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Attribute> attachedAttributes = new ArrayList<Attribute>();
            for (Attribute attributesAttributeToAttach : product.getAttributes()) {
                attributesAttributeToAttach = em.getReference(attributesAttributeToAttach.getClass(), attributesAttributeToAttach.getId());
                attachedAttributes.add(attributesAttributeToAttach);
            }
            product.setAttributes(attachedAttributes);
            List<Stock> attachedStocks = new ArrayList<Stock>();
            for (Stock stocksStockToAttach : product.getStocks()) {
                stocksStockToAttach = em.getReference(stocksStockToAttach.getClass(), stocksStockToAttach.getId());
                attachedStocks.add(stocksStockToAttach);
            }
            product.setStocks(attachedStocks);
            em.persist(product);
            for (Attribute attributesAttribute : product.getAttributes()) {
                Product oldProductOfAttributesAttribute = attributesAttribute.getProduct();
                attributesAttribute.setProduct(product);
                attributesAttribute = em.merge(attributesAttribute);
                if (oldProductOfAttributesAttribute != null) {
                    oldProductOfAttributesAttribute.getAttributes().remove(attributesAttribute);
                    oldProductOfAttributesAttribute = em.merge(oldProductOfAttributesAttribute);
                }
            }
            for (Stock stocksStock : product.getStocks()) {
                Product oldProductOfStocksStock = stocksStock.getProduct();
                stocksStock.setProduct(product);
                stocksStock = em.merge(stocksStock);
                if (oldProductOfStocksStock != null) {
                    oldProductOfStocksStock.getStocks().remove(stocksStock);
                    oldProductOfStocksStock = em.merge(oldProductOfStocksStock);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Product product) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product persistentProduct = em.find(Product.class, product.getId());
            List<Attribute> attributesOld = persistentProduct.getAttributes();
            List<Attribute> attributesNew = product.getAttributes();
            List<Stock> stocksOld = persistentProduct.getStocks();
            List<Stock> stocksNew = product.getStocks();
            List<String> illegalOrphanMessages = null;
            for (Stock stocksOldStock : stocksOld) {
                if (!stocksNew.contains(stocksOldStock)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Stock " + stocksOldStock + " since its product field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Attribute> attachedAttributesNew = new ArrayList<Attribute>();
            for (Attribute attributesNewAttributeToAttach : attributesNew) {
                attributesNewAttributeToAttach = em.getReference(attributesNewAttributeToAttach.getClass(), attributesNewAttributeToAttach.getId());
                attachedAttributesNew.add(attributesNewAttributeToAttach);
            }
            attributesNew = attachedAttributesNew;
            product.setAttributes(attributesNew);
            List<Stock> attachedStocksNew = new ArrayList<Stock>();
            for (Stock stocksNewStockToAttach : stocksNew) {
                stocksNewStockToAttach = em.getReference(stocksNewStockToAttach.getClass(), stocksNewStockToAttach.getId());
                attachedStocksNew.add(stocksNewStockToAttach);
            }
            stocksNew = attachedStocksNew;
            product.setStocks(stocksNew);
            product = em.merge(product);
            for (Attribute attributesOldAttribute : attributesOld) {
                if (!attributesNew.contains(attributesOldAttribute)) {
                    attributesOldAttribute.setProduct(null);
                    attributesOldAttribute = em.merge(attributesOldAttribute);
                }
            }
            for (Attribute attributesNewAttribute : attributesNew) {
                if (!attributesOld.contains(attributesNewAttribute)) {
                    Product oldProductOfAttributesNewAttribute = attributesNewAttribute.getProduct();
                    attributesNewAttribute.setProduct(product);
                    attributesNewAttribute = em.merge(attributesNewAttribute);
                    if (oldProductOfAttributesNewAttribute != null && !oldProductOfAttributesNewAttribute.equals(product)) {
                        oldProductOfAttributesNewAttribute.getAttributes().remove(attributesNewAttribute);
                        oldProductOfAttributesNewAttribute = em.merge(oldProductOfAttributesNewAttribute);
                    }
                }
            }
            for (Stock stocksNewStock : stocksNew) {
                if (!stocksOld.contains(stocksNewStock)) {
                    Product oldProductOfStocksNewStock = stocksNewStock.getProduct();
                    stocksNewStock.setProduct(product);
                    stocksNewStock = em.merge(stocksNewStock);
                    if (oldProductOfStocksNewStock != null && !oldProductOfStocksNewStock.equals(product)) {
                        oldProductOfStocksNewStock.getStocks().remove(stocksNewStock);
                        oldProductOfStocksNewStock = em.merge(oldProductOfStocksNewStock);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = product.getId();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Stock> stocksOrphanCheck = product.getStocks();
            for (Stock stocksOrphanCheckStock : stocksOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Stock " + stocksOrphanCheckStock + " in its stocks field has a non-nullable product field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Attribute> attributes = product.getAttributes();
            for (Attribute attributesAttribute : attributes) {
                attributesAttribute.setProduct(null);
                attributesAttribute = em.merge(attributesAttribute);
            }
            em.remove(product);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
