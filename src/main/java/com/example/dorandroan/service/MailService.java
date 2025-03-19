package com.example.dorandroan.service;

import com.example.dorandroan.dto.ClientCodeResponseDto;
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

    public ClientCodeResponseDto sendEmail(EmailAuthRequestDto requestDto) throws MessagingException {
        String receiver = requestDto.getEmail();
        boolean findUser = memberRegistrationService.findByEmail(receiver);
        if (requestDto.getIsSignUp()) {
            if (findUser) {
                throw new RestApiException(MemberErrorCode.DUPLICATED_EMAIL);
            }
        } else {
            if (!findUser) {
                throw new RestApiException(MemberErrorCode.USER_NOT_FOUND);
            }
        }
        return ClientCodeResponseDto.builder().clientCode(sendSimpleMessage(receiver)).build();
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

    public String sendSimpleMessage(String receiverEmail) throws MessagingException {
        Integer authCode = createNumber();

        MimeMessage message = createMail(receiverEmail, authCode);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new RestApiException(MemberErrorCode.MAIL_ERROR);
        }
        String clientNumber = createClientNumber(receiverEmail);
        redisService.saveCode(clientNumber, authCode);
        return clientNumber;
    }

    public void confirmCode(CodeAuthRequestDto requestDto) {
        AuthCode foundCode = redisService.findByClientCode(requestDto.getClientCode());
        if (!foundCode.getAuthCode().equals(requestDto.getAuthCode())) {
            throw new RestApiException(MailAuthErrorCode.CODE_UNMATCHED);
        }
    }
}