package test.oauth.domain.version;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionControl {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private VersionService versionService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getVersion() {

        VersionBean version = versionService.getVersion();
        return new ResponseEntity<>(modelMapper.map(version, VersionBean.class), HttpStatus.OK);
    }
}

