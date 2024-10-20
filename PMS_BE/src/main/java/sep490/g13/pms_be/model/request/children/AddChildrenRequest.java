package sep490.g13.pms_be.model.request.children;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddChildrenRequest {
    private String childName;
    private LocalDate childBirthDate;
    private String childAddress;
    private String birthAddress;
    private String nationality;
    private String religion;
    private String gender;

    private AddParentRequest father;

    private AddParentRequest mother;

    private String createdBy;
}
