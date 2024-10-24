package com.example.demo.common.config;


import com.example.demo.common.httpclient.holiday.exchanger.HolidayExchanger;
import com.example.demo.common.model.SecretConfig;
import com.example.demo.common.tools.SystemEnvironment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;


@Configuration
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class AppConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public SecretConfig secretConfig() throws IOException {
        Path path;
        if (SystemEnvironment.isWindows()) {
            path = Path.of("/config.json");
        } else if (SystemEnvironment.isLinux()) {
            path = Path.of("etc/config.json");
        } else {
            throw new RuntimeException("Unsupported OS");
        }
        String json = Files.readString(path);
        SecretConfig secretConfig = objectMapper.readValue(json, SecretConfig.class);
        return secretConfig;
    }
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            HttpHeaders httpHeaders = request.getHeaders();
            httpHeaders.forEach((key, value) -> {
                log.info("Request header key : {} value : {}", key, value);
            });
            long start = System.currentTimeMillis();
            ClientHttpResponse response = execution.execute(request, body);
            long end = System.currentTimeMillis();
            log.info("Response result : {} {}", response.getStatusCode(), response.getStatusText());
            log.info("Request to {} elapsed time : {} ms", request.getURI(),end - start);
            return response;
        });
        return restTemplate;
    }
    @Bean
    public RestClient.Builder restClientBuilder(RestTemplate restTemplate) {
        return RestClient.builder(restTemplate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
    @Bean
    @Primary
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
    @Bean("holidayClient")
    public RestClient holidayClient(RestClient.Builder builder, SecretConfig secretConfig) {
        return builder.uriBuilderFactory(new DefaultUriBuilderFactory(){
            @Override
            public UriBuilder uriString(String uriTemplate) {
                return super.uriString(uriTemplate)
                        .queryParam("serviceKey", secretConfig.getApiKey().getHoliday());
            }
        }).build();
    }

    @Bean
    public HolidayExchanger holidayExchanger(@Qualifier("holidayClient") RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(HolidayExchanger.class);
    }
}
