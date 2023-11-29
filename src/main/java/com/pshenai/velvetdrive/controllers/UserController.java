package com.pshenai.velvetdrive.controllers;

import com.pshenai.velvetdrive.common.CommonUtil;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.user.DriveUser;
import com.pshenai.velvetdrive.entities.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final CommonUtil commonUtil;
    private final UserService userService;


    @Autowired
    public UserController(PasswordEncoder passwordEncoder, CommonUtil commonUtil, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.commonUtil = commonUtil;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, Model model,
                          @RequestParam(name = "samePassword", required = false, defaultValue = "false") Boolean samePassword,
                          @RequestParam(name = "wrongOldPassword", required = false, defaultValue = "false") Boolean wrongOldPassword,
                          @RequestParam(name = "sameName", required = false, defaultValue = "false") Boolean sameName,
                          @RequestParam(name = "sameSurname", required = false, defaultValue = "false") Boolean sameSurname){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        commonUtil.spaceAllocator(currentUser, model);

        if(principal != null){
            model.addAttribute("oauthUser", true);
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("fileNumber", fileCounter(currentUser));
        model.addAttribute("samePassword", samePassword);
        model.addAttribute("wrongOldPassword", wrongOldPassword);
        model.addAttribute("sameName", sameName);
        model.addAttribute("sameSurname", sameSurname);
        return "profile";
    }

    @PutMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes attributes,
                                @RequestParam(name = "newName", required = false) String newName,
                                @RequestParam(name = "oldPassword", required = false) String oldPassword,
                                @RequestParam(name = "newPassword", required = false) String newPassword){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        if((!oldPassword.equals("")) && (!newPassword.equals("")) &&
                !updateCheck(attributes,oldPassword,newPassword,currentUser)){
            return "redirect:/profile";
        }

        userService.updateCredentials(currentUser.getId(), passwordEncoder.encode(newPassword), newName);

        return "redirect:/profile";
    }


    private boolean updateCheck(RedirectAttributes attributes, String oldPassword, String newPassword, DriveUser user){
        boolean verified = true;
        if(!passwordEncoder.matches(oldPassword, user.getPassHash())){
            attributes.addAttribute("wrongOldPassword", true);
            verified = false;
        } else if(oldPassword.equals(newPassword)){
            attributes.addAttribute("samePassword", true);
            verified = false;
        }
        return verified;
    }

    private Integer fileCounter(DriveUser driveUser){
        Drive drive = driveUser.getDrive();
        return drive.getFolderList().stream().mapToInt(a -> a.getFiles().size()).sum();
    }
}
