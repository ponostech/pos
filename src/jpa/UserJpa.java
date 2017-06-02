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
    
    public static User createUser(User user)throws Exception{
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager();
        
        em.getTransaction().begin();
        em.persist(user);
        em.flush();
        em.getTransaction().commit();
        em.close();
        System.out.println(user);
        return user;
    }
    public static User updateUser(User user){
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        em.close();
        return user;
    }
    public static User deleteUser(User user){
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager(); 
        em.getTransaction().begin();
        User u = em.merge(user);
        em.remove(u);
        em.getTransaction().commit();
        em.close();
        return u;
    }
    public static List<User> getAllUsers(){
        EntityManager em=JpaSingleton.getInstance().createNewEntityManager();
        List<User> resultList = em.createQuery("SELECT u FROM User u",User.class).getResultList();
        em.close();
        return resultList;
    }
    public static User checkCredential(String username,String password)throws Exception{
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        User user = em.createNamedQuery("User.findByCredential",User.class)
                .setParameter("username",username)
                .setParameter("password", password)
                .getSingleResult();
        return user;
    }
     public static List searchByUsername(String username)throws Exception{
        EntityManager em=JpaSingleton.getInstance().getEntityManager();
        em.getTransaction().begin();
        List<User> users = em.createNamedQuery("User.findByUsername",User.class)
                .setParameter("uname",username+"%")
                .getResultList();
        em.flush();
        em.getTransaction().commit();
        return users;
    }
    
}
