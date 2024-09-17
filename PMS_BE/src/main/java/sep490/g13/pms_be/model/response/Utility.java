package sep490.g13.pms_be.model.response;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public final class Utility {
    private static final DateTimeFormatter YEAR_PATTERN = DateTimeFormatter.ofPattern("uu");

    public static void writeResponse(HttpServletResponse servletResponse, int httpStatus, byte[] body) throws IOException {
        servletResponse.setStatus(httpStatus);
        servletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        servletResponse.setContentLength(body.length);
        servletResponse.getOutputStream().write(body);
    }


    public static String getHeader(String header) {
        try {
            var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            var servletRequest = Objects.requireNonNull(attributes).getRequest();
            return servletRequest.getHeader(header);
        } catch (Exception e) {
            log.error("fail to get header");
        }
        return null;
    }

    public static <T> CompletableFuture<List<T>> toFutureList(List<CompletableFuture<T>> futures) {
        var allOf = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        return allOf.thenApply(v -> futures.stream().map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList()));
    }


    public static BigDecimal convertString2BigDecimal(String val) {
        return Objects.isNull(val) ? BigDecimal.ZERO : new BigDecimal(val);
    }


}
