package com.pshenai.velvetdrive;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController {
    @GetMapping("/login")
    public String login(@RequestParam(name = "error", required = false, defaultValue = "false") Boolean error,
                        @RequestParam(name = "successRegistration", required = false, defaultValue = "false") Boolean successRegistration,
                        @RequestParam(name = "logout", required = false, defaultValue = "false") Boolean logout,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("successRegistration", successRegistration);
        model.addAttribute("logout", logout);
        return "login";
    }
}
