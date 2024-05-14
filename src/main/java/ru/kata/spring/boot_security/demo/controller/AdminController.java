package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping(value = "/getAll")
    public String findAll(ModelMap model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @GetMapping("/viewCreateForm")
    public String viewCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "new";
    }

    @PostMapping("/saveUser")
    public String save(@ModelAttribute("user") User user, @RequestParam("roles") List<Long> roleIds) {
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin/getAll";
    }

    @GetMapping("/getUserEdit")
    public String getUser(Model model, @RequestParam long id) {
        model.addAttribute("user", userService.findOne(id));
        model.addAttribute("allRoles", roleRepository.findAll());
        return "edit";
    }

    @PostMapping("/updateUser")
    public String update(@ModelAttribute("user") User user,
                         @RequestParam long id,
                         @RequestParam("roles") List<Long> roleIds) {
        User existingUser = userService.findOne(id);

        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(user.getPassword());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
            existingUser.setRoles(roles);
        }

        userService.update(id, existingUser);
        return "redirect:/admin/getAll";
    }

    @PostMapping("/deleteUser")
    public String delete(@RequestParam long id) {
        userService.delete(id);
        return "redirect:/admin/getAll";
    }
}