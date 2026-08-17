package com.tss.shorty.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryService
{
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary)
    {
        this.cloudinary = cloudinary;
    }

    public String uploadProfilePicture(MultipartFile file) throws IOException
    {
        if (file == null || file.isEmpty())
        {
            throw new IllegalArgumentException("File cannot be empty");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "shorty/profile_pictures",
                        "resource_type", "image"
                )
        );
        return uploadResult.get("secure_url").toString();
    }

    public void deleteImageFromCloudinary(String imageUrl)
    {
        if (imageUrl == null || imageUrl.isBlank())
        {
            return;
        }
        try
        {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Successfully deleted old image from Cloudinary: {}", publicId);
        } catch (Exception e)
        {
            log.error("Failed to delete old image from Cloudinary: {}", e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl)
    {
        String[] parts = imageUrl.split("/");
        String filenameWithExt = parts[parts.length - 1];
        String folder = parts[parts.length - 2];
        String parentFolder = parts[parts.length - 3];
        String filename = filenameWithExt.substring(0, filenameWithExt.lastIndexOf('.'));
        return parentFolder + "/" + folder + "/" + filename;
    }
}