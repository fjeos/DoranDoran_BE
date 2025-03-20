package com.example.dorandroan.global.jwt;

import com.example.dorandroan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new CustomUserDetails(memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find User")));
    }

    public CustomUserDetails loadUserById(Long memberId) {
        return new CustomUserDetails(memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find User")));
    }
}
