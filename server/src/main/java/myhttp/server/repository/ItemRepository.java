package myhttp.server.repository;

import myhttp.server.entity.Item;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.List;


// 아이템 엔티티를 DB에 저장하고 조회하는 리포지토리 클래스
public class ItemRepository {

    private final EntityManagerFactory emf;

    public ItemRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public Item find(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Item.class, id);
        } finally {
            em.close();
        }
    }

    public List<Item> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT i FROM Item i", Item.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Item save(Item item) {
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction tx = em.getTransaction();

            tx.begin();
            em.persist(item);
            tx.commit();

            return item;
        } finally {
            em.close();
        }
    }

    public Item update(Item item) {
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction tx = em.getTransaction();

            tx.begin();
            Item updated = em.merge(item);
            tx.commit();

            return updated;
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction tx = em.getTransaction();

            tx.begin();
            Item item = em.find(Item.class, id);
            if (item != null) {
                em.remove(item);
            }
            tx.commit();
        } finally {
          em.close();
        }
    }
}
