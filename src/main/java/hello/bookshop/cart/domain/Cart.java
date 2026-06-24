package hello.bookshop.cart.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Cart {

    private Long cartId;

    private Long memberId;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    @Builder
    private Cart(Long memberId) {
        this.memberId = memberId;
    }

    public static Cart create(Long memberId) {
        return Cart.builder()
                .memberId(memberId)
                .build();
    }

}
