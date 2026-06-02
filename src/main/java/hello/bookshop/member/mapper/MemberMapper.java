package hello.bookshop.member.mapper;

import hello.bookshop.member.domain.Member;
import hello.bookshop.member.dto.MemberInfoResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {

    void save(Member member);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    Optional<Member> findByLoginIdAndWithdrawnAtIsNull(String loginId);

    Optional<MemberInfoResponse> findByIdAndWithdrawnAtIsNull(Long memberId);


}
