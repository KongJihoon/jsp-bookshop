package hello.bookshop.member.service;

import hello.bookshop.cart.mapper.CartMapper;
import hello.bookshop.common.exception.member.*;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.request.MemberFindIdRequest;
import hello.bookshop.member.dto.response.MemberFindIdResponse;
import hello.bookshop.member.dto.response.MemberInfoResponse;
import hello.bookshop.member.dto.request.MemberSignUpRequest;
import hello.bookshop.member.dto.request.MemberUpdateRequest;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.order.mapper.OrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private PasswordResetCodeService passwordResetCodeService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private MemberService memberService;


    @Test
    @DisplayName("회원가입 저장 및 비밀번호 암호화 성공 테스트")
    void signup_success() {
        // given

        MemberSignUpRequest request = createSignupRequest();

        when(memberMapper.existsByEmail(request.getEmail())).thenReturn(false);

        when(memberMapper.existsByLoginId(request.getLoginId())).thenReturn(false);

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");


        // when

        memberService.signUp(request);
        // then

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberMapper).save(captor.capture());

        Member savedMember = captor.getValue();

        assertThat(savedMember.getLoginId())
                .isEqualTo(request.getLoginId());
        assertThat(savedMember.getPassword())
                .isEqualTo("encodedPassword");
        assertThat(savedMember.getEmail())
                .isEqualTo(request.getEmail());



    }

    @Test
    @DisplayName("중복 로그인 ID - DuplicateMemberException 발생")
    void signup_duplicateLoginId_throwException() {
        // given

        MemberSignUpRequest request = createSignupRequest();

        when(memberMapper.existsByEmail(request.getEmail())).thenReturn(false);
        when(memberMapper.existsByLoginId(request.getLoginId())).thenReturn(true);

        // when

        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(DuplicateMemberException.class)
                .hasMessage("아이디은(는) 이미 사용 중입니다.");

        // then

        verify(memberMapper, never()).save(any());

    }

    @Test
    @DisplayName("중복 이메일 - DuplicatedMemberException 발생")
    void signup_duplicateEmail_throwException() {
        // given

        MemberSignUpRequest request = createSignupRequest();

        when(memberMapper.existsByEmail(request.getEmail())).thenReturn(true);
        when(memberMapper.existsByLoginId(request.getLoginId())).thenReturn(false);
        // when

        assertThatThrownBy(() -> memberService.signUp(request))
                .isInstanceOf(DuplicateMemberException.class)
                .hasMessage("이메일은(는) 이미 사용 중입니다.");

        // then


        verify(memberMapper, never()).save(any());

    }

    @Test
    @DisplayName("비밀번호 암호화 검증")
    void signup_passwordEncodingVerification() {
        // given

        MemberSignUpRequest request = createSignupRequest();

        String password = request.getPassword();
        String encodedPassword = "encodedPassword";

        when(memberMapper.existsByEmail(request.getEmail())).thenReturn(false);
        when(memberMapper.existsByLoginId(request.getLoginId())).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // when

        memberService.signUp(request);

        // then
        verify(passwordEncoder).encode(password);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberMapper).save(captor.capture());
        assertThat(captor.getValue().getPassword())
                .isEqualTo(encodedPassword)
                .isNotEqualTo(password);

    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given

        String loginId = "test1";
        String password = "test123!";

        Member member = Member.signUp(
                loginId,
                "encodedPassword",
                "홍길동",
                "test@test.com",
                "01012341234",
                "12345",
                "주소",
                "상세주소"
        );


        when(memberMapper.findByLoginIdAndWithdrawnAtIsNull(loginId))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(password, "encodedPassword"))
                .thenReturn(true);
        // when

        SessionMemberDto loginMember = memberService.loginMember(loginId, password);


        // then

        assertThat(loginMember).isNotNull();
        assertThat(loginMember.getLoginId()).isEqualTo(loginId);

        verify(passwordEncoder)
                .matches(password, "encodedPassword");

    }

    @Test
    @DisplayName("존재하지 않는 아이디")
    void login_fail_notFoundMember() {
        // given

        String loginId = "test123";
        String password = "test@123";

        // when

        assertThatThrownBy(
                () -> memberService.loginMember(loginId, password)
        ).isInstanceOf(MemberLoginFailedException.class);

        // then

        verify(passwordEncoder, never()).matches(any(), any());

    }

    @Test
    @DisplayName("비밀번호 불일치")
    void login_fail_passwordMismatch() {
        // given
        String loginId = "test1";
        String password = "wrongPassword";

        Member member = Member.signUp(
                loginId,
                "encodedPassword",
                "홍길동",
                "test@test.com",
                "01012341234",
                "12345",
                "주소",
                "상세주소"
        );

        when(memberMapper.findByLoginIdAndWithdrawnAtIsNull(loginId))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(password, "encodedPassword"))
                .thenReturn(false);
        // when

        assertThatThrownBy(
                () -> memberService.loginMember(loginId, password)
        ).isInstanceOf(MemberLoginFailedException.class);


        // then
        verify(passwordEncoder).matches(password, "encodedPassword");
    }

    @Test
    @DisplayName("회원정보 조회 성공")
    void getMemberDetails_success() {
        // given

        Long memberId = 1L;

        MemberInfoResponse response = new MemberInfoResponse();

        response.setLoginId("test1");
        response.setName("홍길동");
        response.setEmail("test@test.com");
        response.setPhone("010-1234-5678");
        response.setZipcode("12345");
        response.setAddress("인천시 계양구");
        response.setAddressDetail("상세주소");

        when(memberMapper.findByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(response));

        // when

        MemberInfoResponse result = memberService.getMemberDetails(memberId);

        // then
        assertThat(result).isNotNull();

        assertThat(result.getLoginId()).isEqualTo("test1");
        assertThat(result.getEmail()).isEqualTo("test@test.com");

    }

    @Test
    @DisplayName("회원정보 조회 실패 - 존재하지 않는 회원")
    void getMemberDetails_fail_memberNotFound() {
        // given

        Long memberId = 1L;

        when(memberMapper.findByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.empty());

        // when

        assertThatThrownBy(() -> memberService.getMemberDetails(memberId))
                .isInstanceOf(MemberNotFoundException.class);

        // then
        verify(memberMapper).findByIdAndWithdrawnAtIsNull(memberId);

    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberInfo_success() {

        // given

        Long memberId = 1L;

        MemberUpdateRequest request = new MemberUpdateRequest();
        request.setEmail("test@test.test");
        request.setPhone("010-1111-2222");
        request.setZipcode("12345");
        request.setAddress("테스트용 주소");
        request.setAddressDetail("테스트용 상세주소");

        Member member = Member.signUp(
                "testId",
                "encodedPassword",
                "홍길동",
                "old@test.com",
                "010-0000-0000",
                "00000",
                "기존 주소",
                "기존 상세주소"
        );


        when(memberMapper.existsByEmailAndMemberIdNot(request.getEmail(), memberId))
                .thenReturn(false);

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        // when

        memberService.updateMemberInfo(memberId, request);



        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        verify(memberMapper).update(captor.capture());

        Member updateMember = captor.getValue();

        assertThat(request.getEmail()).isEqualTo(updateMember.getEmail());
        assertThat(request.getPhone()).isEqualTo(updateMember.getPhone());
        assertEquals("12345", updateMember.getZipcode());
        assertEquals("테스트용 주소", updateMember.getAddress());
        assertEquals("테스트용 상세주소", updateMember.getAddressDetail());


        verify(memberMapper).existsByEmailAndMemberIdNot(request.getEmail(), memberId);
        verify(memberMapper).findMemberByIdAndWithdrawnAtIsNull(memberId);

    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void withdrawMember_success() {
        // given

        Long memberId = 1L;

        String rawPassword = "test123";
        String encodedPassword = "encodedPassword";
        Member member = Member.signUp(
                "testId",
                encodedPassword,
                "홍길동",
                "test@test.com",
                "010-1111-2222",
                "12345",
                "테스트 주소",
                "테스트 상세주소"
        );

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        when(orderMapper.existsByActiveOrderByMemberId(memberId))
                .thenReturn(false);

        when(cartMapper.deleteCartItemByMemberId(memberId))
                .thenReturn(2);

        when(memberMapper.withdraw(member))
                .thenReturn(1);

        // when

        memberService.withdrawMember(memberId, rawPassword);

        // then

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        verify(memberMapper).withdraw(captor.capture());

        Member withdrawnMember = captor.getValue();

        assertThat(withdrawnMember.getWithdrawnAt()).isNotNull();

        verify(memberMapper).findMemberByIdAndWithdrawnAtIsNull(memberId);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(orderMapper).existsByActiveOrderByMemberId(memberId);
        verify(cartMapper).deleteCartItemByMemberId(memberId);


    }

    @Test
    @DisplayName("회원탈퇴 실패 - 비밀번호 불일치")
    void withdrawMember_fail_passwordMismatch() {
        // given

        Long memberId = 1L;
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword";

        Member member = Member.signUp(
                "testId",
                encodedPassword,
                "홍길동",
                "test@test.com",
                "010-1111-2222",
                "12345",
                "테스트 주소",
                "테스트 상세주소"
        );

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(false);

        // when

        // then

        assertThatThrownBy(() -> memberService.withdrawMember(memberId, rawPassword))
                .isInstanceOf(MemberPasswordMismatchException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");

        verify(memberMapper).findMemberByIdAndWithdrawnAtIsNull(memberId);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);

        verify(orderMapper, never()).existsByActiveOrderByMemberId(memberId);
        verify(cartMapper, never()).deleteCartItemByMemberId(memberId);
        verify(memberMapper, never()).withdraw(member);

    }

    @Test
    @DisplayName("회원탈퇴 실패 - 진행 중인 주문 존재시 예외 발생")
    void withdrawMember_fail_activeOrderExists() {
        // given

        Long memberId = 1L;
        String rawPassword = "test123";
        String encodedPassword = "encodedPassword";

        Member member = Member.signUp(
                "testId",
                encodedPassword,
                "홍길동",
                "test@test.com",
                "010-1111-2222",
                "12345",
                "테스트 주소",
                "테스트 상세주소"
        );

        when(memberMapper.findMemberByIdAndWithdrawnAtIsNull(memberId))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        when(orderMapper.existsByActiveOrderByMemberId(memberId))
                .thenReturn(true);


        // when

        // then

        assertThatThrownBy(() -> memberService.withdrawMember(memberId,rawPassword))
                .isInstanceOf(MemberWithdrawNotAllowedException.class)
                .hasMessage("진행 중인 주문이 존재하여 현재 회원탈퇴가 불가합니다.");

        verify(memberMapper).findMemberByIdAndWithdrawnAtIsNull(memberId);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(orderMapper).existsByActiveOrderByMemberId(memberId);
        verify(cartMapper, never()).deleteCartItemByMemberId(anyLong());
        verify(memberMapper, never()).withdraw(any(Member.class));

    }

    @Test
    @DisplayName("아이디 찾기 성공")
    void findLoginId_success() {
        // given

        MemberFindIdRequest request = new MemberFindIdRequest();

        request.setName("test");
        request.setEmail("test@test.com");
        request.setPhone("010-0000-0000");

        MemberFindIdResponse response = new MemberFindIdResponse();
        response.setLoginId("testLoginId");

        when(memberMapper.findLoginIdByNameEmailPhone(
                request.getName(),
                request.getEmail(),
                request.getPhone()
        )).thenReturn(Optional.of(response));

        // when

        MemberFindIdResponse result = memberService.findLoginId(request);

        // then

        assertThat(result.getLoginId()).isEqualTo("testLoginId");
        assertThat(result.getMaskedLoginId()).isEqualTo("tes********");

        verify(memberMapper).findLoginIdByNameEmailPhone(
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );

    }

    @Test
    @DisplayName("아이디 찾기 실패 - 일치하는 회원이 존재하지 않을 경우 예외 처리")
    void findLoginId_fail_memberNotFound() {
        // given
        MemberFindIdRequest request = new MemberFindIdRequest();
        request.setName("공지훈");
        request.setEmail("wrong@test.com");
        request.setPhone("010-1234-5678");

        when(memberMapper.findLoginIdByNameEmailPhone(
                request.getName(),
                request.getEmail(),
                request.getPhone()
        )).thenReturn(Optional.empty());
        // when

        // then
        assertThatThrownBy(() -> memberService.findLoginId(request))
                .isInstanceOf(MemberFindException.class)
                .hasMessage("일치하는 회원 정보를 찾을 수 없습니다.");

        verify(memberMapper).findLoginIdByNameEmailPhone(
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );

    }

    @Test
    @DisplayName("비밀번호 찾기 인증번호 발송 성공")
    void sendPasswordResetCode_success() {
        // given
        String loginId = "testLoginId";
        String email = "test@test.com";
        String code = "123456";

        Member member = Member.signUp(
                loginId,
                "encodedPassword",
                "test",
                email,
                "010-1234-5678",
                "12345",
                "테스트 주소",
                "테스트 상세주소"
        );

        when(memberMapper.findByLoginIdAndEmailAndWithdrawnAtIsNull(loginId, email))
                .thenReturn(Optional.of(member));
        when(passwordResetCodeService.createCode())
                .thenReturn(code);


        // when
        memberService.sendPasswordResetCode(loginId, email);

        // then

        verify(memberMapper).findByLoginIdAndEmailAndWithdrawnAtIsNull(loginId, email);
        verify(passwordResetCodeService).createCode();
        verify(passwordResetCodeService).saveCode(loginId, email, code);
        verify(mailService).sendPasswordResetCode(email, code);

    }

    @Test
    @DisplayName("비밀번호 찾기 인증번호 발송 실패 - 일치하는 회원 존재하지 않을 경우 예외발생")
    void sendPasswordResetCode_fail_memberNotFound() {
        // given
        String loginId = "testLoginId";
        String email = "wrong@test.com";

        when(memberMapper.findByLoginIdAndEmailAndWithdrawnAtIsNull(loginId, email))
                .thenReturn(Optional.empty());
        // when

        // then

        assertThatThrownBy(() -> memberService.sendPasswordResetCode(loginId, email))
                .isInstanceOf(MemberFindException.class)
                .hasMessage("일치하는 회원 정보를 찾을 수 없습니다.");

        verify(memberMapper).findByLoginIdAndEmailAndWithdrawnAtIsNull(loginId, email);
        verify(passwordResetCodeService, never()).createCode();
        verify(passwordResetCodeService, never()).saveCode(anyString(), anyString(), anyString());
        verify(mailService, never()).sendPasswordResetCode(anyString(), anyString());

    }

    @Test
    @DisplayName("비밀번호 찾기 인증번호 검증 성공")
    void verifyPasswordResetCode_success() {
        // given

        String loginId = "testLoginId";
        String email = "test@test.com";
        String code = "123456";

        when(passwordResetCodeService.verifyCode(loginId, email, code))
                .thenReturn(true);

        // when
        memberService.verifyPasswordResetCode(loginId, email, code);

        // then
        verify(passwordResetCodeService).verifyCode(loginId, email, code);
        verify(passwordResetCodeService).markVerified(loginId, email);
        verify(passwordResetCodeService).deleteCode(loginId, email);
    }

    @Test
    @DisplayName("비밀번호 찾기 인증번호 검증 실패")
    void verifyPasswordResetCode_fail_invalidCode() {
        // given
        String loginId = "testLoginId";
        String email = "test@test.com";
        String code = "000000";
        when(passwordResetCodeService.verifyCode(loginId, email, code))
                .thenReturn(false);

        // when


        // then
        assertThatThrownBy(() -> memberService.verifyPasswordResetCode(loginId, email, code))
                .isInstanceOf(MemberFindException.class)
                .hasMessage("인증번호가 올바르지 않거나 만료되었습니다.");

        verify(passwordResetCodeService).verifyCode(loginId, email, code);
        verify(passwordResetCodeService, never()).markVerified(anyString(), anyString());
        verify(passwordResetCodeService, never()).deleteCode(anyString(), anyString());
    }

    @Test
    @DisplayName("비밀번호 재설정 성공")
    void resetPassword_success() {
        // given

        String loginId = "testLoginId";
        String email = "test@test.com";
        String newPassword = "newPassword123!";
        String confirmPassword = "newPassword123!";
        String encodedPassword = "encodedNewPassword";

        Member member = Member.signUp(
                loginId,
                "oldEncodedPassword",
                "test",
                email,
                "010-1234-5678",
                "12345",
                "테스트 주소",
                "테스트 상세주소"
        );

        ReflectionTestUtils.setField(member, "memberId", 1L);

        when(passwordResetCodeService.isVerified(loginId, email))
                .thenReturn(true);

        when(memberMapper.findByLoginIdAndEmailAndWithdrawnAtIsNull(loginId, email))
                .thenReturn(Optional.of(member));

        when(passwordEncoder.encode(newPassword))
                .thenReturn(encodedPassword);

        when(memberMapper.updatePassword(member.getMemberId(), encodedPassword))
                .thenReturn(1);

        // when

        memberService.resetPassword(loginId, email, newPassword, confirmPassword);

        // then

        verify(passwordResetCodeService).isVerified(loginId, email);
        verify(memberMapper).findByLoginIdAndEmailAndWithdrawnAtIsNull(loginId, email);
        verify(passwordEncoder).encode(newPassword);
        verify(memberMapper).updatePassword(member.getMemberId(),encodedPassword);
        verify(passwordResetCodeService).deleteAll(loginId, email);
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 새 비밀번호 불일치")
    void resetPassword_fail_passwordMismatch() {
        // given
        String loginId = "gongjihun";
        String email = "test@test.com";
        String newPassword = "newPassword123!";
        String confirmPassword = "differentPassword123!";

        // when & then
        assertThatThrownBy(() ->
                memberService.resetPassword(loginId, email, newPassword, confirmPassword)
        )
                .isInstanceOf(MemberFindException.class)
                .hasMessage("새 비밀번호가 일치하지 않습니다.");

        verify(passwordResetCodeService, never()).isVerified(anyString(), anyString());
        verify(memberMapper, never()).findByLoginIdAndEmailAndWithdrawnAtIsNull(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(memberMapper, never()).updatePassword(anyLong(), anyString());
    }


    private MemberSignUpRequest createSignupRequest() {

        return MemberSignUpRequest.builder()
                .loginId("test1")
                .password("test123")
                .checkPassword("test123")
                .name("testName")
                .email("test@test.com")
                .phone("010-0000-0000")
                .zipcode("21033")
                .address("인천시 계양구 봉오대로")
                .addressDetail("상세 주소")
                .build();
    }
}
