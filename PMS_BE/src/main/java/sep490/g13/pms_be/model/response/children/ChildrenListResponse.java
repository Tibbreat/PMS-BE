package sep490.g13.pms_be.model.response.children;

import lombok.*;
import sep490.g13.pms_be.model.request.RelationshipRequest;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChildrenListResponse {
    private String id;
    private String childName;
    private Integer childAge;
    private LocalDate childBirthDate;
    private String childAddress;
    private String classId;
    private String imageLink;
}
