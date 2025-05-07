package com.example.dorandroan.controller;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Long>> createChatRoom(@AuthenticationPrincipal CustomUserDetails member,
                                                            @Valid @RequestBody ChatRoomRequestDto requestDto) {

        return ResponseEntity.ok(Map.of("chatRoomId", chatRoomService.createChatRoom(member.getMember(), requestDto)));
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

}
