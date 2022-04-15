package com.pshenai.velvetdrive.entities.folder;

import com.amazonaws.services.s3.AmazonS3;
import com.pshenai.velvetdrive.configs.BucketName;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.file.File;
import com.pshenai.velvetdrive.entities.file.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final AmazonS3 amazonS3;

    public FolderService(FolderRepository folderRepository, AmazonS3 amazonS3) {
        this.folderRepository = folderRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public void deleteFile(String filePath, Long folderId){
        Folder folder= folderRepository.findById(folderId).get();
        File file = folder.getFiles().stream().filter(a -> a.getPath().equals(filePath)).findFirst().get();
        amazonS3.deleteObject(BucketName.MAIN_BUCKET.getBucketName(), file.getPath());
        folder.getFiles().removeIf(a -> a.equals(file));
        Drive drive = folder.getDrive();

        folder.setFolderSize(folder.getFolderSize() - file.getFileSize());
        drive.setSpaceLeft(drive.getSpaceLeft() + file.getFileSize());
    }

    @Transactional
    public boolean createFolder(String folderName, Drive drive) {
        if(!drive.folderExistsByName(folderName)){
            Folder newFolder = new Folder(folderName, drive, FileService.setBackground());
            folderRepository.save(newFolder);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteFolder(Long folderId){
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        if(folderOptional.isPresent()){
            Folder folder = folderOptional.get();
            Drive drive = folder.getDrive();
            folder.getFiles().forEach(a ->{
                amazonS3.deleteObject(BucketName.MAIN_BUCKET.getBucketName(), a.getPath());
            });
            drive.getFolderList().removeIf(a -> a.getId().equals(folderId));
            if(folder.getFolderSize() != null){
                drive.setSpaceLeft(drive.getSpaceLeft() + folder.getFolderSize());
            }
            folderRepository.delete(folder);
        }
    }

    @Transactional
    public void moveToBin(String fileName, Folder binFolder, Folder currentFolder, Drive drive) {
        File beforeFile;
        if (currentFolder == null) {
            List<Folder> folders = drive.getFolderList();
            currentFolder = folders.stream().filter(a -> fileExistsByName(fileName, a)).findFirst().get();
        }
        beforeFile = getFileByName(fileName, currentFolder);
        File afterFile = new File(fileName, beforeFile.getFileSize(), beforeFile.getPath(), beforeFile.getBackUrl(), binFolder);
        afterFile.setLastFolder(currentFolder.getName());
        binFolder.getFiles().add(afterFile);
        if(binFolder.getFolderSize() != null){
            binFolder.setFolderSize(binFolder.getFolderSize() + afterFile.getFileSize());
        } else {
            binFolder.setFolderSize(afterFile.getFileSize());
        }
        currentFolder.setFolderSize(currentFolder.getFolderSize() - beforeFile.getFileSize());
        currentFolder.getFiles().removeIf(a -> a.getName().equals(fileName));
    }

    @Transactional
    public void moveFromBin(String fileName, Folder binFolder, Drive drive) {
        File beforeFile = getFileByName(fileName, binFolder);
        Folder toFolder;
        if(drive.getFolderByName(beforeFile.getLastFolder()) != null){
            toFolder = drive.getFolderByName(beforeFile.getLastFolder());
        } else {
            toFolder = drive.getFolderByName("Default");
        }

        if(toFolder.getFolderSize() != null){
            toFolder.setFolderSize(toFolder.getFolderSize() + beforeFile.getFileSize());
        } else {
            toFolder.setFolderSize(beforeFile.getFileSize());
        }
        File afterFile = new File(fileName, beforeFile.getFileSize(), beforeFile.getPath(), beforeFile.getBackUrl(), toFolder);
        toFolder.getFiles().add(afterFile);
        binFolder.getFiles().removeIf(a -> a.getName().equals(fileName));
        binFolder.setFolderSize(binFolder.getFolderSize() - afterFile.getFileSize());
    }

    public Boolean fileExistsByName(String fileName, Folder folder){
        Optional<File> result = folder.getFiles().stream().filter(a -> a.getName().equals(fileName)).findFirst();
        return result.isPresent();
    }

    public File getFileByName(String fileName, Folder folder){
        return folder.getFiles().stream().filter(a -> a.getName().equals(fileName)).findFirst().get();
    }

    public Boolean fileExistsByKeyName(String keyName, Folder folder) {
        Optional<File> result = folder.getFiles().stream().filter(a -> a.getName().contains(keyName)).findFirst();
        return result.isPresent();
    }

    public List<File> getFilesByKeyName(String keyName, Folder folder) {
        return folder.getFiles().stream().filter(a -> a.getName().toLowerCase().contains(keyName.toLowerCase())).toList();
    }
}
