package com.especial_topics_1.restaurant.standard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardResponse<T>(
        Instant timestamp,
        int status,
        String message,
        T data,
        List<FieldErrorDetail> errors,
        String path
        ) {

          public static <T> StandardResponse<T> success(T data, String message) {
                  return StandardResponse.<T>builder()
                          .timestamp(Instant.now())
                          .status(200)
                          .message(message)
                          .data(data)
                          .build();
          }
          public static <T> StandardResponse<T> created(T data, String message) {
                  return  StandardResponse.<T>builder()
                          .timestamp(Instant.now())
                          .status(201)
                          .message(message)
                          .data(data)
                          .build();
          }
          public static <T> StandardResponse<T> validationError(List<FieldErrorDetail> errors, String path) {
                  return StandardResponse.<T>builder()
                          .timestamp(Instant.now())
                          .status(422)
                          .message("Validation Error")
                          .errors(errors)
                          .path(path)
                          .build();
          }
          public static <T> StandardResponse<T> error(int status, String message, String path) {
                  return StandardResponse.<T>builder()
                          .timestamp(Instant.now())
                          .status(status)
                          .message(message)
                          .path(path)
                          .build();
          }
        public record FieldErrorDetail(String field, String message) {}
}
