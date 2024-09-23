package sep490.g13.pms_be.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationshipRequest {

        private String parentId;
        private String relationship;
        private Boolean isRepresentative;
}
