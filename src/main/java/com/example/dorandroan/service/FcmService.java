package com.example.dorandroan.service;

import com.example.dorandroan.entity.Member;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmService {

    private final MemberService memberService;
    private final FirebaseMessaging firebaseMessaging;

    public void sendFcmMessage(Member member, String title, String body) throws FirebaseMessagingException, IOException {
        String deviceToken = memberService.getDeviceToken(member);
        firebaseMessaging.send(Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title).setBody(body).build())
                .setToken(deviceToken).build());
    }

}
