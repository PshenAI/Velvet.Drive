package com.pshenai.velvetdrive.entities.file;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.pshenai.velvetdrive.configs.BucketName;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.folder.Folder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 amazonS3;

    public FileService(FileRepository fileRepository, AmazonS3 amazonS3) {
        this.fileRepository = fileRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public void addFile(MultipartFile file, Folder folder, Drive drive) throws IOException {
        String[] initialPath = setPathAndName(drive, file);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        Double fileSize = ((double)file.getSize())/1_048_576;
        com.pshenai.velvetdrive.entities.file.File driveFile =
                new com.pshenai.velvetdrive.entities.file.File(initialPath[2],
                       fileSize , initialPath[1] + "/" + initialPath[2], setBackground(), folder);
        upload(initialPath[0] + "/" + initialPath[1], initialPath[2], Optional.of(metadata),file.getInputStream());
        fileRepository.save(driveFile);

        setFolderSize(folder, fileSize);
        drive.setSpaceLeft(drive.getSpaceLeft() - fileSize);
    }

    @Transactional
    public File getFileByName(String email, String fileName){
        String filePath = email + "/" + fileName;
        return fileRepository.findByPath(filePath);
    }

    private void upload(String path,
                       String fileName,
                       Optional<Map<String, String>> optionalMetaData,
                       InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        optionalMetaData.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(objectMetadata::addUserMetadata);
            }
        });
        try {
            amazonS3.putObject(path, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to upload the file", e);
        }
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

    private String[] setPathAndName(Drive drive, MultipartFile file) {
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
