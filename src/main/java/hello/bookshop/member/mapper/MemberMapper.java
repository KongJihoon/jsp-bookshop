package hello.bookshop.member.mapper;

import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.MemberInfoResponse;
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

}
