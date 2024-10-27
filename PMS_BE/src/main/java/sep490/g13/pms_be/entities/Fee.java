package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Fee extends Auditable<String> {
    private String feeItemName;
    private Double amount;

    private String academicYear;
    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
