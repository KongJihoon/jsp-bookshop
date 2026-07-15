package hello.bookshop.admin.service;

import hello.bookshop.admin.dto.request.AdminLoginRequest;
import hello.bookshop.admin.dto.response.AdminDashboardResponse;
import hello.bookshop.common.exception.admin.AdminLoginFailedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.member.type.MemberType;
import hello.bookshop.order.mapper.OrderMapper;
import hello.bookshop.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberMapper memberMapper;

    private final PasswordEncoder passwordEncoder;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;

    public SessionMemberDto loginAdmin(AdminLoginRequest request) {

        Member member = memberMapper.findByLoginIdAndWithdrawnAtIsNull(request.getLoginId())
                .orElseThrow(() -> new AdminLoginFailedException("아이디 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new AdminLoginFailedException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        if (member.getMemberType() != MemberType.ADMIN) {
            throw new AdminLoginFailedException("관리자 권한이 없습니다.");
        }


        return new SessionMemberDto(member);
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashBoard() {

        long totalProductCount = productMapper.countAllProducts();

        long totalMemberCount = memberMapper.countAllUsers();

        long todayOrderCount = orderMapper.countTodayOrders();

        long soldOutProductCount = productMapper.countSoldOutProducts();

        return new AdminDashboardResponse(totalProductCount, totalMemberCount, todayOrderCount, soldOutProductCount);

    }


}
