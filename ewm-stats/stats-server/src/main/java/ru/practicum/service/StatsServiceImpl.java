package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.exception.IllegalRangeException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.storage.StatsRepository;
import ru.practicum.storage.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public void hit(EndpointHit hit) {
        Hit hitToAdd = HitMapper.mapToHit(hit);

        statsRepository.save(hitToAdd);
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        validateDate(start, end);

        boolean hasUris = uris != null && !uris.isEmpty();

        if (unique) {
            if (hasUris) {
                return statsRepository.getStatsByUniqueIpWithUris(start, end, uris);
            }
            return statsRepository.getStatsByUniqueIpWithoutUris(start, end);
        }
        if (hasUris) {
            return statsRepository.getStatsByNotUniqueIpWithUris(start, end, uris);
        }
        return statsRepository.getStatsByNotUniqueIpWithoutUris(start, end);
    }

    private void validateDate(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IllegalRangeException("Начало временного интервала не может быть позже конца");
        }
    }
}
