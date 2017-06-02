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
import jpa.jpa2.exceptions.PreexistingEntityException;
import ponospos.entities.Inventory;

/**
 *
 * @author Sawmtea
 */
public class InventoryJpaController implements Serializable {

    public InventoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Inventory inventory) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(inventory);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findInventory(inventory.getId()) != null) {
                throw new PreexistingEntityException("Inventory " + inventory + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Inventory inventory) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            inventory = em.merge(inventory);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = inventory.getId();
                if (findInventory(id) == null) {
                    throw new NonexistentEntityException("The inventory with id " + id + " no longer exists.");
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
            Inventory inventory;
            try {
                inventory = em.getReference(Inventory.class, id);
                inventory.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The inventory with id " + id + " no longer exists.", enfe);
            }
            em.remove(inventory);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Inventory> findInventoryEntities() {
        return findInventoryEntities(true, -1, -1);
    }

    public List<Inventory> findInventoryEntities(int maxResults, int firstResult) {
        return findInventoryEntities(false, maxResults, firstResult);
    }

    private List<Inventory> findInventoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Inventory.class));
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

    public Inventory findInventory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Inventory.class, id);
        } finally {
            em.close();
        }
    }

    public int getInventoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Inventory> rt = cq.from(Inventory.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
