package oliphant.com.library.kafkarestservice.gateway;

import lombok.extern.log4j.Log4j2;
import oliphant.com.library.kafkarestservice.commands.Command;
import oliphant.com.library.kafkarestservice.gateway.dto.Volumes;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.inject.Named;

@Log4j2
@Named
public class GoogleBooksGateway {

    private RestTemplate restTemplate;

    @Inject
    public GoogleBooksGateway(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public String getVolumesFromGoogle(Command command) {
        //        known id = GxvHBAAAQBAJ
        Volumes volumes = restTemplate.getForObject(
                "https://www.googleapis.com/books/v1/volumes?q=mitchell+inauthor:mitchell", Volumes.class);
//        log.info("Recieved volumes in gateway, volumes = {}", volumes);

        return String.valueOf(volumes);
    }
}
