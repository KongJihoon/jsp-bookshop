package hello.bookshop.product.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    ACTIVE, SOLD_OUT, DELETED
}
