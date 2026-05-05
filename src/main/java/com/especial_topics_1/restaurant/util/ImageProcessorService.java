package com.especial_topics_1.restaurant.util;

import com.especial_topics_1.restaurant.exception.BusinessException;
import com.especial_topics_1.restaurant.storage.StorageService;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageProcessorService {
    private final StorageService storageService;

    public String processAndUpload(MultipartFile file, int width, int height) {
        List<String> allowedTypes = List.of("image/jpeg", "image/png");
        if (!allowedTypes.contains(file.getContentType())) {
            throw new BusinessException("Formato inválido. JPG ou PNG apenas.");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // 2. Processamento com Thumbnailator usando parâmetros dinâmicos
            Thumbnails.of(file.getInputStream())
                    .size(width, height)
                    .crop(Positions.CENTER) // Garante que a imagem fique quadrada sem esticar
                    .outputFormat("jpg")
                    .outputQuality(0.75) // 75% de qualidade já é excelente e economiza muito espaço
                    .toOutputStream(outputStream);

            byte[] imageData = outputStream.toByteArray();

            String fileName = UUID.randomUUID() + ".jpg"; // Melhor usar UUID para evitar conflitos
            return storageService.uploadImage(imageData, fileName);

        } catch (IOException e) {
            throw new BusinessException("Erro ao processar imagem: " + e.getMessage());
        }
    }
}
