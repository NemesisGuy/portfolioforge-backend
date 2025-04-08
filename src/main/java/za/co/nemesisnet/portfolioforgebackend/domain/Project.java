package za.co.nemesisnet.portfolioforgebackend.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "projects") // Specifies the table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project title cannot be blank")
    @Size(max = 100, message = "Project title must be less than 100 characters")
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank(message = "Project description cannot be blank")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(length = 255)
    private String technologies; // Simple comma-separated string for now

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "live_url", length = 512)
    private String liveUrl;

    @Column(name = "repo_url", length = 512)
    private String repoUrl;

    @Column(name = "display_order", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Relationship Added ---
    @ManyToOne(fetch = FetchType.LAZY) // Many projects belong to one user. Lazy fetch user.
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in 'projects' table. Required.
    private User user;
    // --- End of Relationship ---


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id != null && id.equals(project.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", userId=" + (user != null ? user.getId() : null) + // Avoid recursion
                ", createdAt=" + createdAt +
                '}';
    }
}