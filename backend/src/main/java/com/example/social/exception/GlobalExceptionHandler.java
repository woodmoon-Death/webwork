package com.example.social.exception;

import com.example.social.common.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  public ApiResponse<Void> handleBusiness(BusinessException exception) {
    return ApiResponse.error(exception.getCode(), exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ApiResponse<Void> handleValidation(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().isEmpty()
        ? "请求参数不正确"
        : exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    return ApiResponse.error(400, message);
  }

  @ExceptionHandler(Exception.class)
  public ApiResponse<Void> handleOther(Exception exception) {
    return ApiResponse.error(500, "服务器暂时不可用");
  }
}
