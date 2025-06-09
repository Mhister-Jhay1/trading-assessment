package org.trading.system.common.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T>{
    private int code;
    private String message;
    private T data;
    private List<String> errors;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .code(HttpStatus.OK.value())
                .message("Request Processed Successfully")
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .code(HttpStatus.CREATED.value())
                .message("Resource Created Successfully")
                .build();
    }

    public static <T> ApiResponse<T> error(int statusCode, List<String> errors) {
        return ApiResponse.<T>builder()
                .errors(errors)
                .code(statusCode)
                .message("An error occurred")
                .build();
    }

}
