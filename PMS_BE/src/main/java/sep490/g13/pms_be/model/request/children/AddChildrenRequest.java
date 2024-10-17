package sep490.g13.pms_be.model.request.children;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.model.request.RelationshipRequest;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddChildrenRequest {
    @NotNull(message = "Children Name không được để trống")
    private String childName;
    @NotNull(message = "Children Age không được để trống")
    private Integer childAge;
    @NotNull(message = "Children DOB không được để trống")
    private LocalDate childBirthDate;
    @NotNull(message = "Children Address không được để trống")
    private String childAddress;
    private List<RelationshipRequest> relationships;
    @NotNull(message = "Children people không được để để trống")
    private String people;
    @NotNull(message= "Birth Address không được để để trống")
    private String birthAddress;
    @NotNull(message= "Nationality không được để để trống")
    private String nationality;
    @NotNull(message= "Identification Number không được để để trống")
    private String identificationNumber;
    private String createdById;
}
