package com.pshenai.velvetdrive;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.pshenai.velvetdrive.configs.BucketName;
import com.pshenai.velvetdrive.entities.drive.Drive;
import com.pshenai.velvetdrive.entities.file.File;
import com.pshenai.velvetdrive.entities.folder.Folder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class VelvetDriveApplicationTests {


    void putFile() throws IOException {
        String bucketName = "velvet-drive-storage";
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(System.getenv("S3_KEY"), System.getenv("S3_SECRET"));

        AmazonS3 amazonS3 = AmazonS3ClientBuilder
        .standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("s3.filebase.com", "us-east-1"))
        .build();

        MultipartFile file = getMockFile();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        amazonS3.putObject(bucketName, file.getOriginalFilename(), file.getInputStream(), objectMetadata);
    }

    void deleteFile() throws IOException {

        String bucketName = "velvet-drive-storage";
        String originalFileName = "CV_JavaDev_Pshencyhnyi_Anton.pdf";

        AWSCredentials awsCredentials =
                new BasicAWSCredentials(System.getenv("S3_KEY"), System.getenv("S3_SECRET"));
        AmazonS3 amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("s3.filebase.com", "us-east-1"))
                .build();

        amazonS3.deleteObject(bucketName, originalFileName);
    }

    private MultipartFile getMockFile(){
        Path path = Paths.get("/home/pshenai/Downloads/CV_JavaDev_Pshencyhnyi_Anton.pdf");
        String name = "CV_JavaDev_Pshencyhnyi_Anton.pdf";
        String originalFileName = "CV_JavaDev_Pshencyhnyi_Anton.pdf";
        String contentType = "application/pdf";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
        }

        return new MockMultipartFile(name,
                originalFileName, contentType, content);
    }

    private String[] setPathAndName(MultipartFile file) { //makes each file path and name unique
        String initialPath = "/test/" + file.getOriginalFilename();
        return initialPath.split("/");
    }

}
