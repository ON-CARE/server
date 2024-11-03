package com.example.service.map;

import com.example.dto.map.Facility;
import com.example.dto.map.KakaoLocalSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public final class KakaoLocalSearchService {

    private final RestTemplate restTemplate;
    private final String apiKey = "0da36d0af5d11373a95e4893d9fa4a3e";
    private final String apiUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";

    public KakaoLocalSearchService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<Facility> searchNearbyFacilities(double longitude, double latitude) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + apiKey);

        String keyword = "hospital"; // "노인복지"를 검색어로 인코딩      String keyword = "hospital";
        int radius = 10000;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("query", keyword)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", radius);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoLocalSearchResponse> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    KakaoLocalSearchResponse.class
            );

            log.info("Kakao API Response: {}", response);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getDocuments().stream()
                        .map(doc -> new Facility(doc.getPlaceName(), doc.getAddressName(), doc.getLongitude(), doc.getLatitude()))
                        .collect(Collectors.toList());

            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Kakao API 호출 중 오류 발생", e);
            return Collections.emptyList();
        }
    }
}
