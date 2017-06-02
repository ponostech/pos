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
import ponospos.entities.Payment;
import ponospos.entities.Stock;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jpa.jpa2.exceptions.IllegalOrphanException;
import jpa.jpa2.exceptions.NonexistentEntityException;
import ponospos.entities.Invoice;
import ponospos.entities.InvoiceItem;

/**
 *
 * @author Sawmtea
 */
public class InvoiceJpaController implements Serializable {

    public InvoiceJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Invoice invoice) {
        if (invoice.getStocks() == null) {
            invoice.setStocks(new ArrayList<Stock>());
        }
        if (invoice.getInvoiceItem() == null) {
            invoice.setInvoiceItem(new ArrayList<InvoiceItem>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Payment payment = invoice.getPayment();
            if (payment != null) {
                payment = em.getReference(payment.getClass(), payment.getId());
                invoice.setPayment(payment);
            }
            List<Stock> attachedStocks = new ArrayList<Stock>();
            for (Stock stocksStockToAttach : invoice.getStocks()) {
                stocksStockToAttach = em.getReference(stocksStockToAttach.getClass(), stocksStockToAttach.getId());
                attachedStocks.add(stocksStockToAttach);
            }
            invoice.setStocks(attachedStocks);
            List<InvoiceItem> attachedInvoiceItem = new ArrayList<InvoiceItem>();
            for (InvoiceItem invoiceItemInvoiceItemToAttach : invoice.getInvoiceItem()) {
                invoiceItemInvoiceItemToAttach = em.getReference(invoiceItemInvoiceItemToAttach.getClass(), invoiceItemInvoiceItemToAttach.getId());
                attachedInvoiceItem.add(invoiceItemInvoiceItemToAttach);
            }
            invoice.setInvoiceItem(attachedInvoiceItem);
            em.persist(invoice);
            if (payment != null) {
                Invoice oldInvoiceOfPayment = payment.getInvoice();
                if (oldInvoiceOfPayment != null) {
                    oldInvoiceOfPayment.setPayment(null);
                    oldInvoiceOfPayment = em.merge(oldInvoiceOfPayment);
                }
                payment.setInvoice(invoice);
                payment = em.merge(payment);
            }
            for (Stock stocksStock : invoice.getStocks()) {
                Invoice oldInvoiceOfStocksStock = stocksStock.getInvoice();
                stocksStock.setInvoice(invoice);
                stocksStock = em.merge(stocksStock);
                if (oldInvoiceOfStocksStock != null) {
                    oldInvoiceOfStocksStock.getStocks().remove(stocksStock);
                    oldInvoiceOfStocksStock = em.merge(oldInvoiceOfStocksStock);
                }
            }
            for (InvoiceItem invoiceItemInvoiceItem : invoice.getInvoiceItem()) {
                Invoice oldInvoiceOfInvoiceItemInvoiceItem = invoiceItemInvoiceItem.getInvoice();
                invoiceItemInvoiceItem.setInvoice(invoice);
                invoiceItemInvoiceItem = em.merge(invoiceItemInvoiceItem);
                if (oldInvoiceOfInvoiceItemInvoiceItem != null) {
                    oldInvoiceOfInvoiceItemInvoiceItem.getInvoiceItem().remove(invoiceItemInvoiceItem);
                    oldInvoiceOfInvoiceItemInvoiceItem = em.merge(oldInvoiceOfInvoiceItemInvoiceItem);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Invoice invoice) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Invoice persistentInvoice = em.find(Invoice.class, invoice.getId());
            Payment paymentOld = persistentInvoice.getPayment();
            Payment paymentNew = invoice.getPayment();
            List<Stock> stocksOld = persistentInvoice.getStocks();
            List<Stock> stocksNew = invoice.getStocks();
            List<InvoiceItem> invoiceItemOld = persistentInvoice.getInvoiceItem();
            List<InvoiceItem> invoiceItemNew = invoice.getInvoiceItem();
            List<String> illegalOrphanMessages = null;
            if (paymentOld != null && !paymentOld.equals(paymentNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Payment " + paymentOld + " since its invoice field is not nullable.");
            }
            for (InvoiceItem invoiceItemOldInvoiceItem : invoiceItemOld) {
                if (!invoiceItemNew.contains(invoiceItemOldInvoiceItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain InvoiceItem " + invoiceItemOldInvoiceItem + " since its invoice field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (paymentNew != null) {
                paymentNew = em.getReference(paymentNew.getClass(), paymentNew.getId());
                invoice.setPayment(paymentNew);
            }
            List<Stock> attachedStocksNew = new ArrayList<Stock>();
            for (Stock stocksNewStockToAttach : stocksNew) {
                stocksNewStockToAttach = em.getReference(stocksNewStockToAttach.getClass(), stocksNewStockToAttach.getId());
                attachedStocksNew.add(stocksNewStockToAttach);
            }
            stocksNew = attachedStocksNew;
            invoice.setStocks(stocksNew);
            List<InvoiceItem> attachedInvoiceItemNew = new ArrayList<InvoiceItem>();
            for (InvoiceItem invoiceItemNewInvoiceItemToAttach : invoiceItemNew) {
                invoiceItemNewInvoiceItemToAttach = em.getReference(invoiceItemNewInvoiceItemToAttach.getClass(), invoiceItemNewInvoiceItemToAttach.getId());
                attachedInvoiceItemNew.add(invoiceItemNewInvoiceItemToAttach);
            }
            invoiceItemNew = attachedInvoiceItemNew;
            invoice.setInvoiceItem(invoiceItemNew);
            invoice = em.merge(invoice);
            if (paymentNew != null && !paymentNew.equals(paymentOld)) {
                Invoice oldInvoiceOfPayment = paymentNew.getInvoice();
                if (oldInvoiceOfPayment != null) {
                    oldInvoiceOfPayment.setPayment(null);
                    oldInvoiceOfPayment = em.merge(oldInvoiceOfPayment);
                }
                paymentNew.setInvoice(invoice);
                paymentNew = em.merge(paymentNew);
            }
            for (Stock stocksOldStock : stocksOld) {
                if (!stocksNew.contains(stocksOldStock)) {
                    stocksOldStock.setInvoice(null);
                    stocksOldStock = em.merge(stocksOldStock);
                }
            }
            for (Stock stocksNewStock : stocksNew) {
                if (!stocksOld.contains(stocksNewStock)) {
                    Invoice oldInvoiceOfStocksNewStock = stocksNewStock.getInvoice();
                    stocksNewStock.setInvoice(invoice);
                    stocksNewStock = em.merge(stocksNewStock);
                    if (oldInvoiceOfStocksNewStock != null && !oldInvoiceOfStocksNewStock.equals(invoice)) {
                        oldInvoiceOfStocksNewStock.getStocks().remove(stocksNewStock);
                        oldInvoiceOfStocksNewStock = em.merge(oldInvoiceOfStocksNewStock);
                    }
                }
            }
            for (InvoiceItem invoiceItemNewInvoiceItem : invoiceItemNew) {
                if (!invoiceItemOld.contains(invoiceItemNewInvoiceItem)) {
                    Invoice oldInvoiceOfInvoiceItemNewInvoiceItem = invoiceItemNewInvoiceItem.getInvoice();
                    invoiceItemNewInvoiceItem.setInvoice(invoice);
                    invoiceItemNewInvoiceItem = em.merge(invoiceItemNewInvoiceItem);
                    if (oldInvoiceOfInvoiceItemNewInvoiceItem != null && !oldInvoiceOfInvoiceItemNewInvoiceItem.equals(invoice)) {
                        oldInvoiceOfInvoiceItemNewInvoiceItem.getInvoiceItem().remove(invoiceItemNewInvoiceItem);
                        oldInvoiceOfInvoiceItemNewInvoiceItem = em.merge(oldInvoiceOfInvoiceItemNewInvoiceItem);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invoice.getId();
                if (findInvoice(id) == null) {
                    throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.");
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
            Invoice invoice;
            try {
                invoice = em.getReference(Invoice.class, id);
                invoice.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invoice with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Payment paymentOrphanCheck = invoice.getPayment();
            if (paymentOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Invoice (" + invoice + ") cannot be destroyed since the Payment " + paymentOrphanCheck + " in its payment field has a non-nullable invoice field.");
            }
            List<InvoiceItem> invoiceItemOrphanCheck = invoice.getInvoiceItem();
            for (InvoiceItem invoiceItemOrphanCheckInvoiceItem : invoiceItemOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Invoice (" + invoice + ") cannot be destroyed since the InvoiceItem " + invoiceItemOrphanCheckInvoiceItem + " in its invoiceItem field has a non-nullable invoice field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Stock> stocks = invoice.getStocks();
            for (Stock stocksStock : stocks) {
                stocksStock.setInvoice(null);
                stocksStock = em.merge(stocksStock);
            }
            em.remove(invoice);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Invoice> findInvoiceEntities() {
        return findInvoiceEntities(true, -1, -1);
    }

    public List<Invoice> findInvoiceEntities(int maxResults, int firstResult) {
        return findInvoiceEntities(false, maxResults, firstResult);
    }

    private List<Invoice> findInvoiceEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Invoice.class));
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

    public Invoice findInvoice(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Invoice.class, id);
        } finally {
            em.close();
        }
    }

    public int getInvoiceCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Invoice> rt = cq.from(Invoice.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
