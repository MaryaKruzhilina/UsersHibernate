package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {

    public UserDaoHibernateImpl() {

    }


    @Override
    public void createUsersTable() {
        String sql = """
                    CREATE TABLE IF NOT EXISTS user (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(20),
                    last_name VARCHAR(20),
                    age TINYINT
                    );
                    """;
        try (Session session = Util.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            Query query = session.createSQLQuery(sql).addEntity(User.class);
            session.getTransaction().commit();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dropUsersTable() {
        try (Session session = Util.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            String sql = "DROP TABLE IF EXISTS user";
            session.createSQLQuery(sql).executeUpdate();
            session.getTransaction().commit();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.save(new User(name, lastName, age));
            session.getTransaction().commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            session.delete(user);
            session.getTransaction().commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        Transaction transaction = null;
        List<User> users = new ArrayList<>();
        try (Session session = Util.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            users = session.createQuery("FROM User").getResultList();
            session.getTransaction().commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.createSQLQuery("DELETE FROM user").executeUpdate();
            session.getTransaction().commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
