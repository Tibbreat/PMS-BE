package sep490.g13.pms_be.entities;

import jakarta.persistence.*;
import lombok.*;
import sep490.g13.pms_be.utils.enums.RoleEnums;

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
}
