package sep490.g13.pms_be.service.utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sep490.g13.pms_be.model.response.DistanceMatrixResponse;

@Service
public class DistanceService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_KEY = "AIzaSyC8hvhMnKc_lBFPszIIucXOwwYKTLmFzWI";

    public double calculateDistance(String origin, String destination) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
                    .queryParam("units", "metric")
                    .queryParam("origins", origin)
                    .queryParam("destinations", destination)
                    .queryParam("key", API_KEY)
                    .toUriString();

            // In URL để kiểm tra
            System.out.println("Request URL: " + url);

            DistanceMatrixResponse response = restTemplate.getForObject(url, DistanceMatrixResponse.class);

            // In phản hồi từ API
            System.out.println("Phản hồi từ Google API: " + new ObjectMapper().writeValueAsString(response));

            if (response != null && response.getRows() != null && !response.getRows().isEmpty()) {
                DistanceMatrixResponse.Row row = response.getRows().get(0);
                if (row.getElements() != null && !row.getElements().isEmpty()) {
                    DistanceMatrixResponse.Element element = row.getElements().get(0);
                    if ("OK".equals(element.getStatus())) {
                        return element.getDistance().getValue() / 1000.0;
                    } else {
                        throw new IllegalArgumentException("Google API trả về trạng thái không hợp lệ: " + element.getStatus());
                    }
                }
            }

            throw new IllegalArgumentException("Phản hồi từ Google API không hợp lệ");

        } catch (Exception e) {
            System.out.println("Lỗi khi gọi API Distance Matrix: " + e.getMessage());
            throw new IllegalArgumentException("Không thể tính toán khoảng cách: " + e.getMessage());
        }
    }



}
