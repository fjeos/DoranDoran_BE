package com.example.dorandroan.repository;

import com.example.dorandroan.entity.AuthCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCodeRepository extends CrudRepository<AuthCode, Integer> {
}
