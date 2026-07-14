package hello.bookshop.member.service;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendPasswordResetCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("[BookShop] 비밀번호 재설정 인증번호");
        message.setText(
                "BookShop 비밀번호 재설정 인증번호입니다.\n\n" +
                        "인증번호: " + code + "\n\n" +
                        "인증번호는 3분간 유효합니다."
        );

        mailSender.send(message);
    }
}
