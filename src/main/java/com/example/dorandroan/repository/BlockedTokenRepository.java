package com.example.dorandroan.repository;

import com.example.dorandroan.entity.BlockedToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedTokenRepository extends CrudRepository<BlockedToken, Long> {
}
