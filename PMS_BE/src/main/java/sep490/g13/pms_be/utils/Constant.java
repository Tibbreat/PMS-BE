package sep490.g13.pms_be.utils;

import org.springframework.stereotype.Component;

@Component
public class Constant {
    public static String PREFIX_RESPONSE_CODE;
    public static String OK_CODE;
    public static String GROUP_CODE_SUCCESS = "00";
    public static String GROUP_CODE_DATA_INVALID = "01";
    public static String GROUP_CODE_BUSINESS = "02";
    public static String GROUP_CODE_ESB_CONNECT = "03";
    public static String GROUP_CODE_AUTHORIZATION = "04";
    public static String GROUP_CODE_CONNECT_TIMEOUT = "05";
    public static String GROUP_CODE_UNKNOWN = "99";
}
