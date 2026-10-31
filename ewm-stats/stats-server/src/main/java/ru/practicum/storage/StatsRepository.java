package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStats;
import ru.practicum.storage.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("""
            SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit AS h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStats> getStatsByUniqueIpWithoutUris(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit AS h
            WHERE h.timestamp BETWEEN :start AND :end
            AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStats> getStatsByUniqueIpWithUris(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(h.ip))
            FROM Hit AS h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<ViewStats> getStatsByNotUniqueIpWithoutUris(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ViewStats(h.app, h.uri, COUNT(h.ip))
            FROM Hit AS h
            WHERE h.timestamp BETWEEN :start AND :end
            AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h.ip) DESC
            """)
    List<ViewStats> getStatsByNotUniqueIpWithUris(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("uris") List<String> uris);
}
