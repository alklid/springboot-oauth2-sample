package test.oauth.domain.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class VersionService {

    @Autowired
    private Environment env;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public VersionBean getVersion() {

        VersionBean version = new VersionBean();
        version.setBuild_version(env.getProperty("api.build.version", ""));
        version.setRelease_version(env.getProperty("api.release.version", ""));

        return version;
    }
}
