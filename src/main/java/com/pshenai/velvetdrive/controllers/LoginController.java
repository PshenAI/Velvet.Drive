package com.pshenai.velvetdrive.controllers;

import com.pshenai.velvetdrive.entities.user.UserFactory;
import com.pshenai.velvetdrive.entities.user.UserRole;
import com.pshenai.velvetdrive.entities.user.UserService;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
public class LoginController {

    private final EmailValidator emailValidator;
    private final UserFactory factory;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public LoginController(EmailValidator emailValidator, PasswordEncoder passwordEncoder,
                           UserFactory factory, UserService userService) {
        this.emailValidator = emailValidator;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.factory = factory;
    }


    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false, defaultValue = "false") Boolean error,
                        @RequestParam(name = "logout", required = false, defaultValue = "false") Boolean logout,
                        @RequestParam(name = "successRegistration", required = false, defaultValue = "false")
                        Boolean successRegistration, Model model) {
        model.addAttribute("error", error);
        model.addAttribute("logout", logout);
        model.addAttribute("successRegistration", successRegistration);
        return "login";
    }

    @GetMapping("/register")
    public String registration(Model model,
                               @RequestParam(name = "emailExists", required = false, defaultValue = "false") Boolean emailExists,
                               @RequestParam(name = "wrongEmail", required = false, defaultValue = "false") Boolean wrongEmail,
                               @RequestParam(name = "samePassword", required = false, defaultValue = "false") Boolean samePassword) {
        model.addAttribute("emailExists", emailExists);
        model.addAttribute("wrongEmail", wrongEmail);
        model.addAttribute("samePassword", samePassword);
        return "register";
    }

    @PostMapping("/user/validation")
    public String UserRegistration(RedirectAttributes attributes,
                                   @RequestParam(required = false, name = "email") String email,
                                   @RequestParam(required = false, name = "password") String password,
                                   @RequestParam(required = false, name = "fullName") String fullName,
                                   @RequestParam(required = false, name = "confirmPassword") String confirmPassword){
        if(!userCheck(email, attributes, password, confirmPassword)){
            return "redirect:/register";
        }

        String passHash = passwordEncoder.encode(password);
        factory.createUser(email, passHash, UserRole.USER, fullName, null);
        attributes.addAttribute("successRegistration", true);
        return "redirect:/login";
    }

    private boolean userCheck(String email, RedirectAttributes attributes, String password, String confirmPassword){
        boolean verified = true;
        if(!emailValidator.isValid(email)){
            attributes.addAttribute("wrongEmail", true);
            verified = false;
        } else if(!password.equals(confirmPassword)){
            attributes.addAttribute("samePassword", true);
            verified = false;
        } else if(userService.findByEmail(email)!= null){
            attributes.addAttribute("emailExists", true);
            verified = false;
        }
        return verified;
    }
}
