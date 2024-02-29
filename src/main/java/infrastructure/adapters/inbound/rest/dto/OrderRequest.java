package infrastructure.adapters.inbound.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import domain.entities.OrderItem;
import domain.entities.PaymentDetails;

@Data
@NoArgsConstructor
public class OrderRequest {

    @NotBlank
    private String customerId;

    @NotEmpty
    private List<OrderItem> orderItems;

    @NotNull
    private PaymentDetails paymentDetails;

}