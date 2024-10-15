package sep490.g13.pms_be.service.utils;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class DriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String SERVICE_ACCOUNT_KEY_PATH = getPathToGoogleCredentials();

    // Nếu muốn giữ folder mặc định
    private static final String DRIVE_FOLDER = "1GmrXHwQg_rZ_boafmzZYsr9cUNbKHmD-"; // Folder mặc định

    private static String getPathToGoogleCredentials() {
        ClassLoader classLoader = DriveService.class.getClassLoader();
        try {
            File file = new File(classLoader.getResource("cred.json").getFile());
            return file.getAbsolutePath();
        } catch (NullPointerException e) {
            throw new RuntimeException("cred.json file not found in resources folder");
        }
    }

    public String upload(File file, String folderId) {
        try {
            Drive drive = createDriveService();

            // Tạo metadata cho file upload
            com.google.api.services.drive.model.File fileMetaData = new com.google.api.services.drive.model.File();
            fileMetaData.setName(file.getName());

            // Nếu không truyền folderId, sử dụng folder mặc định
            if (folderId == null || folderId.isEmpty()) {
                folderId = DRIVE_FOLDER;
            }

            fileMetaData.setParents(Collections.singletonList(folderId));

            FileContent mediaContent = new FileContent("application/pdf", file);

            // Upload file lên Google Drive
            com.google.api.services.drive.model.File uploadedFile = drive.files()
                    .create(fileMetaData, mediaContent)
                    .setFields("id")
                    .execute();

            // Trả về link preview
            return "https://drive.google.com/file/d/" + uploadedFile.getId() + "/preview";

        } catch (IOException | GeneralSecurityException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }

    private Drive createDriveService() throws GeneralSecurityException, IOException {
        GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential).build();
    }
}
