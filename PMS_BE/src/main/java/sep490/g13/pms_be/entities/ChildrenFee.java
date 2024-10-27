package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChildrenFee extends Auditable<String> {
    @ManyToOne
    @JoinColumn(name = "children_id")
    private Children children;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "fee_id")
    private Fee fee;

    private Boolean isPayed;

    private Date payedDate;

    @Lob
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
