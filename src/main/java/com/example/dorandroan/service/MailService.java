package com.example.dorandroan.service;

import com.example.dorandroan.dto.EmailAuthRequestDto;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String serverEmail;
    private final JavaMailSender javaMailSender;
    private final MemberRegistrationService memberRegistrationService;
    //private final MailRepository mailRepository;

    public void sendEmail(EmailAuthRequestDto requestDto) throws MessagingException {
        String receiver = requestDto.getEmail();
        boolean findUser = memberRegistrationService.findByEmail(receiver);
        if (requestDto.isSignUp()) {
            if (findUser)
                throw new RestApiException(MemberErrorCode.DUPLICATED_EMAIL);
        } else {
            if (!findUser)
                throw new RestApiException(MemberErrorCode.USER_NOT_FOUND);
        }
        sendSimpleMessage(receiver);
    }

    public String createNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(1000000);
        return String.format("%06d", randomNumber);
    }

    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(serverEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("도란도란 인증 메일입니다.");
        String body = "";
        body += "<h3>도란도란 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>번호를 알맞게 입력해주세요.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    public void sendSimpleMessage(String receiverEmail) throws MessagingException {
        String authCode = createNumber();

        MimeMessage message = createMail(receiverEmail, authCode);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new RestApiException(MemberErrorCode.MAIL_ERROR);
        }
        // TODO 인증코드 저장
    }

    public void authCode(Map<String, Integer> authCode) {

    }
}