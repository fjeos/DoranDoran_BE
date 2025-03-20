package com.example.dorandroan.repository;

import com.example.dorandroan.entity.BlockedToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BlockedTokenRepository extends CrudRepository<BlockedToken, Long> {
    BlockedToken findByToken(String token);
}
