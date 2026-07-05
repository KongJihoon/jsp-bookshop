package hello.bookshop.payment.client;

import hello.bookshop.payment.config.TossProperties;
import hello.bookshop.payment.dto.request.TossConfirmRequest;
import hello.bookshop.payment.dto.response.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class TossPaymentClient {

    private final TossProperties tossProperties;
    private final RestClient restClient = RestClient.create();

    public TossConfirmResponse confirm(TossConfirmRequest request) {
        return restClient.post()
                .uri(tossProperties.getConfirmUrl())
                .header(HttpHeaders.AUTHORIZATION, createAuthorization())
                .body(request)
                .retrieve()
                .body(TossConfirmResponse.class);
    }

    private String createAuthorization() {
        String value = tossProperties.getSecretKey() + ":";

        String encoded = Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));

        return "Basic " + encoded;

    }


}
