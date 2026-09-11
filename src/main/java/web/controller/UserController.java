package web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import web.model.User;
import web.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        return "user-form";
    }

    @PostMapping("/save")
    public String saveUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "user-form";
        }

        if (user.getId() == null) {
            userService.saveUser(user);
        } else {
            User existingUser = userService.getUserById(user.getId());

            if (existingUser == null) {
                bindingResult.reject(
                        "user.notFound",
                        "User not found"
                );

                return "user-form";
            }

            userService.updateUser(user);
        }

        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = userService.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "User not found"
            );

            return "redirect:/users";
        }

        model.addAttribute("user", user);

        return "user-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        User user = userService.getUserById(id);

        if (user == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "User not found"
            );

            return "redirect:/users";
        }

        userService.deleteUser(id);

        return "redirect:/users";
    }
}