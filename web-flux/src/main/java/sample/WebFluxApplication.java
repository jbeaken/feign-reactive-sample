package sample;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class WebFluxApplication {

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args) {
        SpringApplication.run(WebFluxApplication.class, args);
    }

    @GetMapping(value = "/greeting")
    public Mono<Person> greeting() {
        final Person jack = new Person("jack");
//        String idInEureka = eurekaClient.getApplication(appName).getInstances().get(0).getId();
        return Mono.just(jack);
    }



//    @Bean
//    public WebClient webClient() {
//        final ObjectMapper objectMapper = new ObjectMapper()
//                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//        return WebClient.builder().codecs(
//                configurer -> {
//                    configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
//                    configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
//                }
//        ).build();
//    }

    @GetMapping("/greetingWithParam")
    public Mono<String> greetingWithParam(@RequestParam(value = "id") Long id) {
        String idInEureka = eurekaClient.getApplication(appName).getInstances().get(0).getId();
        return Mono.just(String.format("Hello with param from '%s'!", idInEureka));
    }
}

class Person {

    public Person(String name) {
        this.name = name;
    }

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
