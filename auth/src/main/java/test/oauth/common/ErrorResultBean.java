package test.oauth.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResultBean {
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String error;
    private String message;
    private String path;
}
