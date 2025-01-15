package com.example.education.config;

import com.example.education.dto.*;
import com.example.education.mapper.*;
import com.example.education.model.Course;
import com.example.education.model.Role;
import com.example.education.model.User;
import com.example.education.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StartupApp implements CommandLineRunner {

    private final AdminService adminService;
    private final CourseService courseService;
    private final MasterService masterService;
    private final RoleService roleService;
    private final StudentService studentService;
    private final UserService userService;

    private final AdminMapper adminMapper;
    private final CourseMapper courseMapper;
    private final MasterMapper masterMapper;
    private final RoleMapper roleMapper;
    private final StudentMapper studentMapper;
    private final UserMapper userMapper;

    Role adminRole;
    Role masterRole;
    Role studentRole;
    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (roleService.findAll().isEmpty()) {
            RoleDto adminRoleDto = new RoleDto("ADMIN");
            RoleDto masterRoleDto = new RoleDto("MASTER");
            RoleDto studentRoleDto = new RoleDto("STUDENT");
            adminRole = roleMapper.mapToEntity(adminRoleDto);
            masterRole = roleMapper.mapToEntity(masterRoleDto);
            studentRole = roleMapper.mapToEntity(studentRoleDto);


            List<Role> adminRoles = new ArrayList<>();
            adminRoles.add(adminRole);
            UserDto adminUserDto = new UserDto("admin@example.com", "password", adminRoles, null, null, null);
            adminUserDto = userService.createUser(adminUserDto);


            List<Role> masterRoles = new ArrayList<>();
            masterRoles.add(masterRole);
            UserDto masterUserDto = new UserDto("master@example.com", "password", masterRoles, null, null, null);
            masterUserDto = userService.createUser(masterUserDto);

            List<Role> studentRoles = new ArrayList<>();
            studentRoles.add(studentRole);
            UserDto studentUserDto1 = new UserDto("student1@example.com", "password", studentRoles, null, null, null);
            studentUserDto1 = userService.createUser(studentUserDto1);
            UserDto studentUserDto2 = new UserDto("student2@example.com", "password", studentRoles, null, null, null);
            studentUserDto1 = userService.createUser(studentUserDto2);

            AdminDto adminDto = new AdminDto("Admin", "User", "123456789", 1L);
            adminService.createAdmin(adminDto);


            CourseDto courseDto1 = courseService.addCourse(new CourseDto("Math 101", "100.00", null, null));
            CourseDto courseDto2 = courseService.addCourse(new CourseDto("Physics 101", "150.00", null, null));

            Course course1=courseService.findByIdWithAllInfo(1L);
            Course course2=courseService.findByIdWithAllInfo(2L);

            MasterDto master = new MasterDto(2L, 1L, "Master", "User");
            masterService.createMaster(master);


            StudentDto student1 = new StudentDto("John", "Doe", 3L, List.of(course1,course2));
            studentService.createStudent(student1);

            StudentDto student2 = new StudentDto("Jane", "Smith", 4L, List.of(course1));
            studentService.createStudent(student2);
        }
    }
}

