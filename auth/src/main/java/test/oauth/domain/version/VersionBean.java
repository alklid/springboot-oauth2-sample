package test.oauth.domain.version;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import javax.persistence.Entity;

@Data
public class VersionBean {

    private String build_version;
    private String release_version;

}
