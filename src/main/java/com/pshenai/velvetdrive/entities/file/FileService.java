package com.pshenai.velvetdrive.entities.file;

import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.folder.Folder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

@Service
public class FileService {

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public boolean addFile(MultipartFile file, Folder folder, Drive drive) throws IOException {
        String initialPath = setPath(drive, file);
        String[] fileName = initialPath.split("\\\\");
        java.io.File uploadedFile = new java.io.File(initialPath);
        uploadedFile.createNewFile();
        byte[] res = file.getBytes();
        writeByte(res, uploadedFile);

        String path = uploadedFile.getAbsolutePath();
        Long fileSize = Files.size(Path.of(path))/1024;
        com.pshenai.velvetdrive.entities.file.File driveFile =
                new com.pshenai.velvetdrive.entities.file.File(fileName[fileName.length-1],
                       fileSize , path, setBackground(), folder);
        fileRepository.save(driveFile);

        setFolderSize(folder, fileSize);
        drive.setSpaceLeft(drive.getSpaceLeft() - fileSize);
        return true;
    }

    private void setFolderSize(Folder folder, Long fileSize){
        if(folder.getFolderSize() == null){
            folder.setFolderSize(fileSize);
        } else {
            folder.setFolderSize(folder.getFolderSize() + fileSize);
        }
    }

    private String setPath(Drive drive, MultipartFile file) {
        String initialPath = drive.getDrivePath() + "\\" + file.getOriginalFilename();
        int count = 1;
        if(fileRepository.existsByPath(initialPath)){
            StringBuilder sb = new StringBuilder();
            do{
                sb.delete(0, sb.length());
                sb.append(initialPath);
                sb.insert(sb.length() - 4, "(" + count + ")");
                count++;
            } while(fileRepository.existsByPath(sb.toString()));
            return sb.toString();
        }
        return initialPath;
    }

    public static String setBackground(){
        java.io.File backsRepo = new java.io.File("C:\\Users\\Velvet X\\Documents" +
                "\\Java Studies\\Java Pro\\VelvetDrive\\src\\main\\resources\\static\\images\\backs");
        java.io.File[] backs = backsRepo.listFiles();
        Random random = new Random();
        int num = random.nextInt(4);
        String[] initialPath = backs[num].getAbsolutePath().split("\\\\");
        int l = initialPath.length;
        return initialPath[l-3] + "/" + initialPath[l-2] + "/" +initialPath [l-1];
    }


//    private void imageCheck(File driveFile, java.io.File file){
//        String fileExtension = file.getAbsolutePath()
//                .substring(file.getAbsolutePath().length() - 3);
//
//        if(fileExtension.equals("jpg") || fileExtension.equals("png")){
//
//        } else {
//            driveFile.setFileBg("images/fileBgs/dummy.jpg");
//        }
//    }

    private void writeByte(byte[] bytes, java.io.File file){
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }



}
