package za.co.nemesisnet.portfolioforgebackend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.nemesisnet.portfolioforgebackend.domain.ContactMessage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    /**
     * Finds all messages received by a specific user, ordered by submission date descending.
     * @param recipientId The ID of the recipient user.
     * @return A list of contact messages.
     */
    List<ContactMessage> findByRecipientIdOrderBySubmissionDateDesc(Long recipientId);

    /**
     * Finds a specific message by its ID and the ID of its recipient user.
     * Ensures users can only access messages sent to them.
     * @param id The ID of the message.
     * @param recipientId The ID of the recipient user.
     * @return An Optional containing the message if found and owned by the recipient.
     */
    Optional<ContactMessage> findByIdAndRecipientId(Long id, Long recipientId);

}
