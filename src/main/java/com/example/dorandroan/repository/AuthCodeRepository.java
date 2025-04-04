package com.example.dorandroan.repository;

import com.example.dorandroan.entity.AuthCode;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthCodeRepository extends CrudRepository<AuthCode, String> {

}
