package hello.bookshop.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class PasswordResetCodeService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;

    @Value("${bookshop.password-reset.code-ttl-minutes}")
    private long codeTtlMinutes;

    @Value("${bookshop.password-reset.verified-ttl-minutes}")
    private long verifiedTtlMinutes;

    public String createCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public void saveCode(String loginId, String email, String code) {

        redisTemplate.opsForValue().set(
                codeKey(loginId, email),
                code,
                Duration.ofMinutes(codeTtlMinutes)
        );

    }

    public boolean verifyCode(String loginId, String email, String code) {
        String savedCode = redisTemplate.opsForValue().get(codeKey(loginId, email));

        return savedCode != null && savedCode.equals(code);
    }

    public void markVerified(String loginId, String email) {
        redisTemplate.opsForValue().set(
                verifiedKey(loginId, email),
                "true",
                Duration.ofMinutes(verifiedTtlMinutes)
        );
    }

    public boolean isVerified(String loginId, String email) {
        return redisTemplate.hasKey(verifiedKey(loginId, email));
    }

    public long getCodeTtlSecond(String loginId, String email) {

        Long expire = redisTemplate.getExpire(codeKey(loginId, email));

        if (expire == null || expire < 0) {
            return 0;
        }

        return expire;
    }

    public void deleteCode(String loginId, String email) {
        redisTemplate.delete(codeKey(loginId, email));
    }

    public void deleteAll(String loginId, String email) {
        redisTemplate.delete(codeKey(loginId, email));
        redisTemplate.delete(verifiedKey(loginId, email));
    }



    private String codeKey(String loginId, String email) {
        return "password-reset:code:" + loginId + ":" + email;
    }

    private String verifiedKey(String loginId, String email) {
        return "password-reset:verified:" + loginId + ":" + email;
    }

}
