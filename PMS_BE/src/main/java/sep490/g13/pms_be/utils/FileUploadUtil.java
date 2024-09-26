package sep490.g13.pms_be.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;
import sep490.g13.pms_be.exception.other.BadCredentialsException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class FileUploadUtil {
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    public static final String IMAGE_PATTERN = "http(s?)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./]*)+\\.(?:[gG][iI][fF]|[jJ][pP][gG]|[jJ][pP][eE][gG]|[pP][nN][gG]|[bB][mM][pP])";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String FILE_NAME_FORMAT = "%s_%s_%s";

    public static boolean isAllowedExtension(String fileName, String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }
    public static void assertAllowedExtension(MultipartFile file, String pattern) {
        final long size = file.getSize();
        if(size > MAX_FILE_SIZE) {
            throw new BadCredentialsException("File is too large to be uploaded");
        }
        final String fileName = file.getOriginalFilename();
        final String extension = FilenameUtils.getExtension(fileName);
        if(!isAllowedExtension(fileName, pattern)) {
            throw new BadCredentialsException("File is not allowed extension: " + extension);
        }
    }
    public static String getFileName(final String name){
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FILE_NAME_FORMAT,name,date);
    }
}
