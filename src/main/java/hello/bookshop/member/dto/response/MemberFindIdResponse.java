package hello.bookshop.member.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberFindIdResponse {

    private String loginId;

    public String getMaskedLoginId() {
        if (loginId == null || loginId.isBlank()) {
            return "";
        }

        if (loginId.length() <= 3) {
            return loginId.charAt(0) + "**";
        }

        return loginId.substring(0,3) + "*".repeat(loginId.length() - 3);
    }
}
