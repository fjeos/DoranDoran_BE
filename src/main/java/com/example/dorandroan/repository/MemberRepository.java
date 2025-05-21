package com.example.dorandroan.repository;

import com.example.dorandroan.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COUNT(*) FROM Member m WHERE m.recommends = true")
    long countRecommendMembers();

    //    @Query("SELECT m FROM Member m WHERE m.recommends = true")
    @Query(value = "SELECT * FROM member WHERE recommends = true ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<Member> findRecommendedMembers();
}
