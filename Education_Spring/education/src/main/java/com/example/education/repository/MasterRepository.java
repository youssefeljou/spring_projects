package com.example.education.repository;

import com.example.education.model.Admin;
import com.example.education.model.Master;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterRepository extends JpaRepository<Master,Long>
{

}
