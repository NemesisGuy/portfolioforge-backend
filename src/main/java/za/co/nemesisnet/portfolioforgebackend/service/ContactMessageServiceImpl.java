package za.co.nemesisnet.portfolioforgebackend.service; // Use your service package

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
// Correct import paths assumed based on previous files

import za.co.nemesisnet.portfolioforgebackend.domain.ContactMessage;
import za.co.nemesisnet.portfolioforgebackend.domain.Portfolio;
import za.co.nemesisnet.portfolioforgebackend.domain.User;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ContactMessageRequestDTO;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ContactMessageResponseDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException;
import za.co.nemesisnet.portfolioforgebackend.repository.ContactMessageRepository;
import za.co.nemesisnet.portfolioforgebackend.repository.PortfolioRepository;
import za.co.nemesisnet.portfolioforgebackend.repository.UserRepository; // Import UserRepository

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactMessageServiceImpl implements ContactMessageService { // Ensure it implements the correct interface

    private final ContactMessageRepository contactMessageRepository;
    private final PortfolioRepository portfolioRepository; // Need this to find user by slug
    private final UserRepository userRepository; // Added dependency for fallback lookup
    private static final Logger log = LoggerFactory.getLogger(ContactMessageServiceImpl.class);

    /**
     * Saves a contact message sent to a user identified by slug (primary) or username (fallback).
     */
    @Override
    @Transactional
    public ContactMessageResponseDTO saveMessage(String recipientSlugOrUsername, ContactMessageRequestDTO messageDto) {
        log.info("Attempting to save message for recipient identifier: {}", recipientSlugOrUsername);

        // 1. Find the recipient User
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByPublicSlug(recipientSlugOrUsername);

        User recipientUser;
        if (portfolioOpt.isPresent()) {
            // Ensure portfolio has a user associated
            if (portfolioOpt.get().getUser() == null) {
                log.error("Portfolio found for slug '{}', but associated user is null.", recipientSlugOrUsername);
                // Throwing ResourceNotFound seems appropriate as the target entity setup is incomplete/invalid
                throw new ResourceNotFoundException("Recipient user configuration error for identifier: " + recipientSlugOrUsername);
            }
            recipientUser = portfolioOpt.get().getUser();
            log.info("Found recipient user ID {} via portfolio slug '{}'", recipientUser.getId(), recipientSlugOrUsername);
        } else {
            // Fallback: Try finding user directly by username
            log.debug("Portfolio not found by slug '{}', attempting lookup by username.", recipientSlugOrUsername);
            recipientUser = userRepository.findByUsername(recipientSlugOrUsername)
                    .orElseThrow(() -> {
                        log.warn("Recipient user not found by slug or username: {}", recipientSlugOrUsername);
                        return new ResourceNotFoundException(
                                "Recipient portfolio/user not found for identifier: " + recipientSlugOrUsername);
                    });
            log.info("Found recipient user ID {} via username '{}'", recipientUser.getId(), recipientSlugOrUsername);
        }

        // 2. Create and populate the ContactMessage entity
        ContactMessage message = new ContactMessage();
        message.setRecipient(recipientUser);
        message.setSenderName(messageDto.getSenderName());
        message.setSenderEmail(messageDto.getSenderEmail());
        message.setSubject(messageDto.getSubject());
        message.setMessage(messageDto.getMessage());
        message.setRead(false);

        // 3. Save the message
        ContactMessage savedMessage = contactMessageRepository.save(message);
        log.info("Successfully saved contact message ID {} for recipient user ID {}", savedMessage.getId(), recipientUser.getId());

        // 4. Convert to DTO for response
        return convertToDto(savedMessage);
    }

    /**
     * Retrieves all messages for a specific user.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ContactMessageResponseDTO> getMessagesForUser(Long recipientUserId) {
        log.debug("Fetching messages for user ID: {}", recipientUserId);
        List<ContactMessage> messages = contactMessageRepository.findByRecipientIdOrderBySubmissionDateDesc(recipientUserId);
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific message by ID for a specific user.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ContactMessageResponseDTO> getMessageByIdForUser(Long messageId, Long recipientUserId) {
        log.debug("Fetching message ID {} for user ID: {}", messageId, recipientUserId);
        return contactMessageRepository.findByIdAndRecipientId(messageId, recipientUserId)
                .map(this::convertToDto);
    }

    /**
     * Updates the read status of a specific message for a specific user.
     */
    @Override
    @Transactional
    public Optional<ContactMessageResponseDTO> updateMessageReadStatus(Long messageId, Long recipientUserId, boolean isRead) {
        log.info("Updating read status to {} for message ID {} for user ID {}", isRead, messageId, recipientUserId);
        // Find message ensuring ownership
        Optional<ContactMessage> messageOpt = contactMessageRepository.findByIdAndRecipientId(messageId, recipientUserId);

        if (messageOpt.isPresent()) {
            ContactMessage message = messageOpt.get();
            // Only save if status actually changed
            if (message.isRead() != isRead) {
                message.setRead(isRead);
                ContactMessage updatedMessage = contactMessageRepository.save(message);
                return Optional.of(convertToDto(updatedMessage));
            } else {
                // Status hasn't changed, return current state without saving
                return Optional.of(convertToDto(message));
            }
        } else {
            log.warn("Message ID {} not found for user ID {} during read status update.", messageId, recipientUserId);
            // Return empty as per interface contract if not found
            return Optional.empty();
        }
    }

    // --- Helper DTO Conversion ---
    /**
     * Converts a ContactMessage entity to a ContactMessageResponseDTO.
     */
    private ContactMessageResponseDTO convertToDto(ContactMessage message) {
        ContactMessageResponseDTO dto = new ContactMessageResponseDTO();
        dto.setId(message.getId());
        dto.setSenderName(message.getSenderName());
        dto.setSenderEmail(message.getSenderEmail());
        dto.setSubject(message.getSubject());
        dto.setMessage(message.getMessage());
        dto.setSubmissionDate(message.getSubmissionDate());
        dto.setRead(message.isRead());
        return dto;
    }
}