package sep490.g13.pms_be.exception;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sep490.g13.pms_be.model.response.BaseResponse;
import sep490.g13.pms_be.model.response.Utility;

import java.io.IOException;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler({BusinessException.class})
    public void handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        handle(e, e.getErrorCode(), request, response);
    }


    private <T extends Exception> void handle(T e, BusinessErrorCode errorCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
        BaseResponse<Void> errorResponse = BaseResponse.ofFailed(errorCode, e.getMessage());
        log.info("Request: {} {}, Response: {}", request.getMethod(), request.getRequestURL(), objectMapper.writeValueAsString(errorResponse), e);
        writeResponse(response, errorCode.getHttpStatus(), errorResponse, e);
    }


    private void writeResponse(HttpServletResponse servletResponse, int httpStatus, BaseResponse<?> errorResponse, Exception e) throws IOException {
        byte[] body = objectMapper.writeValueAsBytes(errorResponse);
        log.info("[UNI-ERR] {}", e.getLocalizedMessage());
        Utility.writeResponse(servletResponse, httpStatus, body);
    }
}
