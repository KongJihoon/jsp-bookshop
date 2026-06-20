package hello.bookshop.cart.mapper;

import hello.bookshop.cart.domain.Cart;
import hello.bookshop.cart.domain.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface CartMapper {

    Optional<Cart> findCartByMemberId(Long memberId);

    void saveCart(Cart cart);

    Optional<CartItem> findCartItemByCartIdAndProductId(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId
    );

    void saveCartItem(CartItem cartItem);

    void increaseQuantity(
            @Param("cartItemId") Long cartItemId,
            @Param("quantity") int quantity
    );

}
