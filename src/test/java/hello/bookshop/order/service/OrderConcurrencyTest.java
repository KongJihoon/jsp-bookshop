package hello.bookshop.order.service;

import hello.bookshop.order.dto.request.OrderCreateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Long CATEGORY_ID = 900001L;
    private static final Long PRODUCT_ID = 900001L;

    private static final int STOCK_QUANTITY = 5;
    private static final int THREAD_COUNT = 10;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM order_item WHERE product_id = ?", PRODUCT_ID);
        jdbcTemplate.update("DELETE FROM orders WHERE member_id BETWEEN ? AND ?", 910001L, 910100L);
        jdbcTemplate.update("DELETE FROM cart_item WHERE product_id = ?", PRODUCT_ID);
        jdbcTemplate.update("DELETE FROM cart WHERE member_id BETWEEN ? AND ?", 910001L, 910100L);
        jdbcTemplate.update("DELETE FROM product WHERE product_id = ?", PRODUCT_ID);
        jdbcTemplate.update("DELETE FROM category WHERE category_id = ?", CATEGORY_ID);
        jdbcTemplate.update("DELETE FROM member WHERE member_id BETWEEN ? AND ?", 910001L, 910100L);
    }

    @Test
    @DisplayName("재고보다 많은 주문이 동시에 발생 시 동시성 이슈 테스트")
    void concurrentOrder_stockDoesNotGoNegative() throws InterruptedException {
        // given

        prepareTestData();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        CountDownLatch readyLatch = new CountDownLatch(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < THREAD_COUNT; i++) {

            final int index = i;

            executorService.submit(() -> {
                try {
                    Long memberId = 910001L + index;
                    Long cartItemId = 930001L + index;

                    OrderCreateRequest request = createOrderCreateRequest(List.of(cartItemId));

                    readyLatch.countDown();
                    startLatch.await();

                    orderService.createReadyCartOrder(memberId, request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });

        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        executorService.shutdown();
        // when

        // then
        Integer finalStock = jdbcTemplate.queryForObject(
                "SELECT stock_quantity FROM product WHERE product_id = ?",
                Integer.class,
                PRODUCT_ID
        );

        Integer orderCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE member_id BETWEEN ? AND ?",
                Integer.class,
                910001L,
                910100L
        );

        Integer orderItemCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM order_item WHERE product_id = ?",
                Integer.class,
                PRODUCT_ID
        );

        Integer remainingCartItemCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM cart_item WHERE product_id = ?",
                Integer.class,
                PRODUCT_ID
        );

        assertThat(successCount.get()).isEqualTo(STOCK_QUANTITY);
        assertThat(failCount.get()).isEqualTo(THREAD_COUNT - STOCK_QUANTITY);

        assertThat(finalStock).isEqualTo(0);
        assertThat(orderCount).isEqualTo(STOCK_QUANTITY);
        assertThat(orderItemCount).isEqualTo(STOCK_QUANTITY);
        assertThat(remainingCartItemCount).isEqualTo(THREAD_COUNT - STOCK_QUANTITY);

    }




    private void prepareTestData() {
        tearDown();

        jdbcTemplate.update("""
            INSERT INTO category (
                category_id,
                parent_id,
                category_name,
                category_status,
                created_at,
                updated_at
            ) VALUES (
                ?, NULL, '동시성 테스트 카테고리', 'ACTIVE', NOW(), NOW()
            )
            """, CATEGORY_ID);

        for (int i = 0; i < THREAD_COUNT; i++) {
            Long memberId = 910001L + i;

            jdbcTemplate.update("""
                INSERT INTO member (
                    member_id,
                    login_id,
                    password,
                    name,
                    email,
                    phone,
                    zipcode,
                    address,
                    address_detail,
                    member_type,
                    created_at,
                    updated_at
                ) VALUES (
                    ?, ?, 'password', ?, ?, '010-0000-0000',
                    '12345', '서울시 강남구', '101호',
                    'USER', NOW(), NOW()
                )
                """,
                    memberId,
                    "order_test_" + i,
                    "테스트회원" + i,
                    "order_test_" + i + "@test.com"
            );
        }

        jdbcTemplate.update("""
            INSERT INTO product (
                product_id,
                category_id,
                name,
                author,
                publisher,
                price,
                stock_quantity,
                description,
                status,
                created_by,
                updated_by,
                created_at,
                updated_at
            ) VALUES (
                ?, ?, '동시성 테스트 도서', '테스터', '테스트출판사',
                10000, ?, '동시성 테스트용 상품', 'ACTIVE',
                ?, ?, NOW(), NOW()
            )
            """,
                PRODUCT_ID,
                CATEGORY_ID,
                STOCK_QUANTITY,
                910001L,
                910001L
        );

        for (int i = 0; i < THREAD_COUNT; i++) {
            Long memberId = 910001L + i;
            Long cartId = 920001L + i;
            Long cartItemId = 930001L + i;

            jdbcTemplate.update("""
                INSERT INTO cart (
                    cart_id,
                    member_id,
                    created_at,
                    updated_at
                ) VALUES (
                    ?, ?, NOW(), NOW()
                )
                """, cartId, memberId);

            jdbcTemplate.update("""
                INSERT INTO cart_item (
                    cart_item_id,
                    cart_id,
                    product_id,
                    quantity,
                    created_at,
                    updated_at
                ) VALUES (
                    ?, ?, ?, 1, NOW(), NOW()
                )
                """, cartItemId, cartId, PRODUCT_ID);
        }
    }

    private OrderCreateRequest createOrderCreateRequest(List<Long> cartItemIds) {
        OrderCreateRequest request = new OrderCreateRequest();
        request.setCartItemIds(cartItemIds);
        request.setReceiverName("공지훈");
        request.setReceiverPhone("010-1234-5678");
        request.setZipcode("12345");
        request.setAddress("서울시 강남구");
        request.setAddressDetail("101호");
        return request;
    }
}
