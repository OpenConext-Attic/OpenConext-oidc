package oidc.control;

import oidc.repository.FederatedUserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Query;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@RestController
public class ActuatorController {


    @Autowired
    private FederatedUserInfoRepository federatedUserInfoRepository;

    @RequestMapping(method = RequestMethod.GET, value = "/info")
    public Properties info() throws IOException {
        Properties props = new Properties();
        props.load(new ClassPathResource("git.properties").getInputStream());
        return props;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/health")
    @Transactional(timeout = 3)
    public ResponseEntity health() throws IOException {
        Query query = federatedUserInfoRepository.getManager().createNativeQuery("SELECT 1");
        query.setHint("javax.persistence.query.timeout", 1000);
        List resultList = query.getResultList();
        return resultList.size() == 1 ? ResponseEntity.ok(Collections.singletonMap("status", "UP")) :
            ResponseEntity.badRequest().build();
    }

}
