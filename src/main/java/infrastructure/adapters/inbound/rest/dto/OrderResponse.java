package infrastructure.adapters.inbound.rest.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String status;
    private BigDecimal totalPrice;
    private LocalDate orderDate;
    private String customerName;
    private String createdBy;
    private LocalDateTime lastUpdated;
    private Long version;
    private List<OrderItemResponse> orderItems;

}
