package hello.bookshop.cart.service;


import hello.bookshop.cart.domain.Cart;
import hello.bookshop.cart.domain.CartItem;
import hello.bookshop.cart.dto.response.*;
import hello.bookshop.cart.mapper.CartMapper;
import hello.bookshop.common.exception.cart.CartItemNotFoundException;
import hello.bookshop.common.exception.member.MemberNotFoundException;
import hello.bookshop.common.exception.member.NotLoginMemberException;
import hello.bookshop.common.exception.product.ProductNotFoundException;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.mapper.ProductMapper;
import hello.bookshop.product.type.ProductStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final MemberMapper memberMapper;

    private final ProductMapper productMapper;

    private final CartMapper cartMapper;


    /**
     * 장바구니 상품 담기 기능
     */
    @Transactional
    public void addCartItem(Long memberId, Long productId, int quantity) {

        validateQuantity(quantity);

        Member member = memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId)
                .orElseThrow(() -> new NotLoginMemberException("로그인 후 사용 가능합니다."));

        Product product = productMapper.findByProductIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다."));

        validateProductForCart(product);

        Cart cart = findOrCreateCart(member.getMemberId());

        cartMapper.findCartItemByCartIdAndProductId(cart.getCartId(), product.getProductId())
                .ifPresentOrElse(
                        cartItem -> increaseCartItemQuantity(cartItem, product, quantity),
                () -> saveNewCartItem(cart, product, quantity));

    }

    /**
     * 장바구니 조회 기능
     */
    @Transactional(readOnly = true)
    public CartResponse findCart(Long memberId) {

        memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId)
                .orElseThrow(() -> new NotLoginMemberException("로그인 후 사용 가능합니다."));

        List<CartItemResponse> items = cartMapper.findCartItemsByMemberId(memberId);

        return new CartResponse(items);
    }

    /**
     * 장바구니 수량 변경
     */
    @Transactional
    public CartQuantityUpdateResponse updateQuantity(Long memberId, Long cartItemId, Integer quantity) {
        validateQuantity(quantity);

        CartItemForUpdateResponse cartItem = cartMapper.findCartItemForUpdate(memberId, cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if (cartItem.getStatus() != ProductStatus.ACTIVE) {
            throw new ProductNotFoundException("판매 중인 상품이 아닙니다.");
        }

        if (cartItem.getStockQuantity() < quantity) {
            throw new StockQuantityExceedException("재고 수량을 초과하였습니다.");
        }

        int updateCount = cartMapper.updateCartItemQuantity(cartItemId, memberId, quantity);

        if (updateCount == 0) {
            throw new CartItemNotFoundException("장바구니를 찾을 수 없습니다");
        }

        return new CartQuantityUpdateResponse(
                cartItem.getCartItemId(),
                quantity,
                cartItem.getPrice()
        );
    }

    /**
     * 장바구니 상품 삭제 기능
     */
    @Transactional
    public CartItemDeleteResponse deleteCartItem(Long memberId, Long cartItemId) {

        Member member = memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        int deletedCount = cartMapper.deleteCartItem(cartItemId, member.getMemberId());

        if (deletedCount == 0) {
            throw new CartItemNotFoundException("장바구니 상품을 찾을 수 없습니다.");
        }

        return new CartItemDeleteResponse(cartItemId, "장바구니 상품이 삭제되었습니다.");


    }

    private Cart findOrCreateCart(Long memberId) {
        return cartMapper.findCartByMemberId(memberId)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(memberId);
                    cartMapper.saveCart(newCart);
                    return newCart;
                });
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
    }

    private void validateProductForCart(Product product) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ProductNotFoundException("판매 중인 상품이 아닙니다.");
        }

        if (product.getStockQuantity() <= 0) {
            throw new StockQuantityExceedException("품절된 상품입니다.");
        }
    }

    private void increaseCartItemQuantity(CartItem cartItem, Product product, int quantity) {

        int nextQuantity = cartItem.getQuantity() + quantity;

        validateStockQuantity(product, nextQuantity);

        cartMapper.increaseQuantity(cartItem.getCartItemId(), quantity);

    }

    private void saveNewCartItem(Cart cart, Product product, int quantity) {
        validateStockQuantity(product, quantity);

        CartItem cartItem = CartItem.create(cart.getCartId(), product.getProductId(), quantity);

        cartMapper.saveCartItem(cartItem);
    }

    private void validateStockQuantity(Product product, int quantity) {
        if (product.getStockQuantity() < quantity) {
            throw new StockQuantityExceedException("재고 수량을 초과하였습니다.");
        }
    }

}
