package com.example.education.service;

import com.example.education.dto.AdminDto;
import com.example.education.mapper.AdminMapper;
import com.example.education.model.Admin;
import com.example.education.repository.AdminRepository;
import com.example.education.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final UserRepository userRepository;

    public AdminDto findAdminById(long id)
    {
        Admin admin=adminRepository.findById(id).orElseThrow();
        return adminMapper.mapToDto(admin);
    }

    public Admin findAdminByIdWithUserInfo(long id)
    {
        Admin admin=adminRepository.findById(id).orElseThrow();
        return admin;
    }

    public List<AdminDto> findAllAdmins()
    {
        List<Admin> admins=adminRepository.findAll();
        return adminMapper.mapToDto(admins);
    }

    public List<Admin> findAllAdminsWithUserInfo()
    {
        List<Admin> admins=adminRepository.findAll();
        return admins;
    }

    public void deleteAdmin(long id)
    {
        if (adminRepository.findById(id).isEmpty())
        {
            throw new IllegalArgumentException("Admin not found for ID:" + id);
        }
        else
        {
            adminRepository.deleteById(id);
        }
    }

    public AdminDto createAdmin(AdminDto adminDto)
    {
        Admin admin=adminMapper.mapToEntity(adminDto);
        userRepository.findById(adminDto.userId()).orElseThrow(() -> new IllegalArgumentException("User not found for ID:" + admin.getId()));
        adminRepository.save(admin);
        return adminMapper.mapToDto(admin);
    }


}
