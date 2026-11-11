package ru.practicum.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StatsClientImpl implements StatsClient {

    private final RestTemplate restTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void hit(EndpointHit hit) {
        restTemplate.postForEntity("/hit", hit, Void.class);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String startStr = start.format(FORMATTER);
        String endStr = end.format(FORMATTER);

        Map<String, Object> params = new HashMap<>();
        params.put("start", startStr);
        params.put("end", endStr);
        params.put("unique", unique);
        if (uris != null && !uris.isEmpty()) {
            params.put("uris", String.join(",", uris));
        }

        ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
                "/stats?start={start}&end={end}&unique={unique}&uris={uris}",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {},
                params
        );

        return response.getBody();
    }
}