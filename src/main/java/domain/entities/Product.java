package domain.entities;

import java.math.BigDecimal;
import domain.exceptions.ProductValidationException;
import lombok.Data;
import lombok.NonNull;

@Data
public class Product {

    private Long id;
    private String name;
    private String description;
    private String category;
    @NonNull
    private BigDecimal price;
    private String currency;
    private String imageUrl;

    public Product() {
    }

    public boolean validatePrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    public boolean validateCurrency(String currency) {
        return currency != null && !currency.trim().isEmpty();
    }

    public void setPrice(BigDecimal price) {
        if (!validatePrice(price)) {
            throw new ProductValidationException("Price cannot be negative");
        }
        this.price = price;
    }

    public void setCurrency(String currency) {
        if (!validateCurrency(currency)) {
            throw new ProductValidationException("Currency code cannot be null or empty");
        }
        this.currency = currency;
    }
}