package com.example.dorandroan.repository;

import com.example.dorandroan.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshRepository extends CrudRepository<RefreshToken, Long> {
}
