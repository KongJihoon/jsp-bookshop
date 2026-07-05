package hello.bookshop.order.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderFormRequest {

    private List<Long> cartItemIds;
}
