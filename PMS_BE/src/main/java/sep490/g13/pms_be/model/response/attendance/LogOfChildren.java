package sep490.g13.pms_be.model.response.attendance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogOfChildren {
    private String id;
    private String childName;
    private String imageLink;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private String note;
}
