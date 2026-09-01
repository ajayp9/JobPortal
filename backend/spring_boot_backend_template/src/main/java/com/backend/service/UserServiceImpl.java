package com.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.ChangePasswordRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.request.UpdateProfileRequest;
import com.backend.dto.response.ResumeResponse;
import com.backend.dto.response.UserResponse;
import com.backend.entities.Niche;
import com.backend.entities.Role;
import com.backend.entities.User;
import com.backend.entities.UserNiche;
import com.backend.entities.UserResume;
import com.backend.exception.BadRequestException;
import com.backend.exception.ResourceNotFoundException;
import com.backend.repository.NicheRepository;
import com.backend.repository.UserNicheRepository;
import com.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final NicheRepository nicheRepository;
    private final UserNicheRepository userNicheRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final RegistrationEmailClient registrationEmailClient;

    /*
     * Register a new user.
     *
     * JOB_SEEKER:
     * - Resume is required.
     * - Three niches are required.
     * - Cover letter is optional.
     *
     * EMPLOYER:
     * - Resume is not required.
     * - Niches are not required.
     * - Cover letter is not required.
     */
    @Override
    public UserResponse register(
            RegisterRequest request,
            MultipartFile resume) {

        if (request == null) {
            throw new BadRequestException(
                    "Registration request is required"
            );
        }

        if (request.getEmail() == null
                || request.getEmail().isBlank()) {

            throw new BadRequestException(
                    "Email is required"
            );
        }

        if (request.getRole() == null) {
            throw new BadRequestException(
                    "Role is required"
            );
        }

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException(
                    "Email is already registered"
            );
        }

        /*
         * Validate niches only for job seekers.
         */
        if (request.getRole() == Role.JOB_SEEKER) {
            validateRegisterNiches(request);
        }

        User user = new User();

        user.setName(
                request.getName().trim()
        );

        user.setEmail(email);

        user.setPhone(
                request.getPhone().trim()
        );

        user.setAddress(
                request.getAddress().trim()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        /*
         * Cover letter is optional for both roles.
         */
        if (request.getCoverLetter() != null
                && !request.getCoverLetter().isBlank()) {

            user.setCoverLetter(
                    request.getCoverLetter().trim()
            );

        } else {

            user.setCoverLetter(null);
        }

        user.setRole(
                request.getRole()
        );

        user.setActive(true);

        /*
         * Resume is required only for job seekers.
         */
        if (request.getRole() == Role.JOB_SEEKER) {
            addResumeToUser(user, resume);
        }

        User savedUser = userRepository.save(user);
        

        /*
         * Save niches only for job seekers.
         */
        if (request.getRole() == Role.JOB_SEEKER) {

            saveUserNiche(
                    savedUser,
                    request.getFirstNiche()
            );

            saveUserNiche(
                    savedUser,
                    request.getSecondNiche()
            );

            saveUserNiche(
                    savedUser,
                    request.getThirdNiche()
            );
        }

        userRepository.flush();

        User refreshedUser = userRepository
                .findById(savedUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found after registration"
                        )
                );

        /*
         * Call the .NET email microservice only after
         * user, resume and niches are saved successfully.
         */
        registrationEmailClient.sendEmail(refreshedUser);

        return convertToResponse(refreshedUser);
    }

    /*
     * Validate all three niches during job-seeker registration.
     */
    private void validateRegisterNiches(
            RegisterRequest request) {

        String firstNiche =
                normalizeNicheName(
                        request.getFirstNiche()
                );

        String secondNiche =
                normalizeNicheName(
                        request.getSecondNiche()
                );

        String thirdNiche =
                normalizeNicheName(
                        request.getThirdNiche()
                );

        validateDifferentNiches(
                firstNiche,
                secondNiche,
                thirdNiche
        );
    }

    /*
     * Validate all three niches during profile update.
     */
    private void validateUpdateNiches(
            UpdateProfileRequest request) {

        String firstNiche =
                normalizeNicheName(
                        request.getFirstNiche()
                );

        String secondNiche =
                normalizeNicheName(
                        request.getSecondNiche()
                );

        String thirdNiche =
                normalizeNicheName(
                        request.getThirdNiche()
                );

        validateDifferentNiches(
                firstNiche,
                secondNiche,
                thirdNiche
        );
    }

    /*
     * Check that all three niche names are different.
     */
    private void validateDifferentNiches(
            String firstNiche,
            String secondNiche,
            String thirdNiche) {

        if (firstNiche.equalsIgnoreCase(secondNiche)
                || firstNiche.equalsIgnoreCase(thirdNiche)
                || secondNiche.equalsIgnoreCase(thirdNiche)) {

            throw new BadRequestException(
                    "Please enter three different niches"
            );
        }
    }

    /*
     * Trim and validate a niche name.
     */
    private String normalizeNicheName(
            String nicheName) {

        if (nicheName == null
                || nicheName.isBlank()) {

            throw new BadRequestException(
                    "All three niches are required"
            );
        }

        return nicheName.trim();
    }

    /*
     * Find an existing niche or create a new niche.
     * Then create a mapping between the user and niche.
     */
    private void saveUserNiche(
            User user,
            String nicheName) {

        String normalizedNicheName =
                normalizeNicheName(nicheName);

        Niche niche = nicheRepository
                .findByNameIgnoreCase(
                        normalizedNicheName
                )
                .orElseGet(() -> {

                    Niche newNiche =
                            new Niche();

                    newNiche.setName(
                            normalizedNicheName
                    );

                    return nicheRepository.save(
                            newNiche
                    );
                });

        UserNiche userNiche =
                new UserNiche();

        userNiche.setUser(user);
        userNiche.setNiche(niche);

        UserNiche savedUserNiche =
                userNicheRepository.save(
                        userNiche
                );

        user.getUserNiches().add(
                savedUserNiche
        );
    }

    /*
     * Check whether an email is already registered.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(
            String email) {

        if (email == null
                || email.isBlank()) {

            return false;
        }

        return userRepository.existsByEmail(
                email.trim().toLowerCase()
        );
    }

    /*
     * Upload and attach a resume to a job seeker.
     */
    private void addResumeToUser(
            User user,
            MultipartFile resume) {

        if (resume == null
                || resume.isEmpty()) {

            throw new BadRequestException(
                    "Resume is required for job seeker"
            );
        }

        Map<String, Object> uploadResult =
                cloudinaryService.uploadResume(
                        resume
                );

        Object secureUrl =
                uploadResult.get("secure_url");

        Object publicId =
                uploadResult.get("public_id");

        if (secureUrl == null
                || publicId == null) {

            throw new BadRequestException(
                    "Cloudinary did not return resume details"
            );
        }

        UserResume userResume =
                new UserResume();

        userResume.setUser(user);

        userResume.setResumeUrl(
                secureUrl.toString()
        );

        userResume.setPublicId(
                publicId.toString()
        );

        user.getResumes().add(
                userResume
        );
    }

    /*
     * Get the currently authenticated user.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(
            String email) {

        User user =
                findByEmail(email);

        return convertToResponse(user);
    }

    /*
     * Partially update the currently authenticated user.
     */
    @Override
    public UserResponse updateCurrentUser(
            String email,
            UpdateProfileRequest request) {

        User user =
                findByEmail(email);

        /*
         * Update name only when provided.
         */
        if (request.getName() != null
                && !request.getName().isBlank()) {

            user.setName(
                    request.getName().trim()
            );
        }

        /*
         * Update phone only when provided.
         */
        if (request.getPhone() != null
                && !request.getPhone().isBlank()) {

            user.setPhone(
                    request.getPhone().trim()
            );
        }

        /*
         * Update address only when provided.
         */
        if (request.getAddress() != null
                && !request.getAddress().isBlank()) {

            user.setAddress(
                    request.getAddress().trim()
            );
        }

        /*
         * Cover letter can be updated or cleared.
         */
        if (request.getCoverLetter() != null) {

            String coverLetter =
                    request.getCoverLetter().trim();

            if (coverLetter.isBlank()) {
                user.setCoverLetter(null);
            } else {
                user.setCoverLetter(coverLetter);
            }
        }

        boolean firstNicheProvided =
                request.getFirstNiche() != null
                        && !request.getFirstNiche()
                        .isBlank();

        boolean secondNicheProvided =
                request.getSecondNiche() != null
                        && !request.getSecondNiche()
                        .isBlank();

        boolean thirdNicheProvided =
                request.getThirdNiche() != null
                        && !request.getThirdNiche()
                        .isBlank();

        boolean nicheUpdateRequested =
                firstNicheProvided
                        || secondNicheProvided
                        || thirdNicheProvided;

        /*
         * Process niche updates only when niche data is sent.
         */
        if (nicheUpdateRequested) {

            /*
             * Employers are not allowed to update niches.
             */
            if (user.getRole() != Role.JOB_SEEKER) {

                throw new BadRequestException(
                        "Niches can be updated only by job seekers"
                );
            }

            /*
             * All three niches must be provided together.
             */
            if (!firstNicheProvided
                    || !secondNicheProvided
                    || !thirdNicheProvided) {

                throw new BadRequestException(
                        "To update niches, provide all three niches"
                );
            }

            validateUpdateNiches(request);

            /*
             * Delete all old niche mappings of this user.
             */
            userNicheRepository.deleteByUserId(
                    user.getId()
            );

            /*
             * Immediately execute the delete query
             * before inserting new niche mappings.
             */
            userNicheRepository.flush();

            /*
             * Clear old niche objects from the User entity.
             */
            user.getUserNiches().clear();

            /*
             * Save the new user-niche mappings.
             */
            saveUserNiche(
                    user,
                    request.getFirstNiche()
            );

            saveUserNiche(
                    user,
                    request.getSecondNiche()
            );

            saveUserNiche(
                    user,
                    request.getThirdNiche()
            );
        }

        User updatedUser =
                userRepository.save(user);

        userRepository.flush();

        User refreshedUser = userRepository
                .findById(updatedUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found after update"
                        )
                );

        return convertToResponse(refreshedUser);
    }

    /*
     * Change the current user's password.
     */
    @Override
    public void changePassword(
            String email,
            ChangePasswordRequest request) {

        User user =
                findByEmail(email);

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "Current password is incorrect"
            );
        }

        if (!request.getNewPassword()
                .equals(
                        request.getConfirmPassword()
                )) {

            throw new BadRequestException(
                    "New password and confirm password do not match"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new BadRequestException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    /*
     * JWT logout is handled by deleting the JWT token
     * from the frontend or Postman.
     */
    @Override
    public void logout() {

        /*
         * JWT authentication is stateless.
         * No database operation is required here.
         */
    }

    /*
     * Get all registered users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        List<User> users =
                userRepository.findAll();

        List<UserResponse> responses =
                new ArrayList<>();

        for (User user : users) {

            responses.add(
                    convertToResponse(user)
            );
        }

        return responses;
    }

    /*
     * Get a user by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(
            Long id) {

        User user = userRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + id
                        )
                );

        return convertToResponse(user);
    }

    /*
     * Find a user using their email address.
     */
    private User findByEmail(
            String email) {

        if (email == null
                || email.isBlank()) {

            throw new BadRequestException(
                    "User email is required"
            );
        }

        String normalizedEmail =
                email.trim().toLowerCase();

        return userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: "
                                        + normalizedEmail
                        )
                );
    }

    /*
     * Convert User entity to UserResponse DTO.
     */
    private UserResponse convertToResponse(
            User user) {

        UserResponse response =
                new UserResponse();

        response.setId(
                user.getId()
        );

        response.setName(
                user.getName()
        );

        response.setEmail(
                user.getEmail()
        );

        response.setPhone(
                user.getPhone()
        );

        response.setAddress(
                user.getAddress()
        );

        response.setCoverLetter(
                user.getCoverLetter()
        );

        response.setRole(
                user.getRole()
        );

        response.setActive(
                user.getActive()
        );

        response.setCreatedAt(
                user.getCreatedAt()
        );

        response.setUpdatedAt(
                user.getUpdatedAt()
        );

        /*
         * Convert resume entities to resume response DTOs.
         */
        List<ResumeResponse> resumeResponses =
                new ArrayList<>();

        if (user.getResumes() != null) {

            for (UserResume resume
                    : user.getResumes()) {

                ResumeResponse resumeResponse =
                        new ResumeResponse();

                resumeResponse.setId(
                        resume.getId()
                );

                resumeResponse.setPublicId(
                        resume.getPublicId()
                );

                resumeResponse.setResumeUrl(
                        resume.getResumeUrl()
                );

                resumeResponses.add(
                        resumeResponse
                );
            }
        }

        response.setResumes(
                resumeResponses
        );

        /*
         * Convert user-niche mappings to niche names.
         */
        List<String> nicheNames =
                new ArrayList<>();

        if (user.getUserNiches() != null) {

            for (UserNiche userNiche
                    : user.getUserNiches()) {

                if (userNiche.getNiche() != null) {

                    nicheNames.add(
                            userNiche
                                    .getNiche()
                                    .getName()
                    );
                }
            }
        }

        response.setNiches(
                nicheNames
        );

        return response;
    }
}