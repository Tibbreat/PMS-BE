package sep490.g13.pms_be.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerConsultation extends Auditable<String> {
    private String customer;
    private String customerEmail;
    private String customerPhone;
    private String message;

    @ManyToOne
    private User responsible;
}
