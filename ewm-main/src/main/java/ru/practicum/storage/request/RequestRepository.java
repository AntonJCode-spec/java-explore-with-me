package ru.practicum.storage.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findByIdInAndEventId(List<Long> requestIds, Long eventId);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("""
        SELECT r.event.id, COUNT(r)
        FROM Request r
        WHERE r.event.id IN :eventIds AND r.status = :status
        GROUP BY r.event.id
        """)
    List<Object[]> countByEventIdsAndStatus(@Param("eventIds") List<Long> eventIds,
                                            @Param("status") RequestStatus status);

}
