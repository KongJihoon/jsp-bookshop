package hello.bookshop.member.mapper;

import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.response.MemberFindIdResponse;
import hello.bookshop.member.dto.response.MemberInfoResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    void save(Member member);

    void update(Member member);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Optional<Member> findByLoginIdAndWithdrawnAtIsNull(String loginId);

    Optional<MemberInfoResponse> findByIdAndWithdrawnAtIsNull(Long memberId);

    Optional<Member> findMemberByIdAndWithdrawnAtIsNull(Long memberId);

    boolean existsByEmailAndMemberIdNot(@Param("email") String email, @Param("memberId") Long memberId);

    long countAllUsers();

    int withdraw(Member member);

    Optional<MemberFindIdResponse> findLoginIdByNameEmailPhone(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone
    );

    Optional<Member> findByLoginIdAndEmailAndWithdrawnAtIsNull(
            @Param("loginId") String loginId,
            @Param("email") String email
    );

    int updatePassword(
            @Param("memberId") Long memberId,
            @Param("password") String password
    );
}
