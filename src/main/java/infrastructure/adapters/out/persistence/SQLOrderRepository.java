package infrastructure.adapters.out.persistence;

import domain.entities.Order;
import application.ports.out.OrderRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class SQLOrderRepository implements OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(SQLOrderRepository.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Order> findById(Long id) {
        try {
            Order order = entityManager.find(Order.class, id);
            return Optional.ofNullable(order);
        } catch (EntityNotFoundException e) {
            log.warn("Order not found with id: {}", id);
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Data access exception occurred while finding order with id: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Order save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        try {
            entityManager.persist(order);
            return order;
        } catch (DataAccessException e) {
            log.error("Data access exception occurred while saving order", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            Order order = entityManager.find(Order.class, id);
            if (order != null) {
                entityManager.remove(order);
            }
        } catch (EntityNotFoundException e) {
            log.warn("Attempted to delete non-existing order with id: {}", id);
        } catch (DataAccessException e) {
            log.error("Data access exception occurred while deleting order with id: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<Order> findAll() {
        try {
            return entityManager.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        } catch (DataAccessException e) {
            log.error("Data access exception occurred while retrieving all orders", e);
            return Collections.emptyList();
        }
    }
}
