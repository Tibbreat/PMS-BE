package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import sep490.g13.pms_be.utlis.enums.RoleEnums;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends Auditable<String> {
    private String username;
    private String password;
    private String email;
    private String address;
    private String phone;

    private String imageLink;

    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private RoleEnums role;
}
