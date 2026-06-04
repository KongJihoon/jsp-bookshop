package hello.bookshop.member.service;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.MemberLoginFailedException;
import hello.bookshop.common.exception.member.MemberNotFoundException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.MemberInfoResponse;
import hello.bookshop.member.dto.MemberUpdateRequest;
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

    /**
     * 유저 회원가입
     */
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

    /**
     * 유저 로그인
     */
    @Transactional
    public SessionMemberDto loginMember(String loginId, String password) {

        Member member = memberMapper.findByLoginIdAndWithdrawnAtIsNull(loginId)
                .orElseThrow(MemberLoginFailedException::new);

        String encodedPassword = member.getPassword();

        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new MemberLoginFailedException();
        }



        return new SessionMemberDto(member);

    }

    /**
     * 회원정보 조회
     */
    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberDetails(Long memberId) {


        return memberMapper.findByIdAndWithdrawnAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

    /**
     * 회원정보 수정
     */

    @Transactional
    public void updateMemberInfo(Long memberId, MemberUpdateRequest request) {

        if (memberMapper.existsByEmailAndMemberIdNot(request.getEmail(), memberId)) {
            throw new DuplicateMemberException("email", "이메일");
        }

        Member member = memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.updateMemberInfo(request.getEmail(), request.getPhone(), request.getZipcode(), request.getAddress(), request.getAddressDetail());


        memberMapper.update(member);

    }


    /**
     * 중복 로그인 아이디 검사
     */
    @Transactional(readOnly = true)
    public boolean existsByLoginId(String loginId) {


        return memberMapper.existsByLoginId(loginId);
    }
    /**
     * 중복 이메일 검사
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {


        return memberMapper.existsByEmail(email);
    }




    /**
     * 회원가입 검증 로직
     */
    private static void validateDuplicate(boolean existsByLoginId, boolean existsByEmail) {
        if (existsByLoginId) {
            throw new DuplicateMemberException("loginId", "아이디");
        }

        if (existsByEmail) {
            throw new DuplicateMemberException("email", "이메일");
        }
    }


}
