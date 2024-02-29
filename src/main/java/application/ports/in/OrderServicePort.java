package application.ports.in;

import domain.entities.Order;
import domain.exceptions.OrderValidationException;
import domain.exceptions.PaymentProcessingException;
import infrastructure.adapters.inbound.rest.dto.OrderRequest;

public interface OrderServicePort {

    Order placeOrder(OrderRequest orderRequest) throws OrderValidationException, PaymentProcessingException;

}