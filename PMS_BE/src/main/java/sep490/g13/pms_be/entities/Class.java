package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Class extends Auditable<String> {
    private String className;

    @OneToMany
    private Set<Children> children;

    @OneToMany
    private Set<User> teachers;

    @OneToOne
    private User manager;

    @OneToMany
    private Set<DailyMenu> dailyMenus;
}
