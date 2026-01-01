package com.blassa.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif");

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB el upload max

    public String uploadProfilePicture(MultipartFile file, UUID userId) throws IOException {
        validateFile(file);
        String publicId = "blassa/profiles/" + userId.toString();

        // transformation automatique mn service cloudinary
        Transformation transformation = new Transformation()
                .width(400)
                .height(400)
                .crop("thumb")
                .gravity("face")
                .quality("auto:good")
                .fetchFormat("webp");

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", publicId,
                "overwrite", true,
                "transformation", transformation));

        String secureUrl = (String) uploadResult.get("secure_url");
        return secureUrl;
    }

    // DELETE Mn photo men cloudinary
    public void deleteProfilePicture(UUID userId) throws IOException {
        String publicId = "blassa/profiles/" + userId.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        String resultStatus = (String) result.get("result");
    }

    // Validati uploaded file
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Le fichier est trop volumineux (max 5 Mo)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non supporté. Utilisez JPEG, PNG, WebP ou GIF");
        }
    }
}
