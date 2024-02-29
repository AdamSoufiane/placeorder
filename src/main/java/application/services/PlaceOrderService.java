package application.services;

import application.ports.in.OrderServicePort;
import application.ports.out.OrderRepository;
import domain.entities.Order;
import domain.events.OrderPlacedEvent;
import domain.exceptions.*;
import domain.validation.OrderValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;

/**
 * Service class for placing orders. Implements the OrderServicePort interface.
 */
@RequiredArgsConstructor
public class PlaceOrderService implements OrderServicePort {

    private static final Logger log = LoggerFactory.getLogger(PlaceOrderService.class);
    private final OrderRepository orderRepository;
    private final OrderValidationService orderValidationService;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;

    /**
     * Places an order and persists it to the database.
     *
     * @param order The order to be placed.
     * @return The persisted order.
     * @throws OrderValidationException If the order validation fails.
     * @throws OrderPersistenceException If persisting the order fails.
     */
    @Transactional
    @Override
    public Order placeOrder(Order order) throws OrderValidationException, OrderPersistenceException {
        if (order == null || order.getId() == null || !orderValidationService.isCustomerCommentValid(order.getCustomerComments())) {
            throw new OrderValidationException("Order, order ID, and customer comments must not be null and must meet business rules");
        }
        if (!orderValidationService.areOrderItemsValid(order.getOrderItems())) {
            throw new OrderValidationException("Order items are not valid");
        }
        if (!orderValidationService.isPaymentDetailsValid(order.getPaymentDetails())) {
            throw new OrderValidationException("Payment details are not valid");
        }
        BigDecimal taxesAndFees = orderValidationService.calculateTaxesAndFees(order);
        order.setTotalPrice(order.getTotalPrice().add(taxesAndFees));
        log.info("Placing an order with ID: {}", order.getId());
        // Additional validations...
        // Persist the order
        try {
            Order savedOrder = orderRepository.save(order);
            eventPublisher.publishEvent(new OrderPlacedEvent(savedOrder));
            return savedOrder;
        } catch (DataAccessException e) {
            log.error("Data access exception while persisting the order with ID: {}", order.getId(), e);
            throw new OrderPersistenceException("Failed to persist the order", e);
        }
    }

    /**
     * Cancels an order based on its current status.
     *
     * @param orderId The ID of the order to cancel.
     * @throws OrderCancellationException If the order cannot be cancelled.
     * @throws OrderNotFoundException If the order is not found.
     */
    public void cancelOrder(Long orderId) throws OrderCancellationException, OrderNotFoundException {
        transactionTemplate.execute(status -> {
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found"));
            if (order.getStatus().isCancellable()) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            } else {
                throw new OrderCancellationException("Order cannot be cancelled");
            }
            return order;
        });
    }

    // Other service methods...

    // Private helper methods
}
