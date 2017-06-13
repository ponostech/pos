/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;

import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.Category;
import ponospos.entities.Unit;

/**
 *
 * @author Sawmtea
 */
public class UnitJpa {
    public static Unit createUnit(Unit unit) throws Exception {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.persist(unit);
        em.getTransaction().commit();
        em.close();
        return unit;
    }

    public static Unit updateUnit(Unit unit) throws Exception {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(unit);
        em.getTransaction().commit();
        em.close();
        return unit;
    }

    public static Unit deleteUnit(Unit unit) throws Exception {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        Unit c = em.merge(unit);
        em.remove(c);
        em.getTransaction().commit();
        em.close();
        return c;
    }
    public static List getAllUnit() throws Exception {
        EntityManager em = JpaSingleton.getInstance().createNewEntityManager();
        List<Unit> all = em.createNamedQuery("Unit.findAll", Unit.class)
                .getResultList();
        em.close();
        return all;
    }
}
