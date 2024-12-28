package com.sounganization.botanify.common.config.webClient;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;


@Configuration
public class WebClientConfig {
    @Value("${nongsaro.api.base-url}")
    private String baseUrl;

    @Bean(name = "plantWebClient")
    public WebClient plantWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_XML_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder()))
                .build();
    }

    @Bean(name = "weatherWebClient")
    public WebClient weatherWebClient(CircuitBreakerRegistry circuitBreakerRegistry) {

        // 커넥션 풀 설정
        ConnectionProvider provider = ConnectionProvider.builder("weatherService")
                .maxConnections(200) // 최대 연결 수 200
                .pendingAcquireTimeout(Duration.ofSeconds(5)) // 대기 시간 5초
                .build();

        // HttpClient 설정
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 시간 초과 5초
                .responseTimeout(Duration.ofSeconds(5)) // 응답 시간 초과 5초
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(5)) // 읽기 시간 초과 5초
                                .addHandlerLast(new WriteTimeoutHandler(5))) // 쓰기 시간 초과 5초
                .compress(true); // GZIP 압축 사용

        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("weatherService");

        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE); // 자동 인코딩 방지

        // WebClient 설정
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(((request, next) ->
                        next.exchange(request)
                                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 최대 메모리 16MB
                        .build())
                .uriBuilderFactory(uriBuilderFactory) // URI 인코딩 모드 적용
                .build();
    }

    @Bean(name = "locationRestTemplate")
    public RestTemplate locationRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
        restTemplate.setUriTemplateHandler(factory);
        return restTemplate;
    }
}

