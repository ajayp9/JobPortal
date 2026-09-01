package com.backend.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.exception.BadRequestException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private static final long MAX_RESUME_SIZE = 5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> uploadResume(MultipartFile resume) {

        validateResume(resume);

        try {
            String originalFileName = resume.getOriginalFilename();

            String extension = getFileExtension(originalFileName);

            String publicId = "resume_" + UUID.randomUUID() + extension;

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    resume.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "job-portal/resumes",
                            "resource_type", "raw",
                            "public_id", publicId,
                            "overwrite", false
                    )
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> result =
                    (Map<String, Object>) uploadResult;

            return result;

        } catch (IOException exception) {
            throw new BadRequestException(
                    "Resume upload failed. Please try again."
            );
        }
    }

    private void validateResume(MultipartFile resume) {

        if (resume == null || resume.isEmpty()) {
            throw new BadRequestException(
                    "Resume file is required for job seeker registration"
            );
        }

        if (resume.getSize() > MAX_RESUME_SIZE) {
            throw new BadRequestException(
                    "Resume size must not be greater than 5 MB"
            );
        }

        String fileName = resume.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException(
                    "Resume filename is missing"
            );
        }

        String lowerFileName = fileName.toLowerCase();

        boolean validExtension =
                lowerFileName.endsWith(".pdf")
                        || lowerFileName.endsWith(".doc")
                        || lowerFileName.endsWith(".docx");

        if (!validExtension) {
            throw new BadRequestException(
                    "Only PDF, DOC and DOCX resume files are allowed"
            );
        }
    }

    private String getFileExtension(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            throw new BadRequestException(
                    "Resume filename is missing"
            );
        }

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new BadRequestException(
                    "Resume file extension is missing"
            );
        }

        return fileName
                .substring(lastDotIndex)
                .toLowerCase();
    }
    public void deleteResume(String publicId) {

        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {

            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type",
                            "raw"
                    )
            );

        } catch (IOException exception) {

            throw new BadRequestException(
                    "Old resume could not be deleted"
            );
        }
    }
}