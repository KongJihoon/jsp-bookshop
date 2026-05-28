package hello.bookshop.member.service;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.member.domain.Member;
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

        String encodedPassword = passwordEncoder.encode(request.getPassword());


        validateDuplicate(existsByLoginId, existsByEmail);


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

    private static void validateDuplicate(boolean existsByLoginId, boolean existsByEmail) {
        if (existsByLoginId) {
            throw new DuplicateMemberException("아이디");
        }

        if (existsByEmail) {
            throw new DuplicateMemberException("이메일");
        }
    }


}
