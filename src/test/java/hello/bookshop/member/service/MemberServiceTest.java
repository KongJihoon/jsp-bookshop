package hello.bookshop.member.service;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.common.exception.member.MemberLoginFailedException;
import hello.bookshop.common.exception.member.MemberNotFoundException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.response.MemberInfoResponse;
import hello.bookshop.member.dto.request.MemberSignUpRequest;
import hello.bookshop.member.dto.request.MemberUpdateRequest;
import hello.bookshop.member.dto.response.SessionMemberDto;
import hello.bookshop.member.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    void encodePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin@123";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println(encodedPassword);
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