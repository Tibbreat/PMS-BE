package sep490.g13.pms_be.model.response;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.data.domain.Page;
import sep490.g13.pms_be.exception.BusinessErrorCode;
import sep490.g13.pms_be.exception.BusinessException;
import sep490.g13.pms_be.utils.Constant;

import java.util.List;

@Getter
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private T data;
    private Metadata meta = new Metadata();

    BaseResponse(T data, Metadata meta) {
        this.data = data;
        this.meta = meta;
    }

    public BaseResponse() {
    }

    public static <T> BaseResponse<T> ofSucceeded() {
        return ofSucceeded((T) null);
    }

    public static <T> BaseResponse<T> ofSucceeded(T data) {
        BaseResponse<T> response = new BaseResponse<>();
        response.data = data;
        response.meta.code = Constant.GROUP_CODE_SUCCESS;
        response.meta.message = "Thành công";
        return response;
    }

    public static <T> BaseResponse<List<T>> ofSucceeded(Page<T> data) {
        BaseResponse<List<T>> response = new BaseResponse<>();
        response.data = data.getContent();
        response.meta.code = Constant.GROUP_CODE_SUCCESS;
        response.meta.message = "Thành công";
        response.meta.page = data.getNumber();
        response.meta.size = data.getSize();
        response.meta.total = data.getTotalElements();
        return response;
    }

    public static <T> BaseResponse<List<T>> ofSucceeded(Page<T> data, Long totalErrors) {
        BaseResponse<List<T>> response = ofSucceeded(data);
        response.meta.totalErrors = totalErrors;
        return response;
    }

    public static BaseResponse<Void> ofFailed(BusinessErrorCode errorCode) {
        return ofFailed(errorCode, (String) null);
    }


    public static BaseResponse<Void> ofFailed(BusinessErrorCode errorCode, String message) {
        BaseResponse<Void> response = new BaseResponse<>();
        response.meta.code = Constant.PREFIX_RESPONSE_CODE + "-" + Constant.GROUP_CODE_SUCCESS + "-" + errorCode.getCode();
        response.meta.message = message;
        return response;
    }


    public static BaseResponse<Void> ofFailed(BusinessException exception) {
        return ofFailed(exception.getErrorCode(), exception.getMessage());
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Getter
    public static class Metadata {
        String code;
        Integer page;
        Integer size;
        Long total;
        Long totalErrors;
        String message;

        public Metadata() {
        }

        @CompiledJson
        public Metadata(String code, Integer page, Integer size, Long total, String message) {
            this.code = code;
            this.page = page;
            this.size = size;
            this.total = total;
            this.message = message;
        }
    }
}
