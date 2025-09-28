package com.example.dorandroan.controller;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.service.ChatAndChatroomUtil;
import com.example.dorandroan.service.ChatRoomService;
import com.example.dorandroan.service.GroupChatroomService;
import com.example.dorandroan.service.PrivateChatroomService;
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
    private final GroupChatroomService groupChatroomService;
    private final PrivateChatroomService privateChatroomService;
    private final ChatAndChatroomUtil chatAndChatroomUtil;

    @GetMapping("/lists")
    public ResponseEntity<List<MyChatRoomListResponseDto>> getChatRoomLists(@AuthenticationPrincipal CustomUserDetails member) {
        return ResponseEntity.ok(chatRoomService.getChatRoomLists(member));
    }

    @GetMapping("/info")
    public ResponseEntity<ChatroomInfoResponseDto> getGroupChatroomInfo(@AuthenticationPrincipal CustomUserDetails member,
                                                     @RequestParam("id") Long chatRoomId) {
        return ResponseEntity.ok(groupChatroomService.getGroupChatroomInfo(member.getMember(), chatRoomId));
    }

    @PostMapping("/chatrooms")
    public ResponseEntity<Map<String, Long>> createGroupChatroom(@AuthenticationPrincipal CustomUserDetails member,
                                                                 @Valid @RequestBody ChatRoomRequestDto requestDto) {

        return ResponseEntity.ok(Map.of("chatRoomId", chatAndChatroomUtil.createGroupChatroom(member.getMember(), requestDto)));
    }

    @GetMapping("/members")
    public ResponseEntity<List<ChatRoomMembersResponseDto>> getChatRoomMembers(@RequestParam(value = "groupId", required = false) Long groupId,
                                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
            return ResponseEntity.ok(groupChatroomService.getGroupChatRoomMembers(userDetails.getMember(), groupId));
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
                privateChatroomService.createPrivateChatroom(member.getMember(), requestDto.get("memberId"))));
    }

    @PostMapping("/group")
    public ResponseEntity<Void> enterGroupChatroom(@AuthenticationPrincipal CustomUserDetails member,
                                                   @RequestBody Map<String, Long> requestDto) {
        chatAndChatroomUtil.enterGroupChatroom(member.getMember(), requestDto.get("chatRoomId"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatResponseDto>> getChats(@RequestParam(value = "groupId", required = false) Long groupId,
                                                          @RequestParam(value = "privateId", required = false) Long privateId,
                                                          @RequestParam(value = "key", required = false) String key,
                                                          @AuthenticationPrincipal CustomUserDetails member) {
        if (groupId == null) {
            return ResponseEntity.ok(chatRoomService.getPrivateChats(privateId, key, member.getMember()));
        } else if (privateId == null) {
            return ResponseEntity.ok(chatRoomService.getGroupChats(groupId, key, member.getMember()));
        } else {
            throw new RestApiException(ChattingErrorCode.ILLEGAL_PARAMETER);
        }
    }

    @PatchMapping("/info/title")
    public ResponseEntity<Void> changeRoomTitle(@AuthenticationPrincipal CustomUserDetails member,
                                                @Valid @RequestBody ChatRoomTitleUpdateDto requestDto) {
        chatAndChatroomUtil.changeRoomTitle(member.getMember(), requestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/info/image")
    public ResponseEntity<Void> changeRoomImage(@AuthenticationPrincipal CustomUserDetails member,
                                                @Valid @RequestBody ChatRoomImageUpdateDto requestDto) {
        chatAndChatroomUtil.changeRoomImage(member.getMember(), requestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/info/count")
    public ResponseEntity<Void> changeRoomMaxCount(@AuthenticationPrincipal CustomUserDetails member,
                                                   @Valid @RequestBody ChatRoomMaxUpdateDto requestDto) {
        chatAndChatroomUtil.changeRoomMaxCount(member.getMember(), requestDto);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/info/description")
    public ResponseEntity<Void> changeRoomDescription(@AuthenticationPrincipal CustomUserDetails member,
                                                      @Valid @RequestBody ChatRoomDescriptionUpdateDto requestDto) {
        chatAndChatroomUtil.changeRoomDescription(member.getMember(), requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/shutdown")
    public ResponseEntity<Void> deleteGroupChatRoom(@AuthenticationPrincipal CustomUserDetails member,
                                                    @RequestParam("groupId") Long chatRoomId) {
        chatAndChatroomUtil.deleteGroupChatRoom(member.getMember(), chatRoomId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/chatrooms")
    public ResponseEntity<Void> outOfChatRoom(@AuthenticationPrincipal CustomUserDetails member,
                                              @RequestParam(value = "groupId", required = false) Long groupId,
                                              @RequestParam(value = "privateId", required = false) Long privateId) {
        if (groupId == null) {
            privateChatroomService.outOfPrivateChatRoom(member.getMember(), privateId);
            return ResponseEntity.ok().build();
        } else if (privateId == null) {
            chatAndChatroomUtil.outOfGroupChatRoom(member.getMember(), groupId);
            return ResponseEntity.ok().build();
        } else {
            throw new RestApiException(ChattingErrorCode.ILLEGAL_PARAMETER);
        }
    }

    @GetMapping("/chatrooms")
    public ResponseEntity<List<ChatRoomListResponseDto>> getGroupChatrooms(@RequestParam(value = "cursor", required = false) Long cursor,
                                                                           @RequestParam("limit") Integer limit) {

        return ResponseEntity.ok(groupChatroomService.getGroupChatroomList(cursor, limit));
    }
}
