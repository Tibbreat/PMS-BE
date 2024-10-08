package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


import java.util.Date;
import java.util.HashSet;
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

    @OneToMany(mappedBy = "schoolClass", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Children> children = new HashSet<>();

    @OneToMany(mappedBy = "schoolClasses", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private Set<ClassTeacher> teachers = new HashSet<>();

    @ManyToOne
    private User manager;

    @OneToMany(mappedBy = "schoolClasses")
    private Set<DailyMenu> dailyMenus;

    private boolean status = false;
}
