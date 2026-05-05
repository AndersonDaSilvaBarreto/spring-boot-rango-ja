package com.especial_topics_1.restaurant.storage;

import com.especial_topics_1.restaurant.exception.DataIntegrityException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final S3Client s3Client;
    
    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;

    public String uploadImage(byte[] fileData, String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType("image/jpeg") // Podes escrever isto à confiança!
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));
        return publicUrl + "/" + uniqueFileName;
    }
    public void deleteImage(String imageUrl) {
        if( imageUrl == null || imageUrl.isBlank()) {
            return;
        }
        try{
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new DataIntegrityException("Falha ao deletar a imagem antiga: " + e.getMessage());
        }
    }
}
