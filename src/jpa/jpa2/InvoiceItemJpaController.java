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
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;

/**
 *
 * @author Sawmtea
 */
public class InvoiceItemJpaController implements Serializable {

    public InvoiceItemJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(InvoiceItem invoiceItem) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice invoice = invoiceItem.getInvoice();
            if (invoice != null) {
                invoice = em.getReference(invoice.getClass(), invoice.getId());
                invoiceItem.setInvoice(invoice);
            }
            em.persist(invoiceItem);
            if (invoice != null) {
                invoice.getInvoiceItem().add(invoiceItem);
                invoice = em.merge(invoice);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(InvoiceItem invoiceItem) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            InvoiceItem persistentInvoiceItem = em.find(InvoiceItem.class, invoiceItem.getId());
            Invoice invoiceOld = persistentInvoiceItem.getInvoice();
            Invoice invoiceNew = invoiceItem.getInvoice();
            if (invoiceNew != null) {
                invoiceNew = em.getReference(invoiceNew.getClass(), invoiceNew.getId());
                invoiceItem.setInvoice(invoiceNew);
            }
            invoiceItem = em.merge(invoiceItem);
            if (invoiceOld != null && !invoiceOld.equals(invoiceNew)) {
                invoiceOld.getInvoiceItem().remove(invoiceItem);
                invoiceOld = em.merge(invoiceOld);
            }
            if (invoiceNew != null && !invoiceNew.equals(invoiceOld)) {
                invoiceNew.getInvoiceItem().add(invoiceItem);
                invoiceNew = em.merge(invoiceNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invoiceItem.getId();
                if (findInvoiceItem(id) == null) {
                    throw new NonexistentEntityException("The invoiceItem with id " + id + " no longer exists.");
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
            InvoiceItem invoiceItem;
            try {
                invoiceItem = em.getReference(InvoiceItem.class, id);
                invoiceItem.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoiceItem with id " + id + " no longer exists.", enfe);
            }
            Invoice invoice = invoiceItem.getInvoice();
            if (invoice != null) {
                invoice.getInvoiceItem().remove(invoiceItem);
                invoice = em.merge(invoice);
            }
            em.remove(invoiceItem);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<InvoiceItem> findInvoiceItemEntities() {
        return findInvoiceItemEntities(true, -1, -1);
    }

    public List<InvoiceItem> findInvoiceItemEntities(int maxResults, int firstResult) {
        return findInvoiceItemEntities(false, maxResults, firstResult);
    }

    private List<InvoiceItem> findInvoiceItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(InvoiceItem.class));
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

    public InvoiceItem findInvoiceItem(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(InvoiceItem.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvoiceItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<InvoiceItem> rt = cq.from(InvoiceItem.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
