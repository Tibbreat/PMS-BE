package sep490.g13.pms_be.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School extends Auditable<String> {
    private String schoolName;
    private String phoneContact;
    private String emailContact;
    @Column(name = "address")
    private String schoolAddress;


    @OneToOne
    private User principal;
}
