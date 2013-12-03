// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package cs.ut.security;

import cs.ut.security.Users;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Users_Roo_Jpa_ActiveRecord {
    
    @PersistenceContext
    transient EntityManager Users.entityManager;
    
    public static final EntityManager Users.entityManager() {
        EntityManager em = new Users().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long Users.countUserses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Users o", Long.class).getSingleResult();
    }
    
    public static List<Users> Users.findAllUserses() {
        return entityManager().createQuery("SELECT o FROM Users o", Users.class).getResultList();
    }
    
    public static Users Users.findUsers(Long id) {
        if (id == null) return null;
        return entityManager().find(Users.class, id);
    }
    
    public static List<Users> Users.findUsersEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Users o", Users.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    @Transactional
    public void Users.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void Users.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            Users attached = Users.findUsers(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void Users.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void Users.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public Users Users.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Users merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
}
