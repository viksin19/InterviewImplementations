package com.example.authserver.config;

import com.example.authserver.entity.AppUser;
import com.example.authserver.entity.Privilege;
import com.example.authserver.entity.Role;
import com.example.authserver.repository.PrivilegeRepository;
import com.example.authserver.repository.RoleRepository;
import com.example.authserver.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(RoleRepository roleRepository,
                               PrivilegeRepository privilegeRepository,
                               UserRepository userRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Privilege readPrivilege = privilegeRepository.findByName("READ_PRIVILEGE")
                .orElseGet(() -> privilegeRepository.save(new Privilege("READ_PRIVILEGE")));
            Privilege writePrivilege = privilegeRepository.findByName("WRITE_PRIVILEGE")
                .orElseGet(() -> privilegeRepository.save(new Privilege("WRITE_PRIVILEGE")));
            Privilege adminPrivilege = privilegeRepository.findByName("ADMIN_PRIVILEGE")
                .orElseGet(() -> privilegeRepository.save(new Privilege("ADMIN_PRIVILEGE")));

            Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role("ROLE_USER");
                    role.setPrivileges(new HashSet<>(List.of(readPrivilege)));
                    return roleRepository.save(role);
                });

            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ROLE_ADMIN");
                    role.setPrivileges(new HashSet<>(List.of(readPrivilege, writePrivilege, adminPrivilege)));
                    return roleRepository.save(role);
                });

            if (!userRepository.existsByEmail("admin@spring.com")) {
                AppUser admin = new AppUser("admin@spring.com", passwordEncoder.encode("Admin@123"));
                admin.setRoles(new HashSet<>(List.of(adminRole)));
                userRepository.save(admin);
            }
        };
    }
}
