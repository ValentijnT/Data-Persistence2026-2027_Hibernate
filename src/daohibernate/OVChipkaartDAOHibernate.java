package daohibernate;

import dao.OVChipkaartDAO;
import domain.Adres;
import domain.OVChipkaart;
import domain.Reiziger;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class OVChipkaartDAOHibernate implements OVChipkaartDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public boolean save(OVChipkaart ov) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(ov);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("fout bij save ov: " + ov);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(OVChipkaart ov) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(ov);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("fout bij update ov: " + ov);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(OVChipkaart ov) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.remove(ov);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.err.println("fout bij delete ov: " + ov);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<OVChipkaart> findByReiziger(Reiziger r) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "FROM OVChipkaart WHERE reiziger = :r", OVChipkaart.class)
                    .setParameter("r", r)
                    .getResultList();
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OVChipkaart> findAll() {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM OVChipkaart", OVChipkaart.class).list();
        }
    }
}
