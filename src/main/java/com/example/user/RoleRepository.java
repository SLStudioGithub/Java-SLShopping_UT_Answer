package com.example.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
