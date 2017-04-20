package jpa;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sawmtea
 */
public class JpaSingleton {
    private  final EntityManagerFactory entityManagerFactory;
    private  final EntityManager entityManager;
    
    private JpaSingleton() {
        entityManagerFactory=Persistence.createEntityManagerFactory("PonosPosPU");
        entityManager=entityManagerFactory.createEntityManager();
    }
    
    public static JpaSingleton getInstance() {
        return JpaSingletonHolder.INSTANCE;
    }
    
    private static class JpaSingletonHolder {

        private static final JpaSingleton INSTANCE = new JpaSingleton();
    }
    
    public EntityManager getEntityManager(){
        return entityManager;
    }
}
