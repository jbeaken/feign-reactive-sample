package sample;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactivefeign.ReactiveOptions;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactivefeign.webclient.WebClientFeignCustomizer;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "web-flux-app", configuration = GreetingReactive.CustomConfiguration.class)
public interface GreetingReactive {


    @GetMapping(value = "/greeting", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<Person> greeting();

    @GetMapping("/greetingWithParam")
    Mono<String> greetingWithParam(@RequestParam(value = "id") Long id);

    @Configuration
    class CustomConfiguration {

        private static final int MAX_CODEC_MEMORY_SIZE = 2 * 1024 * 1024;

        public static ObjectMapper buildObjectMapper(Module... modules) {
            final ObjectMapper objectMapper = new ObjectMapper()
                    .registerModules(modules)
                    .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
            return objectMapper;
        }


        @Bean
        public WebClientFeignCustomizer webClientFeignCustomizer(Module... modules) {
            final ObjectMapper objectMapper = new ObjectMapper()
                    .registerModules(modules)
                    .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

            return builder -> {
                builder.exchangeStrategies(
                        buildExchangeStrategies(objectMapper));
            };
        }

        public static ExchangeStrategies buildExchangeStrategies(ObjectMapper objectMapper) {
            return ExchangeStrategies.builder().codecs(
                    configurer -> {
                        configurer.defaultCodecs().maxInMemorySize(MAX_CODEC_MEMORY_SIZE);
                        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                    }
            ).build();
        }
    }
}


