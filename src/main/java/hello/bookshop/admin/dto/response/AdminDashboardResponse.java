package hello.bookshop.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminDashboardResponse {

    private long totalProductCount;

    private long totalMemberCount;

    private long todayOrderCount;

    private long soldOutProductCount;

}
