package com.example.dorandroan.service;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final GroupChatroomService groupChatroomService;
    private final PrivateChatroomService privateChatroomService;
    private final MemberService memberService;
    private final MongoTemplate mongoTemplate;


    public List<MyChatRoomListResponseDto> getChatRoomLists(CustomUserDetails member) {
        List<MyChatRoomListResponseDto> responseDto = new ArrayList<>();

        List<MemberChatroom> groupList = groupChatroomService.findChatRoomByMemberAndNotQuit(member.getMember());
        for (MemberChatroom memberChatroom : groupList) {
            responseDto.add(MyChatRoomListResponseDto.toDto(memberChatroom.getGroupChatroom(),
                    groupChatroomService.findTop(memberChatroom.getGroupChatroom().getGroupChatroomId()),
                    groupChatroomService.countGroupUnreadChat(memberChatroom)));
        }
        List<PrivateChatroom> privateList = privateChatroomService.findChatroomByMember(member.getMember());
        for (PrivateChatroom privateChatroom : privateList) {
            PrivateChat lastChat = privateChatroomService.findTop(privateChatroom.getPrivateChatroomId());
            if (lastChat == null)
                continue;
            responseDto.add(MyChatRoomListResponseDto.toPrivateDto(privateChatroom, lastChat,
                    privateChatroom.getMemberA().equals(member.getMember()) ? privateChatroom.getMemberB() : privateChatroom.getMemberA(),
                    privateChatroomService.countPrivateUnreadChat(member.getMember(), privateChatroom)));
        }

        responseDto.sort((a, b) -> {
            if (a.getLastChatTime() == null && b.getLastChatTime() == null) return 0;
            if (a.getLastChatTime() == null) return 1;
            if (b.getLastChatTime() == null) return -1;
            return b.getLastChatTime().compareTo(a.getLastChatTime());
        });

        return responseDto;
    }


    public ProfileResponseDto getMemberProfile(Long memberId) {

        Member findMember = memberService.findMember(memberId);
        if (findMember.getState())
            throw new RestApiException(MemberErrorCode.MEMBER_NOT_FOUND);
        return ProfileResponseDto.toDto(findMember);
    }

    public List<RecommendMemberResponseDto> getRecommendMembers() {

//        long totalCount = memberService.countRecommendMembers();
//        int idx = (int)(Math.random() * totalCount);
//        int pageSize = 8;
//        int maxPage = (int) Math.ceil((double) totalCount / pageSize);
//
//        int randomPage = new Random().nextInt(Math.max(maxPage, 1));
//        Pageable pageable = PageRequest.of(randomPage, pageSize);
        return memberService.findRecommendedMembers().stream()
                .map(RecommendMemberResponseDto::toDto).collect(Collectors.toList());
    }


    public List<ChatResponseDto> getGroupChats(Long chatRoomId, String key, Member member) {
        return getChatResponseDto(chatRoomId, key, true, member);
    }


    public List<ChatResponseDto> getPrivateChats(Long privateId, String key, Member member) {
        return getChatResponseDto(privateId, key, false, member);
    }

    private List<ChatResponseDto> getChatResponseDto(Long chatRoomId, String key, boolean isGroup, Member member) {
        Query query = new Query()
                .addCriteria(Criteria.where("chatRoomId").is(chatRoomId))
                .with(Sort.by(Sort.Direction.DESC, "sendAt"))
                .limit(30);
        if (isGroup) {
            Instant enterTime = groupChatroomService.findChatRoomByMemberAndChatRoomId(member, chatRoomId).getJoinTime();
            query.addCriteria(Criteria.where("sendAt").gt(enterTime));
        }
        if (key != null) {
            query.addCriteria(Criteria.where("_id").lt(new ObjectId((key))));
        }

        List<? extends Chat> chats;
        if (isGroup)
            chats = mongoTemplate.find(query, GroupChat.class);
        else chats = mongoTemplate.find(query, PrivateChat.class);

        Collections.reverse(chats);
        return chats.stream()
                .map(c -> c.getSenderId() == -1 ? ChatResponseDto.toDto(c, null) :
                        ChatResponseDto.toDto(c, memberService.findMember(c.getSenderId()))).collect(Collectors.toList());
    }


}
