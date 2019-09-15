package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class Util {

    static Supplier<ResponseStatusException> notFound(UUID msg, Class entity) {
        return () ->  {
            log.error(entity + " " + msg + " not found");
            return new ResponseStatusException(HttpStatus.NOT_FOUND, msg + " not found");
        };
    }

    public static ResponseEntity<byte[]> download(AmazonS3 s3, String bucket, String key) throws IOException {
        S3Object s3Object = s3.getObject(bucket, key);
        InputStream is = s3Object.getObjectContent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteStreams.copy(is, baos);
        is.close();

        String[] parts = key.split("/");
        String filename = parts[parts.length - 1];
        log.info("filename is " + filename);

        return ResponseEntity.ok().contentType(contentType(key))
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + filename + "\"")
                .body(baos.toByteArray());
    }

    private static MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length-1];
        switch(type) {
            case "txt": return MediaType.TEXT_PLAIN;
            case "png": return MediaType.IMAGE_PNG;
            case "jpg": return MediaType.IMAGE_JPEG;
            default: return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public static void putFile(AmazonS3 s3, String bucket, String key, MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        s3.putObject(bucket, key, file.getInputStream(), objectMetadata);
    }

    public static void putFile(AmazonS3 s3, String bucket, String key, long size, InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(size);
        s3.putObject(bucket, key, inputStream, objectMetadata);
    }
}
