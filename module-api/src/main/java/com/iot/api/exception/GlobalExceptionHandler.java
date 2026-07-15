package com.iot.api.exception;

import com.iot.alerts.exception.AlertRuleNotFoundException;
import com.iot.api.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.iot.shared.exception.DeviceNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleDeviceNotFound(DeviceNotFoundException e) {
        log.warn("Device introuvable : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(e.getMessage()));
    }

    @ExceptionHandler(AlertRuleNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleAlertRuleNotFound(AlertRuleNotFoundException e) {
        log.warn("Règle introuvable : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDTO(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleAll(Exception e) {
        log.error("Erreur inattendue : {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDTO("Erreur interne du serveur"));
    }
}
