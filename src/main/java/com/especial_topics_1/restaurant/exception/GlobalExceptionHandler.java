package com.especial_topics_1.restaurant.exception;

import com.especial_topics_1.restaurant.standard.StandardResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST; //400
        StandardResponse<Void> error= StandardResponse.error(
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        StandardResponse<Void> error = StandardResponse.error(
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT; // 422

        List<StandardResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f ->
                        new StandardResponse.FieldErrorDetail(f.getField(),f.getDefaultMessage()))
                .collect(Collectors.toList());
        StandardResponse<Void> err = StandardResponse.validationError(fieldErrors,request.getRequestURI());


        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGenericException(Exception ex, HttpServletRequest request) {

        log.error("Erro crítico não tratado na rota [{}]: ", request.getRequestURI(), ex);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500

        StandardResponse<Void> error =  StandardResponse.error(
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<StandardResponse<Void>> handleDataIntegrityException(DataIntegrityException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT; // 409

        StandardResponse<Void> error = StandardResponse.error(
                status.value(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<StandardResponse<Void>> handleAccessDeniedException(HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN; // 403


        StandardResponse<Void> error = StandardResponse.error(
                status.value(),
                "Você não tem permissão para realizar esta ação.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleNoResourceFoundException(
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND; // 404

        StandardResponse<Void> error = StandardResponse.error(
                status.value(),
                "O endpoint que você tentou acessar não existe no servidor.",
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<StandardResponse<Void>> handleMethodNotSupportedException(
            org.springframework.web.HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED; // 405

        StandardResponse<Void> error = StandardResponse.error(
                status.value(),
                "A rota existe, mas não aceita requisições do tipo " + ex.getMethod() + ".",
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                    HttpServletRequest request) {
        // Verifica se o erro foi no parâmetro "id" e se era pra ser um UUID
        if (ex.getRequiredType() == UUID.class) {
            StandardResponse<Void> error = StandardResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                    "O ID informado na URL não é um formato de UUID válido.",
                    request.getRequestURI()
            );
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        StandardResponse<Void> error = StandardResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro inválido na URL.",
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardResponse<Void>> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        StandardResponse<Void> error = StandardResponse.error(
                httpStatus.value(),
                ex.getMessage(),
                request.getRequestURI() );

        return ResponseEntity.status(httpStatus).body(error);
    }
}
