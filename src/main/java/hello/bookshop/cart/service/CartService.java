package hello.bookshop.cart.service;


import hello.bookshop.cart.domain.Cart;
import hello.bookshop.cart.domain.CartItem;
import hello.bookshop.cart.mapper.CartMapper;
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

@Service
@RequiredArgsConstructor
public class CartService {

    private final MemberMapper memberMapper;

    private final ProductMapper productMapper;

    private final CartMapper cartMapper;

    // 세션 로그인 상태 확인 -> 비로그인 유저 로그인 화면으로 리다이렉트
    // Cart 존재 확인 후 Cart 생성
    // 상품 존재 확인 -> 서비스 내부 검증도 필요하기 때문에 구현
    // 재고 확인? Product.quantity -> quantity 매게변수랑 비교 검증?
    // 검증 통과 시 CartItem save();


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

    private Cart findOrCreateCart(Long memberId) {
        return cartMapper.findCartByMemberId(memberId)
                .orElseGet(() -> {
                    Cart newCart = Cart.create(memberId);
                    cartMapper.saveCart(newCart);
                    return newCart;
                });
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
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
