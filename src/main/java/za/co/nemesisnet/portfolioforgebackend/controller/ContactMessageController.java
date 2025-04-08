package za.co.nemesisnet.portfolioforgebackend.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import za.co.nemesisnet.portfolioforgebackend.domain.dto.ContactMessageRequestDTO;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ContactMessageResponseDTO;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.UpdateReadStatusRequestDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException;
import za.co.nemesisnet.portfolioforgebackend.service.ContactMessageService;
import za.co.nemesisnet.portfolioforgebackend.service.UserDetailsImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
// Note: Different base paths for public vs private endpoints
// @CrossOrigin(origins = "http://localhost:5173") // Add later for frontend interaction
public class ContactMessageController {

    private final ContactMessageService contactMessageService;
    private static final Logger log = LoggerFactory.getLogger(ContactMessageController.class);

    // --- Public Endpoint ---

    /**
     * POST /api/v1/portfolios/{slugOrUsername}/contact : Submit a contact message to a specific portfolio owner.
     * This endpoint is PUBLIC.
     *
     * @param slugOrUsername The public slug or username of the recipient portfolio/user.
     * @param messageRequestDTO The message details from the contact form.
     * @return 201 Created on success, 404 Not Found if recipient doesn't exist, 400 Bad Request on validation error.
     */
    @PostMapping("/api/v1/portfolios/{slugOrUsername}/contact")
    public ResponseEntity<?> submitContactMessage(
            @PathVariable String slugOrUsername,
            @Valid @RequestBody ContactMessageRequestDTO messageRequestDTO) {

        log.info("Received contact message submission for recipient identifier: {}", slugOrUsername);
        try {
            // Service method handles finding the recipient and saving
            contactMessageService.saveMessage(slugOrUsername, messageRequestDTO);
            // Return a simple success message or the created DTO if preferred
            return ResponseEntity.status(HttpStatus.CREATED).body("Message sent successfully.");
            // Or return the DTO:
            // ContactMessageResponseDTO savedMessageDto = contactMessageService.saveMessage(slugOrUsername, messageRequestDTO);
            // return ResponseEntity.status(HttpStatus.CREATED).body(savedMessageDto);

        } catch (ResourceNotFoundException e) {
            log.warn("Failed to send message: Recipient '{}' not found.", slugOrUsername);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error submitting contact message for recipient '{}': {}", slugOrUsername, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while sending the message.");
        }
    }

    // --- Private Endpoints (Require Authentication) ---

    /**
     * GET /api/v1/me/contact-messages : Get all messages received by the currently logged-in user.
     */
    @GetMapping("/api/v1/me/contact-messages")
    public ResponseEntity<List<ContactMessageResponseDTO>> getMyMessages(Authentication authentication) {
        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Fetching contact messages for user ID: {}", currentUser.getId());

        List<ContactMessageResponseDTO> messages = contactMessageService.getMessagesForUser(currentUser.getId());
        return ResponseEntity.ok(messages);
    }

    /**
     * GET /api/v1/me/contact-messages/{messageId} : Get a specific message by ID received by the logged-in user.
     */
    @GetMapping("/api/v1/me/contact-messages/{messageId}")
    public ResponseEntity<ContactMessageResponseDTO> getMyMessageById(
            Authentication authentication,
            @PathVariable Long messageId) {

        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        log.info("Fetching contact message ID {} for user ID: {}", messageId, currentUser.getId());

        return contactMessageService.getMessageByIdForUser(messageId, currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/v1/me/contact-messages/{messageId} : Update the read status of a specific message.
     * Using PATCH as it's a partial update (only 'isRead' field).
     */
    @PatchMapping("/api/v1/me/contact-messages/{messageId}")
    public ResponseEntity<ContactMessageResponseDTO> updateMyMessageReadStatus(
            Authentication authentication,
            @PathVariable Long messageId,
            @Valid @RequestBody UpdateReadStatusRequestDTO readStatusDto) { // Use a dedicated DTO

        UserDetailsImpl currentUser = getUserDetails(authentication);
        if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // --- CORRECTION HERE ---
        // Use the getter generated by Lombok for the Boolean field
        log.info("Updating read status to {} for message ID {} for user ID: {}", readStatusDto.getIsRead(), messageId, currentUser.getId());

        return contactMessageService.updateMessageReadStatus(messageId, currentUser.getId(), readStatusDto.getIsRead()) // <<< USE getIsRead()
                .map(ResponseEntity::ok) // Return updated message DTO on success
                .orElse(ResponseEntity.notFound().build()); // Return 404 if message not found for user
    }


    // --- Helper Method ---
    private UserDetailsImpl getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            log.warn("Could not extract UserDetailsImpl from Authentication object.");
            return null;
        }
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
