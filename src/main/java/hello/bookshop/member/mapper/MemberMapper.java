package hello.bookshop.member.mapper;

import hello.bookshop.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    void save(Member member);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);
}
