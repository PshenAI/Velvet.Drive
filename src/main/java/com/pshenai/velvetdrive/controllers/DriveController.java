package com.pshenai.velvetdrive.controllers;

import com.pshenai.velvetdrive.common.CommonUtil;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.drive.DriveAO;
import com.pshenai.velvetdrive.entities.drive.DrivePlan;
import com.pshenai.velvetdrive.entities.drive.DriveService;
import com.pshenai.velvetdrive.entities.file.File;
import com.pshenai.velvetdrive.entities.file.FileAO;
import com.pshenai.velvetdrive.entities.file.FileService;
import com.pshenai.velvetdrive.entities.folder.Folder;
import com.pshenai.velvetdrive.entities.folder.FolderService;
import com.pshenai.velvetdrive.entities.user.DriveUser;
import com.pshenai.velvetdrive.entities.user.UserService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DriveController {

    private final UserService userService;
    private final DriveService driveService;
    private final FolderService folderService;
    private final FileService fileService;
    private final CommonUtil commonUtil;


    @Autowired
    public DriveController(UserService userService, DriveService driveService, FolderService folderService,
                           FileService fileService, CommonUtil commonUtil) {
        this.userService = userService;
        this.driveService = driveService;
        this.folderService = folderService;
        this.fileService = fileService;
        this.commonUtil = commonUtil;
    }
    @GetMapping("/drive")
    public String drive(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, Model model,
                        DriveAO driveAO){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        Drive currentDrive = currentUser.getDrive();
        Folder currentFolder = getFolder(driveAO.getFolder(), currentUser.getEmail());
        commonUtil.spaceAllocator(currentUser, model);

        setContent(model, driveAO.isFolderList(),driveAO.getKeyName(), currentDrive, currentFolder, driveAO.getFolder());
        model.addAttribute("wrongName", driveAO.isWrongName());
        model.addAttribute("noFile", driveAO.isNoFile());
        model.addAttribute("user", currentUser);
        model.addAttribute("bigFile", driveAO.isBigFile());
        model.addAttribute("dupFolder", driveAO.isDuplicateFolder());
        return "drive";
    }

    @PostMapping("/file/upload")
    public String uploadFile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes attributes,
                         @RequestParam("file")MultipartFile file,
                         @RequestParam(name = "folder", defaultValue = "Default") String folderName) throws IOException {
        DriveUser currentUser = commonUtil.getUser(user, principal);
        Folder currentFolder = getFolder(folderName, currentUser.getEmail());
        if(!fileCheck(file, currentUser.getEmail(), attributes)){
            return "redirect:/drive?folder=" + currentFolder.getName();
        }
        fileService.addFile(file, currentFolder, currentFolder.getDrive());

        attributes.addAttribute("files", currentFolder.getFiles());
        attributes.addAttribute("folder", currentFolder.getName());

        return "redirect:/drive";
    }

    @GetMapping("/file/download")
    public ResponseEntity<byte[]> getFile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                                          FileAO fileAO) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders();
        DriveUser currentUser = commonUtil.getUser(user, principal);

        com.pshenai.velvetdrive.entities.file.File file = fileService.getFileByName(currentUser.getEmail(), fileAO.getFile());
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.setContentDispositionFormData("attachment",
                new String(fileAO.getFile().getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        return ResponseEntity.ok().headers(httpHeaders)
                .body(IOUtils.toByteArray(fileService.getFileInputStream(file.getPath())));
    }

    @DeleteMapping("/file/delete")
    public String deleteFile(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                             RedirectAttributes attributes, FileAO fileAO){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        Folder currentFolder = getFolder(fileAO.getFolder(), currentUser.getEmail());
        fileService.deleteFile(currentFolder.getFilePath(fileAO.getFile()), currentFolder.getId());
        attributes.addAttribute("folder", currentFolder.getName());

        return "redirect:/drive";
    }

    @PostMapping("/bin/insert")
    public String moveToBin(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                            RedirectAttributes attributes, FileAO fileAO){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        Folder currentFolder = getFolder(fileAO.getFolder(), currentUser.getEmail());
        Folder binFolder = getFolder("Bin", currentUser.getEmail());
        folderService.moveToBin(fileAO.getFile(), binFolder, currentFolder, currentUser.getDrive());
        if(currentFolder == null){
            return "redirect:/drive";
        }

        attributes.addAttribute("folder", currentFolder.getName());

        return "redirect:/drive";
    }

    @PostMapping("/bin/extract")
    public String moveFromBin(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                              RedirectAttributes attributes, FileAO fileAO){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        Folder binFolder = getFolder("Bin", currentUser.getEmail());
        folderService.moveFromBin(fileAO.getFile(), binFolder, currentUser.getDrive());
        attributes.addAttribute("folder",binFolder.getName());

        return "redirect:/drive";
    }

    @PostMapping("/folder/create")
    public String createFolder(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, RedirectAttributes attributes,
                               @RequestParam(name = "folderName") String folderName){
        if(folderName == null || folderName.equals("")){
            attributes.addAttribute("wrongName", true);
            return "redirect:/drive?folderList=true";
        }
        DriveUser currentUser = commonUtil.getUser(user, principal);
        Drive currentDrive = userService.findByEmail(currentUser.getEmail()).getDrive();
        if(folderService.createFolder(folderName, currentDrive)){
            return "redirect:/drive?folderList=true";
        }
        attributes.addAttribute("duplicateFolder", true);
        return "redirect:/drive?folderList=true";
    }

    @DeleteMapping("/folder/delete")
    public String deleteFolder(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal,
                               @RequestParam(name = "folder", defaultValue = "Default") String folderName){
        DriveUser currentUser = commonUtil.getUser(user, principal);
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
        DriveUser currentUser = commonUtil.getUser(user, principal);
        driveService.updateDrivePlan(drivePlan, currentUser.getDrive());
        return "redirect:/drive?folderList=true";
    }

    @GetMapping("/about")
    public String about(@AuthenticationPrincipal User user, @AuthenticationPrincipal OAuth2User principal, Model model){
        DriveUser currentUser = commonUtil.getUser(user, principal);
        commonUtil.spaceAllocator(currentUser, model);

        model.addAttribute("user", currentUser);

        return "about";
    }

    private void setContent(Model model, Boolean folderList, String keyName, Drive currentDrive, Folder currentFolder,
                            String folderName) { //fills drive page with content depending on request parameters
        if(folderList){
            model.addAttribute("folders",currentDrive.getFolderList());
        } else {
            if(!keyName.equals("Default")){
                getFilesByKeyName(keyName, currentDrive, model);
                folderName = "Results for query: " + keyName;
            } else {
                model.addAttribute("files", currentFolder.getFiles());
            }
            model.addAttribute("folder", folderName);
        }
    }

    private void getFilesByKeyName(String keyName, Drive drive, Model model) {
        List<Folder> folders = drive.getFolderList();
        folders = folders.stream().filter(a -> folderService.fileExistsByKeyName(keyName, a) != null).toList();
        List<File> resList = new ArrayList<>();
        if(folders != null){
            folders.forEach(a ->{
                if(!a.getName().equals("Bin")){
                    resList.addAll(folderService.getFilesByKeyName(keyName, a));
                }
            });
            model.addAttribute("files", resList);
        } else {
            model.addAttribute("noSuchFile", true);
            model.addAttribute("files", drive.getFolderList().get(0).getFiles());
        }
    }



    private boolean fileCheck(MultipartFile file, String username, RedirectAttributes attributes){
        boolean verified = true;
        Drive currentDrive = userService.findByEmail(username).getDrive();
        if(file.getSize() == 0){
            attributes.addAttribute("noFile", true);
            verified = false;
        } else if(file.getSize()/1_048_576d > currentDrive.getSpaceLeft()){
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
}
