package za.co.nemesisnet.portfolioforgebackend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios") // Table name for user portfolios
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    // --- Portfolio Content Fields ---
    @Column(name = "about_me_text", columnDefinition = "TEXT")
    private String aboutMeText;

    @Size(max = 512)
    @Column(name = "resume_url", length = 512)
    private String resumeUrl;

    @Size(max = 255)
    @Column(name = "linkedin_url", length = 255)
    private String linkedInUrl;

    @Size(max = 255)
    @Column(name = "github_url", length = 255)
    private String githubUrl;

    @Email(message = "Please provide a valid contact email address")
    @Size(max = 255)
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    // Example: Unique public identifier for the portfolio (e.g., for portfolioforge.com/username)
    @Size(min=3, max = 50)
    @Column(name = "public_slug", length = 50, unique = true) // Make it unique if used
    private String publicSlug;

    // Add other fields as needed (e.g., profile picture URL, tagline)

    // --- Timestamps ---
    @UpdateTimestamp
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;

    // --- Relationships ---
    @OneToOne(fetch = FetchType.LAZY) // Lazy fetch User unless explicitly needed
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true) // Foreign key to users table
    private User user; // The user this portfolio belongs to


    // --- equals(), hashCode(), toString() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Portfolio portfolio = (Portfolio) o;
        return id != null && id.equals(portfolio.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) + // Avoid infinite loop with User.toString()
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}
