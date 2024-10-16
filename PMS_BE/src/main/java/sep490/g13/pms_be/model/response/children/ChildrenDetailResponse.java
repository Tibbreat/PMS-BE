package sep490.g13.pms_be.model.response.children;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.model.request.RelationshipRequest;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChildrenDetailResponse {

    private String childName;

    private Integer childAge;

    private LocalDate childBirthDate;

    private String childAddress;

    private String classId;
    private Boolean isRegisterForBoarding;
    private Boolean isRegisterForTransport;
    private List<RelationshipRequest> relationships;
    private String imageUrl;
    private String people;
    private String identificationNumber;
    private String nationality;
    private String birthAddress;
}
