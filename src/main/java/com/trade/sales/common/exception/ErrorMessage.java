package com.trade.sales.common.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorMessage {
    private String message;
    private int code;
    private String status;
    private LocalDateTime timestamp;
    private String path;
}
