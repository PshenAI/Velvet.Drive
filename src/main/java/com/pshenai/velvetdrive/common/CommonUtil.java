package com.pshenai.velvetdrive.common;

import com.pshenai.velvetdrive.entities.user.DriveUser;
import com.pshenai.velvetdrive.entities.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.text.DecimalFormat;

@Component
public class CommonUtil {

    private final UserService userService;


    @Autowired
    public CommonUtil(UserService userService) {
        this.userService = userService;
    }

    public DriveUser getUser(User user, OAuth2User principal) {
        if(user!= null){
            return userService.findByEmail(user.getUsername());
        } else {
            return userService.findByEmail((String) principal.getAttributes().get("email"));
        }
    }

    public void spaceAllocator(DriveUser currentUser, Model model){ //returns progress (space) bar info
        String[] driveSpaces = new String[3];
        DecimalFormat dFormat = new DecimalFormat("##.#");
        driveSpaces[0] = dFormat.format((currentUser.getDrive().getDrivePlan().getSpace()));
        driveSpaces[1] = dFormat.format((currentUser.getDrive().getDrivePlan().getSpace() - currentUser.getDrive().getSpaceLeft()));

        Double spaceMax = currentUser.getDrive().getDrivePlan().getSpace();
        Double spaceLeft = currentUser.getDrive().getSpaceLeft();
        driveSpaces[2] = dFormat.format(((spaceMax-spaceLeft)*100)/spaceMax);

        model.addAttribute("percentage", driveSpaces[2]);
        model.addAttribute("maxSpace", driveSpaces[0]);
        model.addAttribute("usedSpace", driveSpaces[1]);

    }
}
