package com.example.dorandroan.service;

import com.example.dorandroan.dto.*;
import com.example.dorandroan.entity.*;
import com.example.dorandroan.global.RestApiException;
import com.example.dorandroan.global.error.ChattingErrorCode;
import com.example.dorandroan.global.error.MemberErrorCode;
import com.example.dorandroan.global.jwt.CustomUserDetails;
import com.example.dorandroan.repository.*;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final GroupChatRoomRepository groupChatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final PrivateChatroomRepository privateChatroomRepository;
    private final GroupChatRepository groupChatRepository;
    private final PrivateChatRepository privateChatRepository;
    private final MemberService memberService;
    private final ChatService chatService;
    private Member getNowMember(Long memberId) {
        return memberService.findMember(memberId);
    }

    @Transactional
    public Long createGroupChatroom(Member member, ChatRoomRequestDto requestDto) {

        GroupChatroom newChatRoom = groupChatRoomRepository.save(GroupChatroom.builder().
                title(requestDto.getChatRoomTitle())
                .chatRoomImg(requestDto.getChatRoomImage())
                .description(requestDto.getDescription())
                .maxPartIn(requestDto.getMaxCount())
                .nowPartIn(1)
                .closed(false)
                .build());
        memberChatRoomRepository.save(MemberChatroom.builder().member(member)
                .role(ChatRoomRole.LEAD)
                .quit(false)
                .groupChatroom(newChatRoom).build());

        chatService.sendSystemMessage(newChatRoom.getGroupChatroomId(), true, "채팅방이 생성되었습니다.");
        return newChatRoom.getGroupChatroomId();
    }

    public List<MyChatRoomListResponseDto> getChatRoomLists(CustomUserDetails member) {
        List<MyChatRoomListResponseDto> responseDto = new ArrayList<>();

        List<GroupChatroom> groupList = memberChatRoomRepository.findChatRoomByMember(member.getMember());
        for (GroupChatroom groupChatroom : groupList) {
            responseDto.add(MyChatRoomListResponseDto.toDto(groupChatroom,
                    groupChatRepository.findTopByChatRoomIdOrderBySendAtDesc(groupChatroom.getGroupChatroomId())));
        }
        List<PrivateChatroom> privateList = privateChatroomRepository.findChatroomByMember(member.getMember());
        for (PrivateChatroom privateChatroom : privateList) {
            PrivateChat lastChat = privateChatRepository.findTopByChatRoomIdOrderBySendAtDesc(privateChatroom.getPrivateChatroomId());
            if (lastChat == null)
                continue;
            responseDto.add(MyChatRoomListResponseDto.toPrivateDto(privateChatroom, lastChat,
                    privateChatroom.getMemberA().equals(member.getMember())? privateChatroom.getMemberB() : privateChatroom.getMemberA()));
        }

        responseDto.sort((a, b) -> {
            if (a.getLastChatTime() == null && b.getLastChatTime() == null) return 0;
            if (a.getLastChatTime() == null) return 1;
            if (b.getLastChatTime() == null) return -1;
            return b.getLastChatTime().compareTo(a.getLastChatTime());
        });

        return  responseDto;
    }


    public List<ChatRoomMembersResponseDto> getGroupChatRoomMembers(Member member, Long chatRoomId) {

        List<Member> memberByChatRoom = memberChatRoomRepository.findMemberByChatRoom(chatRoomId);
        if (memberByChatRoom.isEmpty())
            throw new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND);

        if (memberByChatRoom.stream().noneMatch(m -> m.getMemberId().equals(member.getMemberId())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return memberByChatRoom.stream()
                .map(ChatRoomMembersResponseDto::toDto).toList();
    }

    public List<ChatRoomMembersResponseDto> getPrivateChatRoomMembers(Long memberId, Long privateChatRoomId) {
        Member nowMember = getNowMember(memberId);
        PrivateChatroom privateChatroom = privateChatroomRepository.findById(privateChatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        if (!(nowMember.equals(privateChatroom.getMemberA()) || nowMember.equals(privateChatroom.getMemberB())))
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);

        return List.of(ChatRoomMembersResponseDto.toDto(privateChatroom.getMemberA()),
                ChatRoomMembersResponseDto.toDto(privateChatroom.getMemberB()));
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

    @Transactional
    public Long createPrivateChatroom(Member member, Long otherMemberId) {
        Member nowMember = getNowMember(member.getMemberId());
        Member otherMember = memberService.findMember(otherMemberId);
        if (nowMember.equals(otherMember) || nowMember.getState() || otherMember.getState() || !otherMember.getRecommends())
            throw new RestApiException(ChattingErrorCode.INVALID_MEMBER);

        return privateChatroomRepository.save(
                PrivateChatroom.builder()
                .memberA(nowMember)
                .memberB(otherMember).build()).getPrivateChatroomId();
    }

    @Transactional
    public void enterGroupChatroom(Member member, Long chatRoomId) {

        GroupChatroom groupChatroom = groupChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));

        if (memberChatRoomRepository.existsByMember_MemberIdAndGroupChatroom_GroupChatroomIdAndQuitFalse(
                member.getMemberId(), chatRoomId))
            throw new RestApiException(ChattingErrorCode.ALREADY_JOINED);

        if (groupChatroom.getMaxPartIn() >= groupChatroom.getNowPartIn())
            throw new RestApiException(ChattingErrorCode.FULL_CHATROOM);

        memberChatRoomRepository.save(MemberChatroom.builder()
                .groupChatroom(groupChatroom)
                .member(memberService.findMember(member.getMemberId()))
                .role(ChatRoomRole.PART)
                .quit(false).build());
        chatService.sendSystemMessage(chatRoomId, true, member.getNickname() + "님이 입장하셨습니다.");
        groupChatroom.enterRoom();
    }

    public List<ChatResponseDto> getGroupChats(Long chatRoomId, String key) {
        return getChatResponseDto(chatRoomId, key, true);
    }


    public List<ChatResponseDto> getPrivateChats(Long privateId, String key) {
        return getChatResponseDto(privateId, key, false);
    }
    private List<ChatResponseDto> getChatResponseDto(Long chatRoomId, String key, boolean isGroup) {
        Pageable pageable = PageRequest.of(0, 30, Sort.by(Sort.Direction.ASC, "id"));
        List<Chat> result;
        if (key != null) {
            ObjectId lastId = new ObjectId(key);
            if (isGroup)
                result = groupChatRepository.findByChatRoomIdAndIdLessThanOrderById(chatRoomId, lastId, pageable);
            else
                result = privateChatRepository.findByChatRoomIdAndIdLessThanOrderById(chatRoomId, lastId, pageable);

        } else {
            if (isGroup)
                result = groupChatRepository.findByChatRoomIdOrderById(chatRoomId, pageable);
            else
                result = privateChatRepository.findByChatRoomIdOrderById(chatRoomId, pageable);
        }
        return result.stream()
                .map(c -> ChatResponseDto.toDto(c, memberService.findMember(c.getSenderId()))).collect(Collectors.toList());
    }

    @Transactional
    public void changeRoomTitle(Member member, ChatRoomTitleUpdateDto requestDto) {
        getMemberChatroomForUpdate(member, requestDto.getChatRoomId())
                .changeTitle(requestDto.getChatRoomTitle());
    }

    @Transactional
    public void changeRoomImage(Member member, ChatRoomImageUpdateDto requestDto) {
        getMemberChatroomForUpdate(member, requestDto.getChatRoomId())
                .changeRoomImage(requestDto.getChatRoomImage());
    }

    @Transactional
    public void changeRoomMaxCount(Member member, ChatRoomMaxUpdateDto requestDto) {
        GroupChatroom chatroom = getMemberChatroomForUpdate(member, requestDto.getChatRoomId());
        if (requestDto.getMaxCount() < chatroom.getNowPartIn())
            throw new RestApiException(ChattingErrorCode.LESS_THAN_NOW);
        chatroom.changeMaxCount(requestDto.getMaxCount());
    }
    @Transactional
    public void changeRoomDescription(Member member, ChatRoomDescriptionUpdateDto requestDto) {
        getMemberChatroomForUpdate(member, requestDto.getChatRoomId())
                .changeDescription(requestDto.getDescription());
    }
    @Transactional
    public void deleteGroupChatRoom(Member member, Long chatRoomId) {
        getMemberChatroomForUpdate(member, chatRoomId).delete();
    }

    private GroupChatroom getMemberChatroomForUpdate(Member member, Long chatRoomId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomIdAndNotClosed(member, chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        if (!chatroom.getRole().equals(ChatRoomRole.LEAD))
            throw new RestApiException(ChattingErrorCode.NOT_LEAD);
        if (chatroom.getGroupChatroom().isClosed())
            throw new RestApiException(ChattingErrorCode.ALREADY_CLOSED);
        return chatroom.getGroupChatroom();
    }

    @Transactional
    public void outOfPrivateChatRoom(Member member, Long privateId) {
        PrivateChatroom chatroom = privateChatroomRepository.findChatroomByMemberAndNotQuit(member, privateId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.NOT_PART_IN));

        if (chatroom.getMemberA().equals(member)) {
            if (chatroom.isAOut())
                throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
            else {
                chatService.sendSystemMessage(privateId, false, chatroom.getMemberA().getNickname()
                        + "님이 퇴장하셨습니다.");
                chatroom.outA();
            }
        } else {
            if (chatroom.isBOut())
                throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
            else {
                chatService.sendSystemMessage(privateId, false, chatroom.getMemberB().getNickname()
                        + "님이 퇴장하셨습니다.");
                chatroom.outB();
            }
        }
    }

    @Transactional
    public void outOfGroupChatRoom(Member member, Long groupId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomId(member, groupId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        if (chatroom.isQuit())
            throw new RestApiException(ChattingErrorCode.NOT_PART_IN);
        chatService.sendSystemMessage(groupId, true, member.getNickname() + "님이 퇴장하셨습니다.");
        chatroom.out();
    }

    public ChatroomInfoResponseDto getGroupChatroomInfo(Member member, Long chatRoomId) {
        MemberChatroom chatroom = memberChatRoomRepository.findChatRoomByMemberAndChatRoomIdAndNotClosed(member, chatRoomId)
                .orElseThrow(() -> new RestApiException(ChattingErrorCode.CHATROOM_NOT_FOUND));
        return ChatroomInfoResponseDto.toDto(chatroom.getGroupChatroom(), chatroom.getRole().equals(ChatRoomRole.LEAD));
    }

    public List<ChatRoomListResponseDto> getGroupChatroomList(Long cursor, Integer limit) {


        return groupChatRoomRepository.findAllAndNotClosed(cursor, PageRequest.of(0, limit)).stream().map(
                c -> ChatRoomListResponseDto.toDto(c, groupChatRepository.findTopByChatRoomIdOrderBySendAtDesc(c.getGroupChatroomId()))
        ).collect(Collectors.toList());
    }
}
