package daohibernate;

import dao.OVChipkaartDAO;
import dao.ProductDAO;
import domain.Adres;
import domain.OVChipkaart;
import domain.Product;
import domain.Reiziger;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class OVChipkaartDAOHibernate implements OVChipkaartDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public boolean save(OVChipkaart ov) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(ov);

            for (Product p : ov.getProducten()) {
                p.addOVChipkaart(ov);
                session.merge(p);
            }

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

            OVChipkaart managed = session.merge(ov);

            for(Product p : new ArrayList<>(managed.getProducten())) {
                if(!ov.getProducten().contains(p)) {
                    p.removeOVChipkaart(managed);
                    session.merge(p);
                }
            }

            for (Product p : ov.getProducten()) {
                if(!managed.getProducten().contains(p)) {
                    p.addOVChipkaart(managed);
                    session.merge(p);
                }
            }

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

            OVChipkaart managed = session.merge(ov);

            for(Product p : new ArrayList<>(managed.getProducten())) {
                p.removeOVChipkaart(managed);
                session.merge(p);
            }

            session.remove(managed);
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
