package sep490.g13.pms_be.model.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetParentOptionResponse {
    String id;
    String username;
    String fullName;
}
