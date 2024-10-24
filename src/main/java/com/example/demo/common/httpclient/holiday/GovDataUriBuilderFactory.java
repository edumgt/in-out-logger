package com.example.demo.common.httpclient.holiday;

import com.example.demo.common.model.SecretConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GovDataUriBuilderFactory extends DefaultUriBuilderFactory {
    private final SecretConfig secretConfig;

    @SneakyThrows
    @Override
    public URI expand(String uriTemplate, Map<String, ?> uriVars) {
        URI uri = super.expand(uriTemplate, uriVars);
        String apiKey = secretConfig.getApiKey().getHoliday();
        String character = uri.toString().contains("?") ? "&" : "?";
        return new URI(uri + character + "serviceKey=" + apiKey); // secretKey 인코딩 문제로 별도 클래스 작성
    }

    @SneakyThrows
    @Override
    public URI expand(String uriTemplate, Object... uriVars) {
        URI uri = super.expand(uriTemplate, uriVars);
        String apiKey = secretConfig.getApiKey().getHoliday();
        String character = uri.toString().contains("?") ? "&" : "?";
        return new URI(uri + character + "serviceKey=" + apiKey);
    }

}
