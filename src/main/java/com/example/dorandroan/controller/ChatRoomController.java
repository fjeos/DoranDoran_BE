package com.example.dorandroan.controller;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/lists")
    public ResponseEntity<List<ChatRoomListResponseDto>> getChatRoomLists(@AuthenticationPrincipal CustomUserDetails member) {
        return ResponseEntity.ok(chatRoomService.getChatRoomLists(member));
    }

    @PostMapping("/chatrooms")
    public ResponseEntity<Map<String, Long>> createGroupChatroom(@AuthenticationPrincipal CustomUserDetails member,
                                                            @Valid @RequestBody ChatRoomRequestDto requestDto) {

        return ResponseEntity.ok(Map.of("chatRoomId", chatRoomService.createGroupChatroom(member.getMember(), requestDto)));
    }

    @GetMapping("/members")
    public ResponseEntity<List<ChatRoomMembersResponseDto>> getChatRoomMembers(@RequestParam("id") Long chatRoomId) {
        return ResponseEntity.ok(chatRoomService.getChatRoomMembers(chatRoomId));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponseDto> getMemberProfile(@RequestParam("id") Long memberId) {
        return ResponseEntity.ok(chatRoomService.getMemberProfile(memberId));
    }

    @GetMapping("/recommends")
    public ResponseEntity<List<RecommendMemberResponseDto>> getRecommendMembers() {
        return ResponseEntity.ok(chatRoomService.getRecommendMembers());
    }

    @PostMapping("/private")
    public ResponseEntity<Map<String, Long>> createPrivateChatroom(@AuthenticationPrincipal CustomUserDetails member,
                                                                   @RequestBody Map<String, Long> requestDto) {
        return ResponseEntity.ok(Map.of("chatRoomId",
                chatRoomService.createPrivateChatroom(member, requestDto.get("memberId"))));
    }

    @PostMapping("/group")
    public ResponseEntity<Void> enterGroupChatroom(@AuthenticationPrincipal CustomUserDetails member,
                                                   @RequestParam("id") Long chatRoomId) {
        chatRoomService.enterGroupChatroom(member, chatRoomId);
        return ResponseEntity.ok().build();
    }

}
