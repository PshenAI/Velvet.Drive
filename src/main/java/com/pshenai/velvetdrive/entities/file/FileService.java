package com.pshenai.velvetdrive.entities.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.pshenai.velvetdrive.configs.BucketName;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.folder.Folder;
import com.pshenai.velvetdrive.entities.folder.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

@Service
public class FileService {

    private final FileRepository fileRepository;

    private final FolderRepository folderRepository;
    private final AmazonS3 amazonS3;

    public FileService(FileRepository fileRepository, FolderRepository folderRepository, AmazonS3 amazonS3) {
        this.fileRepository = fileRepository;
        this.folderRepository = folderRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public void addFile(MultipartFile file, Folder folder, Drive drive) throws IOException {
        String[] initialPath = setPathAndName(drive, file);
        Double fileSize = ((double)file.getSize())/1_048_576;
        com.pshenai.velvetdrive.entities.file.File driveFile =
                new com.pshenai.velvetdrive.entities.file.File(initialPath[2],
                       fileSize , initialPath[1] + "/" + initialPath[2], setBackground(), folder);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        amazonS3.putObject(initialPath[0] + "/" + initialPath[1], initialPath[2], file.getInputStream(), objectMetadata);
        fileRepository.save(driveFile);

        setFolderSize(folder, fileSize);
        drive.setSpaceLeft(drive.getSpaceLeft() - fileSize);
    }

    @Transactional
    public File getFileByName(String email, String fileName){
        String filePath = email + "/" + fileName;
        return fileRepository.findByPath(filePath);
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

    public S3ObjectInputStream getFileInputStream(String path) {
        S3Object s3Object = amazonS3.getObject(BucketName.MAIN_BUCKET.getBucketName(), path);
        return s3Object.getObjectContent();
    }

    private void setFolderSize(Folder folder, Double fileSize){
        if(folder.getFolderSize() == null){
            folder.setFolderSize(fileSize);
        } else {
            folder.setFolderSize(folder.getFolderSize() + fileSize);
        }
    }

    private String[] setPathAndName(Drive drive, MultipartFile file) { //makes each file path and name unique
        String initialPath = drive.getDrivePath() + file.getOriginalFilename();
        String filePath = drive.getDriveUser().getEmail() + "/" + file.getOriginalFilename();
        int count = 1;
        if(fileRepository.existsByPath(filePath)){
            StringBuilder sb = new StringBuilder();
            do{
                sb.delete(0, sb.length());
                sb.append(initialPath);
                sb.insert(sb.length() - 4, "(" + count + ")");
                count++;
                String[] temp = sb.toString().split("/");
                filePath = temp[1] + "/" + temp[2];
            } while(fileRepository.existsByPath(filePath));
            initialPath =  sb.toString();
        }
        return initialPath.split("/");
    }

    public static String setBackground(){
        Random random = new Random();
        int num = random.nextInt(4);
        return "images/backs/" + num + ".jpg";
    }
}
