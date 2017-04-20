/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpa;


import java.util.List;
import javax.persistence.EntityManager;
import ponospos.entities.User;


/**
 *
 * @author Sawmtea
 */
public class UserJpa {
    
    public static User createUser(User user){
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.flush();
        em.close();
        return user;
    }
    public static User updateUser(User user){
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        em.flush();
        em.close();
        return user;
    }
    public static User deleteUser(User user){
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        em.getTransaction().begin();
        em.remove(user);
        em.getTransaction().commit();
        em.flush();
        em.close();
        return user;
    }
    public static List<User> getAllUsers(){
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        List<User> resultList = em.createQuery("SELECT u FROM User u",User.class).getResultList();
        em.close();
        return resultList;
    }
    public static User checkCredential(String username,String password){
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        List res = em.createNamedQuery("User.findByCredential")
                .setParameter("username",username)
                .setParameter("password", password)
                .getResultList();
        return (User) res.get(0);
    }
    
}
