package com.pshenai.velvetdrive.entities.folder;

import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.file.File;
import com.pshenai.velvetdrive.entities.file.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FolderService {

    private final FolderRepository folderRepository;

    public FolderService(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Transactional
    public void deleteFile(String filePath, Long folderId){
        Optional<Folder> folderOptional = folderRepository.findById(folderId);
        Optional<File> file = folderOptional.get().getFiles().stream().filter(a -> a.getPath().equals(filePath)).findFirst();
        folderOptional.get().getFiles().removeIf(a -> a.equals(file.get()));
        Drive drive = folderOptional.get().getDrive();

        drive.setSpaceLeft(drive.getSpaceLeft() + file.get().getFileSize());
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
            drive.getFolderList().removeIf(a -> a.getId().equals(folderId));
            folder.deleteAllFiles();
            folderRepository.delete(folder);
        }
    }
}
