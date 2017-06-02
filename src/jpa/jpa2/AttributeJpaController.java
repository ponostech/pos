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
import ponospos.entities.Attribute;
import ponospos.entities.Product;

/**
 *
 * @author Sawmtea
 */
public class AttributeJpaController implements Serializable {

    public AttributeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Attribute attribute) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Product product = attribute.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getId());
                attribute.setProduct(product);
            }
            em.persist(attribute);
            if (product != null) {
                product.getAttributes().add(attribute);
                product = em.merge(product);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Attribute attribute) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Attribute persistentAttribute = em.find(Attribute.class, attribute.getId());
            Product productOld = persistentAttribute.getProduct();
            Product productNew = attribute.getProduct();
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getId());
                attribute.setProduct(productNew);
            }
            attribute = em.merge(attribute);
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getAttributes().remove(attribute);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getAttributes().add(attribute);
                productNew = em.merge(productNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = attribute.getId();
                if (findAttribute(id) == null) {
                    throw new NonexistentEntityException("The attribute with id " + id + " no longer exists.");
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
            Attribute attribute;
            try {
                attribute = em.getReference(Attribute.class, id);
                attribute.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The attribute with id " + id + " no longer exists.", enfe);
            }
            Product product = attribute.getProduct();
            if (product != null) {
                product.getAttributes().remove(attribute);
                product = em.merge(product);
            }
            em.remove(attribute);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Attribute> findAttributeEntities() {
        return findAttributeEntities(true, -1, -1);
    }

    public List<Attribute> findAttributeEntities(int maxResults, int firstResult) {
        return findAttributeEntities(false, maxResults, firstResult);
    }

    private List<Attribute> findAttributeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Attribute.class));
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

    public Attribute findAttribute(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Attribute.class, id);
        } finally {
            em.close();
        }
    }

    public int getAttributeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Attribute> rt = cq.from(Attribute.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
