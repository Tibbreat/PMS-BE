package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import sep490.g13.pms_be.utils.enums.RoleEnums;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Auditable<String> {
    private String username;
    private String password;

    private String fullName;
    private String email;
    private String address;
    private String phone;

    private String imageLink;

    private Boolean isActive;

    @ManyToOne
    private TransportServiceProvider transportServiceProvider;

    @Enumerated(EnumType.STRING)
    private RoleEnums role;
    @OneToMany(mappedBy = "teacherId")
    @JsonIgnore // Loại bỏ khỏi JSON để tránh vòng lặp
    private Set<ClassTeacher> classTeachers = new HashSet<>();
}
