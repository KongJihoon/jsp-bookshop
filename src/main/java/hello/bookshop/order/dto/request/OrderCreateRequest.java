package hello.bookshop.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreateRequest {

    @NotEmpty(message = "주문할 상품을 선택해주세요.")
    private List<Long> cartItemIds;

    @NotBlank(message = "받는 사람을 입력해주세요.")
    private String receiverName;

    @NotBlank(message = "연락처를 입력해주세요.")
    private String receiverPhone;

    @NotBlank(message = "우편번호를 입력해주세요.")
    private String zipcode;

    @NotBlank(message = "주소를 입력해주세요.")
    private String address;

    @NotBlank(message = "상세 주소를 입력해주세요.")
    private String addressDetail;

}
