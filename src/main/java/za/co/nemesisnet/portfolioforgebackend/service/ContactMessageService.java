package za.co.nemesisnet.portfolioforgebackend.service; // Use your service package


import za.co.nemesisnet.portfolioforgebackend.domain.dto.ContactMessageRequestDTO;
import za.co.nemesisnet.portfolioforgebackend.domain.dto.ContactMessageResponseDTO;
import za.co.nemesisnet.portfolioforgebackend.exception.ResourceNotFoundException; // Correct exception import

import java.util.List;
import java.util.Optional;

public interface ContactMessageService {

    /**
     * Saves a contact message sent to a specific user (identified by slug).
     * This is intended for the public contact form endpoint.
     *
     * @param recipientSlug The public slug of the user portfolio to send the message to.
     * @param messageDto    The DTO containing the sender's details and message.
     * @return The saved ContactMessageResponseDTO.
     * @throws ResourceNotFoundException if no portfolio/user found for the slug.
     */
    ContactMessageResponseDTO saveMessage(String recipientSlug, ContactMessageRequestDTO messageDto);

    /**
     * Retrieves all messages received by a specific user.
     * Intended for the logged-in user to view their messages.
     *
     * @param recipientUserId The ID of the user whose messages are being requested.
     * @return A list of ContactMessageResponseDTOs, ordered by submission date descending.
     */
    List<ContactMessageResponseDTO> getMessagesForUser(Long recipientUserId);

    /**
     * Retrieves a specific message by its ID, ensuring it belongs to the specified recipient user.
     *
     * @param messageId     The ID of the message.
     * @param recipientUserId The ID of the user who should have received the message.
     * @return An Optional containing the ContactMessageResponseDTO if found and owned by the user.
     */
    Optional<ContactMessageResponseDTO> getMessageByIdForUser(Long messageId, Long recipientUserId);

    /**
     * Marks a specific message as read or unread.
     *
     * @param messageId     The ID of the message to update.
     * @param recipientUserId The ID of the user who owns the message.
     * @param isRead        The desired read status (true for read, false for unread).
     * @return An Optional containing the updated ContactMessageResponseDTO if found and updated. Returns empty if not found.
     */
    Optional<ContactMessageResponseDTO> updateMessageReadStatus(Long messageId, Long recipientUserId, boolean isRead);

    // Optional delete method signature if needed later
    // void deleteMessage(Long messageId, Long recipientUserId);
}