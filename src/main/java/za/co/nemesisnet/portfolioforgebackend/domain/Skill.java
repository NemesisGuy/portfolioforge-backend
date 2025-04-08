package za.co.nemesisnet.portfolioforgebackend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Skill name cannot be blank")
    @Size(max = 100, message = "Skill name must be less than 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 100, message = "Category must be less than 100 characters")
    @Column(length = 100) // e.g., "Frontend", "Backend", "Database", "DevOps"
    private String category;

    @Size(max = 100, message = "Icon reference must be less than 100 characters")
    @Column(length = 100) // e.g., CSS class for an icon font like FontAwesome ("fab fa-java"), or an SVG name/path
    private String icon;

    // --- Relationship ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column in 'skills' table
    private User user;
    // --- End of Relationship ---


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill skill = (Skill) o;
        return id != null && id.equals(skill.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}