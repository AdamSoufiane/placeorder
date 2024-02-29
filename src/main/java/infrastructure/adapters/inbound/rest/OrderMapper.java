package infrastructure.adapters.inbound.rest;

import domain.entities.Order;
import domain.entities.Product;
import domain.valueobjects.CustomerInfo;
import domain.valueobjects.PaymentDetails;
import domain.enums.OrderStatus;
import infrastructure.adapters.inbound.rest.dto.OrderItemResponse;
import infrastructure.adapters.inbound.rest.dto.OrderRequest;
import infrastructure.adapters.inbound.rest.dto.OrderResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderMapper {

    public static final Function<OrderRequest, Order> orderRequestToOrder = orderRequest -> {
        // Conversion logic for OrderStatus and LocalDateTime
        OrderStatus status = null;
        try {
            status = OrderStatus.valueOf(orderRequest.getStatus());
        } catch (IllegalArgumentException | NullPointerException e) {
            // Handle invalid status value
        }

        LocalDateTime dateCreated = null;
        try {
            dateCreated = LocalDateTime.parse(orderRequest.getDateCreated(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // Handle invalid date format
        }

        // Assuming OrderRequest has the necessary fields
        CustomerInfo customerInfo = orderRequest.getCustomerInfo();
        PaymentDetails paymentDetails = orderRequest.getPaymentDetails();
        String customerComments = orderRequest.getCustomerComments();

        Order order = new Order();
        order.setCustomerInfo(customerInfo);
        order.setPaymentDetails(paymentDetails);
        order.setCustomerComments(customerComments);
        order.setStatus(status);
        order.setDateCreated(dateCreated);

        List<Product> products = orderRequest.getOrderItems().stream()
                .map(item -> new Product(
                        item.getProductId(),
                        item.getName(),
                        item.getDescription(),
                        item.getCategory(),
                        item.getPrice(),
                        item.getCurrency()
                ))
                .collect(Collectors.toList());
        order.setOrderItems(products);

        return order;
    };

    public static final Function<Order, OrderResponse> orderToOrderResponse = order -> {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        // Verified calculateTotal method with boolean parameter
        orderResponse.setTotalPrice(order.calculateTotal(true));
        orderResponse.setOrderDate(order.getDateCreated() != null ? order.getDateCreated().toLocalDate() : null);
        orderResponse.setCustomerName(order.getCustomerInfo() != null ? order.getCustomerInfo().getName() : null);
        orderResponse.setOrderItems(convertOrderItems(order.getOrderItems()));

        // Verified existence of getters for createdBy, lastModifiedBy, and version
        orderResponse.setCreatedBy(order.getCreatedBy());
        orderResponse.setLastModifiedBy(order.getLastModifiedBy());
        orderResponse.setVersion(order.getVersion());

        return orderResponse;
    };

    private static List<OrderItemResponse> convertOrderItems(List<Product> products) {
        return products.stream().map(product -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setProductId(product.getId());
            itemResponse.setName(product.getName());
            itemResponse.setDescription(product.getDescription());
            itemResponse.setPrice(product.getPrice());
            itemResponse.setCategory(product.getCategory());
            return itemResponse;
        }).collect(Collectors.toList());
    }

}