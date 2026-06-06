package hello.bookshop.admin.service;

import hello.bookshop.admin.dto.AdminLoginRequest;
import hello.bookshop.common.exception.admin.AdminLoginFailedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.SessionMemberDto;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.member.type.MemberType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MemberMapper memberMapper;

    private final PasswordEncoder passwordEncoder;

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


}
