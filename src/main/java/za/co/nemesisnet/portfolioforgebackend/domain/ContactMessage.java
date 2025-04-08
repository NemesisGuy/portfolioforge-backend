package za.co.nemesisnet.portfolioforgebackend.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Sender name cannot be blank")
    @Size(max = 100)
    @Column(name = "sender_name", nullable = false, length = 100)
    private String senderName;

    @NotBlank(message = "Sender email cannot be blank")
    @Email(message = "Please provide a valid sender email address")
    @Size(max = 255)
    @Column(name = "sender_email", nullable = false, length = 255)
    private String senderEmail;

    @Size(max = 200)
    @Column(length = 200)
    private String subject;

    @NotBlank(message = "Message body cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @CreationTimestamp // Automatically set when the message is created
    @Column(name = "submission_date", nullable = false, updatable = false)
    private LocalDateTime submissionDate;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false; // Default to unread

    // --- Relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_user_id", nullable = false) // Foreign key to users table
    private User recipient; // The User (portfolio owner) who received the message
    // --- End of Relationship ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactMessage that = (ContactMessage) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ContactMessage{" +
                "id=" + id +
                ", senderName='" + senderName + '\'' +
                ", subject='" + subject + '\'' +
                ", recipientId=" + (recipient != null ? recipient.getId() : null) +
                ", submissionDate=" + submissionDate +
                ", isRead=" + isRead +
                '}';
    }
}
