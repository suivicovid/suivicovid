package org.suivicovid.dao;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.suivicovid.data.AppInfo;
import org.suivicovid.data.Patient;

public class JpaDao {
    private static class InstanceHolder {
        public static JpaDao instance = new JpaDao();
    }

    public static JpaDao getInstance() {
        return InstanceHolder.instance;
    }

    @PersistenceContext(unitName = "suivicovid")
    private EntityManager em;

    EntityManagerFactory fac = null;

    public EntityManager getEM() {
        if (em != null)
            return em;

        fac = Persistence.createEntityManagerFactory("suivicovid");
        em = fac.createEntityManager();
        return em;
    }

    public EntityManager getEMWithTx() {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        if (!tx.isActive())
            tx.begin();
        return em;
    }

    public void flush() {
        getEMWithTx().flush();
    }

    public void clear() {
        getEMWithTx().clear();
    }

    public void commit() {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        if (tx != null && tx.isActive())
            tx.commit();
    }

    public void rollback() {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        if (tx != null && tx.isActive())
            tx.rollback();
    }

    public <T> T merge(T o) {
        EntityManager em = getEMWithTx();
        return (T) em.merge(o);
    }

    public <T> void persist(T t) {
        EntityManager em = getEMWithTx();
        em.persist(t);
    }

    public <T> void remove(T t) {
        EntityManager em = getEMWithTx();
        t = em.merge(t);
        em.remove(t);
    }

    public <T> T getById(Class<T> claz, Serializable id) {
        EntityManager em = getEMWithTx();

        return em.find(claz, id);
    }

    public <T> List<T> getAll(Class<T> claz) {
        EntityManager em = getEMWithTx();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(claz);
        return em.createQuery(q.select(q.from(claz))).getResultList();
    }

    public int getDbVersion() {
        AppInfo i = getById(AppInfo.class, 1L);
        return i == null ? 4 : i.getAppVersion();
    }

    public void checkAndUpdateDb() {
        backup();
        int currentVersion = 20;
        int dbVersion = getDbVersion();
        System.out.println("db version " + dbVersion);

        if (currentVersion == dbVersion)
            return;

        if (dbVersion < 5)
            updateTov5();

        if (dbVersion < 6)
            updateTov6();

        if (dbVersion < 15)
            updateToV15();

        updateDbVersion(currentVersion);
    }

    private void updateToV15() {
        JpaDao.getInstance().getAll(Patient.class).stream().forEach(p -> p.updateConsistency());
        JpaDao.getInstance().commit();
    }

    private void updateDbVersion(int currentVersion) {
        AppInfo i = getById(AppInfo.class, 1L);
        if (i == null)
            i = new AppInfo();
        i.setId(1);
        i.setAppVersion(currentVersion);
        i = merge(i);
        commit();
    }

    private void updateTov5() {
        EntityManager em = getEMWithTx();
        Query q = em.createNativeQuery("ALTER TABLE PATIENT ALTER COLUMN NOTES VARCHAR(2048)");
        q.executeUpdate();
    }

    private void updateTov6() {
        // should be handled by hibernate autoupdate
    }

    private void backup() {
        try {
            EntityManager em = getEMWithTx();
            LocalDateTime dt = LocalDateTime.now();
            // String dts = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));
            String dts = dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String home = System.getProperty("user.home");
            String file = home + "/suivicovid-backup-" + dts + ".zip";
            if (new File(file).exists())
                return;
            System.out.println("backup " + file);
            Query q = em.createNativeQuery("BACKUP TO '" + file + "'");
            q.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}