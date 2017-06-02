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
import ponospos.entities.Invoice;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.jpa2.exceptions.IllegalOrphanException;
import jpa.jpa2.exceptions.NonexistentEntityException;
import ponospos.entities.Payment;

/**
 *
 * @author Sawmtea
 */
public class PaymentJpaController implements Serializable {

    public PaymentJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Payment payment) throws IllegalOrphanException {
        List<String> illegalOrphanMessages = null;
        Invoice invoiceOrphanCheck = payment.getInvoice();
        if (invoiceOrphanCheck != null) {
            Payment oldPaymentOfInvoice = invoiceOrphanCheck.getPayment();
            if (oldPaymentOfInvoice != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Invoice " + invoiceOrphanCheck + " already has an item of type Payment whose invoice column cannot be null. Please make another selection for the invoice field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice invoice = payment.getInvoice();
            if (invoice != null) {
                invoice = em.getReference(invoice.getClass(), invoice.getId());
                payment.setInvoice(invoice);
            }
            em.persist(payment);
            if (invoice != null) {
                invoice.setPayment(payment);
                invoice = em.merge(invoice);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Payment payment) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Payment persistentPayment = em.find(Payment.class, payment.getId());
            Invoice invoiceOld = persistentPayment.getInvoice();
            Invoice invoiceNew = payment.getInvoice();
            List<String> illegalOrphanMessages = null;
            if (invoiceNew != null && !invoiceNew.equals(invoiceOld)) {
                Payment oldPaymentOfInvoice = invoiceNew.getPayment();
                if (oldPaymentOfInvoice != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Invoice " + invoiceNew + " already has an item of type Payment whose invoice column cannot be null. Please make another selection for the invoice field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (invoiceNew != null) {
                invoiceNew = em.getReference(invoiceNew.getClass(), invoiceNew.getId());
                payment.setInvoice(invoiceNew);
            }
            payment = em.merge(payment);
            if (invoiceOld != null && !invoiceOld.equals(invoiceNew)) {
                invoiceOld.setPayment(null);
                invoiceOld = em.merge(invoiceOld);
            }
            if (invoiceNew != null && !invoiceNew.equals(invoiceOld)) {
                invoiceNew.setPayment(payment);
                invoiceNew = em.merge(invoiceNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = payment.getId();
                if (findPayment(id) == null) {
                    throw new NonexistentEntityException("The payment with id " + id + " no longer exists.");
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
            Payment payment;
            try {
                payment = em.getReference(Payment.class, id);
                payment.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The payment with id " + id + " no longer exists.", enfe);
            }
            Invoice invoice = payment.getInvoice();
            if (invoice != null) {
                invoice.setPayment(null);
                invoice = em.merge(invoice);
            }
            em.remove(payment);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Payment> findPaymentEntities() {
        return findPaymentEntities(true, -1, -1);
    }

    public List<Payment> findPaymentEntities(int maxResults, int firstResult) {
        return findPaymentEntities(false, maxResults, firstResult);
    }

    private List<Payment> findPaymentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Payment.class));
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

    public Payment findPayment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Payment.class, id);
        } finally {
            em.close();
        }
    }

    public int getPaymentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Payment> rt = cq.from(Payment.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
