package ru.kata.spring.boot_security.demo.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UsersRepository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class DbInit implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private User tempAdmin;
    private User tempUser;
    private final WebSecurityConfig webSecurityConfig;

    @Autowired
    public DbInit(UsersRepository usersRepository
            , RoleRepository roleRepository
            , WebSecurityConfig webSecurityConfig) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.webSecurityConfig = webSecurityConfig;
    }

    @Override
    public void run(String... args) throws Exception {


        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }

        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
        adminRole = roleRepository.findByName("ROLE_ADMIN");
        userRole = roleRepository.findByName("ROLE_USER");

        tempAdmin = new User("tempAdmin"
                , webSecurityConfig.getPasswordEncoder().encode("admin_password")
                , "FirstNameTempAdmin"
                , "LastNameTempAdmin"
                , "E-mailTempAdmin@email.ru"
        ,Set.of(adminRole, userRole));

        tempUser = new User("tempUser"
                , webSecurityConfig.getPasswordEncoder().encode("user_password")
                , "FirstNameTempUser"
                , "LastNameTempUser"
                , "E-mailTempUser@email.ru"
        , Set.of(userRole));

        usersRepository.save(tempAdmin);
        usersRepository.save(tempUser);
    }
}
