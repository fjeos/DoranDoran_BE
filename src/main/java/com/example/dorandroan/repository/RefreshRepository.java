package com.example.dorandroan.repository;

import com.example.dorandroan.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefresh(String refresh);
}
