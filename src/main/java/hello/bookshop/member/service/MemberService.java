package hello.bookshop.member.service;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.LoginFailedException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.SessionMemberDto;
import hello.bookshop.member.dto.MemberSignUpRequest;
import hello.bookshop.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(MemberSignUpRequest request) {
        // 회원 가입 로직 구현


        boolean existsByEmail = memberMapper.existsByEmail(request.getEmail());

        boolean existsByLoginId = memberMapper.existsByLoginId(request.getLoginId());



        validateDuplicate(existsByLoginId, existsByEmail);

        String encodedPassword = passwordEncoder.encode(request.getPassword());


        Member member = Member.signUp(
                request.getLoginId(),
                encodedPassword,
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getZipcode(),
                request.getAddress(),
                request.getAddressDetail()
        );

        memberMapper.save(member);

    }

    public SessionMemberDto loginMember(String loginId, String password) {

        Member member = memberMapper.findByLoginIdAndWithdrawnAtIsNull(loginId)
                .orElseThrow(LoginFailedException::new);

        String encodedPassword = member.getPassword();

        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new LoginFailedException();
        }



        return new SessionMemberDto(member);

    }

    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {


        return memberMapper.existsByLoginId(loginId);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {


        return memberMapper.existsByEmail(email);
    }

    private static void validateDuplicate(boolean existsByLoginId, boolean existsByEmail) {
        if (existsByLoginId) {
            throw new DuplicateMemberException("loginId", "아이디");
        }

        if (existsByEmail) {
            throw new DuplicateMemberException("email", "이메일");
        }
    }


}
