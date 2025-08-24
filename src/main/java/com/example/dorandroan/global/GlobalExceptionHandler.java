package com.example.dorandroan.global;

import com.example.dorandroan.global.error.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleCustomException(RestApiException e) {
        log.error("Error: {}   Message: {}", e.getErrorCode(), e.getErrorCode().getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("handleIllegalArgument : {}", e.getMessage());
        ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
        return handleExceptionInternal(errorCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid (MethodArgumentNotValidException e,
                                                                   HttpHeaders headers, HttpStatusCode status,
                                                                   WebRequest request) {

        log.warn("handleMethodArgumentNotValidException : {}", "Invalid Arguments");
        String errMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");
        return ResponseEntity.badRequest().body(makeResponse(CommonErrorCode.INVALID_PARAMETER, errMsg));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex) {
        log.warn("handleAllException", ex);
        ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(makeResponse(errorCode, errorCode.getMessage()));
    }

    private ErrorResponse makeResponse(ErrorCode errorCode, String errMsg) {
        return ErrorResponse.of(errorCode.name(), errMsg);
    }

}
