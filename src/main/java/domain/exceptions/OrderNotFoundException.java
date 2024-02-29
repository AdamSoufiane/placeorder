package domain.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderNotFoundException extends RuntimeException {

    private final String orderId;

}