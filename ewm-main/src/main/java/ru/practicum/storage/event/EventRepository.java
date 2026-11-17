package ru.practicum.storage.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query(nativeQuery = true,
    value = """
            SELECT *
            FROM events AS e
            WHERE initiator_id = :userId
            OFFSET :from
            LIMIT :size
            """)
    List<Event> getEventByInitiatorId(Long userId, int from, int size);

    Optional<Event> findEventByInitiatorIdAndId(Long userId, Long eventId);

    Optional<Event> findByIdAndState(Long eventId, EventState state);

    Set<Event> findByIdIn(Collection<Long> events);
}
