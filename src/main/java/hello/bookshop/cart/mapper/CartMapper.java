package hello.bookshop.cart.mapper;

import hello.bookshop.cart.domain.Cart;
import hello.bookshop.cart.domain.CartItem;
import hello.bookshop.cart.dto.response.CartItemDeleteResponse;
import hello.bookshop.cart.dto.response.CartItemForUpdateResponse;
import hello.bookshop.cart.dto.response.CartItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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

    List<CartItemResponse> findCartItemsByMemberId(Long memberId);

    Optional<CartItemForUpdateResponse> findCartItemForUpdate(
            @Param("memberId") Long memberId,
            @Param("cartItemId") Long cartItemId
    );

    int updateCartItemQuantity(
            @Param("cartItemId") Long cartItemId,
            @Param("memberId") Long memberId,
            @Param("quantity") Integer quantity
    );

    int deleteCartItem(
            @Param("cartItemId") Long cartItemId,
            @Param("memberId") Long memberId);

}
