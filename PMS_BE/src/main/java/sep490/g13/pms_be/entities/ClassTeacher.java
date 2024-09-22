package sep490.g13.pms_be.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ClassTeacher extends Auditable<String> {
    @ManyToOne
    @JoinColumn(name = "class_id")
    @JsonBackReference // Điều này sẽ ngăn Jackson serialize phía ngược lại
    private Classes schoolClasses;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnore // Loại bỏ khỏi JSON
    private User teacherId;
}
