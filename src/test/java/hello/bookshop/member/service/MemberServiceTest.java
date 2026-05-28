package hello.bookshop.member.service;

import hello.bookshop.common.exception.member.DuplicateMemberException;
import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.MemberSignUpRequest;
import hello.bookshop.member.mapper.MemberMapper;
import hello.bookshop.member.validator.MemberValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

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
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
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