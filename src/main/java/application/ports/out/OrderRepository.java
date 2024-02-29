package application.ports.out;

import domain.entities.Order;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface OrderRepository {

    Optional<Order> findById(Long id);

    Order save(Order order);

    void deleteById(Long id);

    List<Order> findAll();
}