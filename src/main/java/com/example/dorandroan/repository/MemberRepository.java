package com.example.dorandroan.repository;

import com.example.dorandroan.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.recommends = true and m.state = false")
    List<Member> findByRecommends();
}
