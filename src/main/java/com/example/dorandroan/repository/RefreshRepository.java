package com.example.dorandroan.repository;

import com.example.dorandroan.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshRepository extends CrudRepository<RefreshToken, Long> {
}
