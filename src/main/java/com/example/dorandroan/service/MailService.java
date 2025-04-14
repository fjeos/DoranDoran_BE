package com.example.dorandroan.service;

import com.example.dorandroan.dto.CodeAuthRequestDto;
import com.example.dorandroan.dto.EmailAuthRequestDto;
import com.example.dorandroan.entity.AuthCode;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MailAuthErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String serverEmail;
    private final JavaMailSender javaMailSender;
    private final MemberRegistrationService memberRegistrationService;
    private final RedisService redisService;

    public void sendEmail(EmailAuthRequestDto requestDto) throws MessagingException {
        String receiver = requestDto.getEmail();
        boolean findUser = memberRegistrationService.findByEmail(receiver);
        if (requestDto.getIsSignUp()) {
            if (findUser) {
                throw new RestApiException(MemberErrorCode.DUPLICATED_EMAIL);
            }
        } else {
            if (!findUser) {
                throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
            }
        }
        sendSimpleMessage(receiver);
    }

    private Integer createNumber() {
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(1000000);
        return Integer.parseInt(String.format("%06d", randomNumber));
    }

    public String createClientNumber(String receiverEmail) {
        return UUID.nameUUIDFromBytes(receiverEmail.getBytes()).toString();
    }

    public MimeMessage createMail(String mail, Integer number) throws MessagingException {
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
        Integer authCode = createNumber();

        MimeMessage message = createMail(receiverEmail, authCode);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            System.out.println("메일 전송 중 에러 발생");
            System.out.println(e.getMessage());
            throw new RestApiException(MemberErrorCode.MAIL_ERROR);
        }
        redisService.saveCode(receiverEmail, authCode);
    }

    public void confirmCode(CodeAuthRequestDto requestDto) {
        AuthCode foundCode = redisService.findByEmail(requestDto.getEmail());
        if (foundCode.getApproved())
            throw new RestApiException(MailAuthErrorCode.ALREADY_APPROVED);
        if (!foundCode.getAuthCode().equals(requestDto.getAuthCode())) {
            throw new RestApiException(MailAuthErrorCode.CODE_UNMATCHED);
        }
        redisService.confirmCode(foundCode);
    }
}