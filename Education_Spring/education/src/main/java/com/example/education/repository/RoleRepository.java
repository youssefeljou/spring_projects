package com.example.education.repository;

import com.example.education.model.Admin;
import com.example.education.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long>
{

}
