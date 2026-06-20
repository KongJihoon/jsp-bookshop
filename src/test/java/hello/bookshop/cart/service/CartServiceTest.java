package hello.bookshop.cart.service;

import hello.bookshop.cart.domain.Cart;
import hello.bookshop.cart.domain.CartItem;
import hello.bookshop.cart.mapper.CartMapper;
import hello.bookshop.common.exception.product.StockQuantityExceedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.product.domain.Product;
import hello.bookshop.product.mapper.ProductMapper;
import hello.bookshop.product.type.ProductStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니 담기 성공 - 장바구니가 없으면 Cart 생성 후 CartItem 저장")
    void addCartItem_success_createCartAndSaveCartItem() {
        // given

        Long memberId = 1L;
        Long productId = 10L;
        int quantity = 2;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 10, ProductStatus.ACTIVE);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(cartMapper.findCartByMemberId(memberId))
                .thenReturn(Optional.empty());

        doAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            ReflectionTestUtils.setField(cart, "cartId", 100L);
            return null;
        }).when(cartMapper).saveCart(any(Cart.class));

        // when

        cartService.addCartItem(memberId, productId, quantity);

        // then
        verify(cartMapper).saveCart(any(Cart.class));

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartMapper).saveCartItem(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertThat(savedCartItem.getCartId()).isEqualTo(100L);
        assertThat(savedCartItem.getProductId()).isEqualTo(productId);
        assertThat(savedCartItem.getQuantity()).isEqualTo(quantity);

        verify(cartMapper, never()).increaseQuantity(anyLong(), anyInt());
    }

    @Test
    @DisplayName("장바구니 담기 실패 - 기존 수량과 추가 수량 합계가 재고를 초과")
    void addCartItem_fail_exceedStockWithExistingQuantity() {
        // given

        Long memberId = 1L;
        Long productId = 10L;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 10, ProductStatus.ACTIVE);
        Cart cart = createCart(100L, memberId);
        CartItem cartItem = createCartItem(1000L, cart.getCartId(), productId, 8);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(cartMapper.findCartByMemberId(memberId))
                .thenReturn(Optional.of(cart));

        when(cartMapper.findCartItemByCartIdAndProductId(cart.getCartId(), productId))
                .thenReturn(Optional.of(cartItem));
        // when

        // then

        assertThatThrownBy(() -> cartService.addCartItem(memberId, productId, 5))
                .isInstanceOf(StockQuantityExceedException.class)
                .hasMessage("재고 수량을 초과하였습니다.");

        verify(cartMapper, never()).increaseQuantity(anyLong(), anyInt());
        verify(cartMapper, never()).saveCartItem(any(CartItem.class));


    }

    @Test
    @DisplayName("장바구니 담기 실패 - 재고가 없는 상품")
    void addCartItem_fail_zeroStockProduct() {
        // given
        Long memberId = 1L;
        Long productId = 10L;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 0, ProductStatus.ACTIVE);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> cartService.addCartItem(memberId, productId, 1))
                .isInstanceOf(StockQuantityExceedException.class)
                .hasMessage("품절된 상품입니다.");

        verify(cartMapper, never()).saveCart(any(Cart.class));
        verify(cartMapper, never()).saveCartItem(any(CartItem.class));
    }

    @Test
    @DisplayName("장바구니 담기 성공 - 이미 담긴 상품이면 수량 증가")
    void addCartItem_success_increaseQuantity() {
        // given
        Long memberId = 1L;
        Long productId = 10L;
        int quantity = 3;

        Member member = createMember(memberId);
        Product product = createProduct(productId, 10, ProductStatus.ACTIVE);

        Cart cart = createCart(100L, memberId);

        CartItem cartItem = createCartItem(1000L, cart.getCartId(), productId, 2);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(productMapper.findByProductIdAndDeletedAtIsNull(productId))
                .thenReturn(Optional.of(product));

        when(cartMapper.findCartByMemberId(memberId))
                .thenReturn(Optional.of(cart));

        when(cartMapper.findCartItemByCartIdAndProductId(cart.getCartId(), productId))
                .thenReturn(Optional.of(cartItem));
        // when

        cartService.addCartItem(memberId, productId, quantity);

        // then

        verify(cartMapper).increaseQuantity(cartItem.getCartItemId(), quantity);
        verify(cartMapper, never()).saveCart(any(Cart.class));
        verify(cartMapper, never()).saveCartItem(any(CartItem.class));

    }

    private Member createMember(Long memberId) {
        Member member = Member.signUp(
                "test",
                "password",
                "홍길동",
                "test@test.com",
                "01012345678",
                "12345",
                "주소",
                "상세주소"
        );

        ReflectionTestUtils.setField(member, "memberId", memberId);
        return member;
    }

    private Product createProduct(Long productId, int stockQuantity, ProductStatus status) {
        Product product = Product.create(
                1L,
                "자바의 정석",
                "남궁성",
                "도우출판",
                30000,
                stockQuantity,
                "자바 기본서",
                1L
        );

        ReflectionTestUtils.setField(product, "productId", productId);
        ReflectionTestUtils.setField(product, "status", status);

        return product;
    }

    private Cart createCart(Long cartId, Long memberId) {
        Cart cart = Cart.create(memberId);

        ReflectionTestUtils.setField(cart, "cartId", cartId);

        return cart;
    }

    private CartItem createCartItem(Long cartItemId, Long cartId, Long productId, int quantity) {
        CartItem cartItem = CartItem.create(cartId, productId, quantity);

        ReflectionTestUtils.setField(cartItem, "cartItemId", cartItemId);

        return cartItem;
    }

}