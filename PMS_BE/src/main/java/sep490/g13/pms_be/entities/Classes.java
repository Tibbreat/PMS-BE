package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classes extends Auditable<String> {
    private String className;

    private String ageRange;

    private Date openingDay;

    private Date closingDay;

    @OneToMany
    private Set<Children> children;

    @OneToMany(mappedBy = "schoolClasses")
    private Set<ClassTeacher> teachers;

    @OneToOne
    private User manager;

    @OneToMany(mappedBy = "schoolClasses")
    private Set<DailyMenu> dailyMenus;
}
