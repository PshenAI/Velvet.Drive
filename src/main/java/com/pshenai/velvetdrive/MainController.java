package com.pshenai.velvetdrive;

import com.pshenai.velvetdrive.configs.BucketName;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.drive.DrivePlan;
import com.pshenai.velvetdrive.entities.drive.DriveService;
import com.pshenai.velvetdrive.entities.file.File;
import com.pshenai.velvetdrive.entities.file.FileService;
import com.pshenai.velvetdrive.entities.folder.Folder;
import com.pshenai.velvetdrive.entities.folder.FolderService;
import com.pshenai.velvetdrive.entities.storage.StorageService;
import com.pshenai.velvetdrive.entities.user.DriveUser;
import com.pshenai.velvetdrive.entities.user.UserFactory;
import com.pshenai.velvetdrive.entities.user.UserRole;
import com.pshenai.velvetdrive.entities.user.UserService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    private final EmailValidator emailValidator;
    private final UserService userService;
    private final DriveService driveService;
    private final FolderService folderService;
    private final FileService fileService;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory factory;

    @Autowired
    public MainController(EmailValidator emailValidator, UserService userService, DriveService driveService,
                          FolderService folderService, FileService fileService, PasswordEncoder passwordEncoder,
                          UserFactory factory) {
        this.emailValidator = emailValidator;
        this.userService = userService;
        this.driveService = driveService;
        this.folderService = folderService;
        this.fileService = fileService;
        this.passwordEncoder = passwordEncoder;
        this.factory = factory;
    }

    @GetMapping("/drive")
    public String drive(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, Model model,
                        @RequestParam(value = "folder",required = false, defaultValue = "Default") String folderName,
                        @RequestParam(value = "noFile",required = false, defaultValue = "false") String noFile,
                        @RequestParam(name = "bigFile", required = false, defaultValue = "false") Boolean bigFile,
                        @RequestParam(name = "folderList",required = false, defaultValue = "false") Boolean folderList,
                        @RequestParam(name = "duplicateFolder",required = false, defaultValue = "false") Boolean dupFolder,
                        @RequestParam(value = "keyName",required = false, defaultValue = "Default") String keyName){
        DriveUser currentUser = getUser(user, principal);
        Drive currentDrive = currentUser.getDrive();
        Folder currentFolder = getFolder(folderName, currentUser.getEmail());
        spaceAllocator(currentUser, model);

        if(folderList){
            model.addAttribute("folders",currentDrive.getFolderList());
        } else {
            if(!keyName.equals("Default")){
                getFilesByKeyName(keyName, currentFolder, model);
            } else {
                model.addAttribute("files", currentFolder.getFiles());
            }
        }
        
        model.addAttribute("noFile", noFile);
        model.addAttribute("user", currentUser);
        model.addAttribute("folder", folderName);
        model.addAttribute("bigFile", bigFile);
        model.addAttribute("dupFolder", dupFolder);
        return "drive";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes attributes,
                         @RequestParam("file")MultipartFile file,
                         @RequestParam(name = "folder", defaultValue = "Default") String folderName)
            throws IOException {
        DriveUser currentUser = getUser(user, principal);
        Folder currentFolder = getFolder(folderName, currentUser.getEmail());
        if(!fileCheck(file, currentUser.getEmail(), attributes)){
            return "redirect:/drive?folder=" + currentFolder.getName();
        }
        fileService.addFile(file, currentFolder, currentFolder.getDrive());

        attributes.addAttribute("files", currentFolder.getFiles());

        return "redirect:/drive?folder=" + currentFolder.getName();
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<byte[]> getFile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                                          @RequestParam(value = "file") String fileName)
            throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        DriveUser currentUser = getUser(user, principal);

        com.pshenai.velvetdrive.entities.file.File file = fileService.getFileByName(currentUser.getEmail(), fileName);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.setContentDispositionFormData("attachment",fileName);
        return ResponseEntity.ok().headers(httpHeaders)
                .body(IOUtils.toByteArray(fileService.getFileInputStream(file.getPath())));
    }

    @DeleteMapping("/deleteFile")
    public String deleteFile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                             @RequestParam(name = "folder", defaultValue = "Default") String folderName,
                             @RequestParam(name = "file", defaultValue = "Default") String fileName){
        DriveUser currentUser = getUser(user, principal);
        Folder currentFolder = getFolder(folderName, currentUser.getEmail());
        folderService.deleteFile(currentFolder.getFilePath(fileName), currentFolder.getId());

        return "redirect:/drive?folder=" + currentFolder.getName();
    }

    @PostMapping("/createFolder")
    public String createFolder(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes attributes,
                               @RequestParam(name = "folderName") String folderName){
        DriveUser currentUser = getUser(user, principal);
        Drive currentDrive = userService.findByEmail(currentUser.getEmail()).getDrive();
        if(folderService.createFolder(folderName, currentDrive)){
            return "redirect:/drive?folderList=true";
        }
        attributes.addAttribute("duplicateFolder", true);
        return "redirect:/drive?folderList=true";
    }

    @DeleteMapping("/deleteFolder")
    public String deleteFolder(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                               @RequestParam(name = "folder", defaultValue = "Default") String folderName){
        DriveUser currentUser = getUser(user, principal);
        Folder currentFolder = getFolder(folderName, currentUser.getEmail());
        folderService.deleteFolder(currentFolder.getId());
        return "redirect:/drive?folderList=true";
    }

    @GetMapping("/pricing")
    public String pricing(Model model){
         DrivePlan[] plans = DrivePlan.values();
         model.addAttribute("plans", plans);
        return "pricing";
    }

    @PostMapping("/pricing/{drivePlan}")
    public String setPricing(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                          @PathVariable DrivePlan drivePlan){
        DriveUser currentUser = getUser(user, principal);
        driveService.updateDrivePlan(drivePlan, currentUser.getDrive());
        return "redirect:/drive?folderList=true";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, Model model,
                          @RequestParam(name = "samePassword", required = false, defaultValue = "false") Boolean samePassword,
                          @RequestParam(name = "wrongOldPassword", required = false, defaultValue = "false") Boolean wrongOldPassword,
                          @RequestParam(name = "sameName", required = false, defaultValue = "false") Boolean sameName,
                          @RequestParam(name = "sameSurname", required = false, defaultValue = "false") Boolean sameSurname){
        DriveUser currentUser = getUser(user, principal);
        spaceAllocator(currentUser, model);
        model.addAttribute("user", currentUser);
        model.addAttribute("fileNumber", fileCounter(currentUser));
        model.addAttribute("samePassword", samePassword);
        model.addAttribute("wrongOldPassword", wrongOldPassword);
        model.addAttribute("sameName", sameName);
        model.addAttribute("sameSurname", sameSurname);
        return "profile";
    }

    @PutMapping("/updateProfile")
    public String updateProfile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes attributes,
                                @RequestParam(name = "newName", required = false) String newName,
                                @RequestParam(name = "oldPassword", required = false) String oldPassword,
                                @RequestParam(name = "newPassword", required = false) String newPassword){
        DriveUser currentUser = getUser(user, principal);
        if((!oldPassword.equals("")) && (!newPassword.equals("")) &&
                !updateCheck(attributes,oldPassword,newPassword,currentUser)){
            return "redirect:/profile";
        }

        userService.updateCredentials(currentUser.getId(), passwordEncoder.encode(newPassword), newName);

        return "redirect:/profile";
    }

    @GetMapping("/about")
    public String about(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, Model model){
        DriveUser currentUser = getUser(user, principal);
        spaceAllocator(currentUser, model);

        model.addAttribute("user", currentUser);

        return "about";
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

    @PostMapping("/newUser")
    public String UserRegistration(RedirectAttributes attributes,
                                   @RequestParam(required = false, name = "email") String email,
                                   @RequestParam(required = false, name = "password") String password,
                                   @RequestParam(required = false, name = "name") String fullName,
                                   @RequestParam(required = false, name = "confirmPassword") String confirmPassword){
        if(!userCheck(email, attributes, password, confirmPassword)){
            return "redirect:/register";
        }

        String passHash = passwordEncoder.encode(password);
        factory.createUser(email, passHash, UserRole.USER, fullName, null);
        attributes.addAttribute("successRegistration", true);
        return "redirect:/login";
    }

    private DriveUser getUser(User user, OAuth2User principal) {
        if(user!= null){
            return userService.findByEmail(user.getUsername());
        } else {
            return userService.findByEmail((String) principal.getAttributes().get("email"));
        }
    }

    private void getFilesByKeyName(String keyName, Folder folder, Model model) {
        List<File> resList = new ArrayList<>();
        File file = folderService.getFileByName(keyName, folder);
        if(file != null){
            resList.add(file);
            model.addAttribute("files", resList);
        } else {
            model.addAttribute("noSuchFile", true);
            model.addAttribute("files", folder.getFiles());
        }
    }

    private Integer fileCounter(DriveUser driveUser){
        Drive drive = driveUser.getDrive();
        return drive.getFolderList().stream().mapToInt(a -> a.getFiles().size()).sum();
    }

    private void spaceAllocator(DriveUser currentUser, Model model){
        Long[] driveSpaces = new Long[3];
        driveSpaces[0] = (currentUser.getDrive().getDrivePlan().getSpace())/1024;
        driveSpaces[1] = (currentUser.getDrive().getDrivePlan().getSpace() - currentUser.getDrive().getSpaceLeft())/1024;

        Long spaceMax = currentUser.getDrive().getDrivePlan().getSpace();
        Long spaceLeft = currentUser.getDrive().getSpaceLeft();
        driveSpaces[2] = ((spaceMax-spaceLeft)*100)/spaceMax;

        model.addAttribute("percentage", driveSpaces[2]);
        model.addAttribute("maxSpace", driveSpaces[0]);
        model.addAttribute("usedSpace", driveSpaces[1]);

    }

    private boolean fileCheck(MultipartFile file, String username, RedirectAttributes attributes){
        boolean verified = true;
        Drive currentDrive = userService.findByEmail(username).getDrive();
        if(file.getSize() == 0){
            attributes.addAttribute("noFile", true);
            verified = false;
        } else if(file.getSize()/1024 > currentDrive.getSpaceLeft()){
            attributes.addAttribute("bigFile", true);
            verified = false;
        }
        return verified;
    }

    private Folder getFolder(String folderName, String username){
        DriveUser currentUser = userService.findByEmail(username);
        Drive currentDrive = currentUser.getDrive();
        return currentDrive.getFolderByName(folderName);
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
