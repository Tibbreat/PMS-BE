package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
    private String feeTitle;
    private Boolean isActive;
    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<ChildrenFee> childrenFees = new HashSet<>();
}
