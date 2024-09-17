package sep490.g13.pms_be.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode
public class BusinessErrorCode {
    int code;
    String group;
    String message;
    int httpStatus;
}
