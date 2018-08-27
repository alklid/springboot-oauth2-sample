package test.oauth.common;

import lombok.Data;

@Data
public class SearchBean {
    private String key;
    private String operation;
    private Object value;

    public SearchBean(String key, String operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }
}
