package domain.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Order {

    private Long id;
    private CustomerInfo customerInfo;
    private List<Product> orderItems;
    private PaymentDetails paymentDetails;
    private OrderStatus status;
    private LocalDateTime dateCreated;
    private String customerComments;

    public Order(Long id, CustomerInfo customerInfo, List<Product> orderItems, PaymentDetails paymentDetails, OrderStatus status, LocalDateTime dateCreated, String customerComments) {
        this.id = id;
        this.customerInfo = customerInfo;
        this.orderItems = orderItems != null ? orderItems : new ArrayList<>();
        this.paymentDetails = paymentDetails;
        this.status = status;
        this.dateCreated = dateCreated != null ? dateCreated : LocalDateTime.now();
        this.customerComments = customerComments;
    }

    public BigDecimal calculateTotalIncludingTaxDiscountsShipping() {
        BigDecimal total = calculateTotalExcludingTaxDiscountsShipping();
        // TODO: Implement the logic for applying taxes, discounts, and shipping costs
        // Example: total = total.add(tax).subtract(discounts).add(shipping);
        return total;
    }

    public BigDecimal calculateTotalExcludingTaxDiscountsShipping() {
        return orderItems.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void removeAllProductsByType(String productType) {
        orderItems.removeIf(product -> product.getType().equals(productType));
    }
}