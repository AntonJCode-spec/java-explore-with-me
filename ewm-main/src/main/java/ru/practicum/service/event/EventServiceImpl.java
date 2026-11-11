package ru.practicum.service.event;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHit;
import ru.practicum.ViewStats;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.event.AdminStateAction;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSortValue;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import com.querydsl.core.BooleanBuilder;
import ru.practicum.model.EventState;
import ru.practicum.model.QEvent;
import ru.practicum.model.User;
import ru.practicum.storage.category.CategoryRepository;
import ru.practicum.storage.event.EventRepository;
import ru.practicum.storage.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EventFullDto createEvent(NewEventDto newEventDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id не существует"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException("Категории с таким id не существует"));

        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Событие не может начинаться раньше чем через два часа после его создания");
        }

        Event eventToSave = EventMapper.mapToEvent(newEventDto, category, user);

        eventRepository.save(eventToSave);

        return EventMapper.mapToEventFullDto(eventToSave);
    }

    @Override
    public List<EventFullDto> getEventsByInitiatorId(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }

        List<Event> events = eventRepository.getEventByInitiatorId(userId, from, size);

        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

    @Override
    public EventFullDto findEventByInitiatorIdAndEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }

        Event event = eventRepository.findEventByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие не найдено"));

        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto findEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new EventNotFoundException("События с таким id не существует"));
        sendHit(request);
        String uri = "/events/" + eventId;
        List<ViewStats> stats = statsClient.getStats(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1),
                List.of(uri), true);
        if (stats == null || stats.isEmpty()) {
            return EventMapper.mapToEventFullDto(event);
        }
        return EventMapper.mapToEventFullDto(event, stats.getFirst().getHits());
    }

    @Override
    public EventFullDto updateEvent(UpdateEventUserRequest dto, Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }

        if (dto.getEventDate() != null && dto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Ошибка валидации времени");
        }

        Event event = eventRepository.findEventByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new EventNotFoundException("Событие не найдено"));
        if (event.getState() == EventState.PUBLISHED) {
            throw new DataConflictException("Нельзя изменять событие которое уже опубликовано");
        }

        EventMapper.updateEventFields(event, dto);

        if (dto.getCategory() != null && !dto.getCategory().equals(event.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("Категории с таким id не существует"));

            event.setCategory(category);
        }

        eventRepository.save(event);

        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByAdmin(UpdateEventAdminRequest dto, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("События с таким id не существует"));

        if (dto.getCategory() != null && !dto.getCategory().equals(event.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException("Категории с таким id не существует"));
            event.setCategory(category);
        }

        if (dto.getStateAction() == AdminStateAction.PUBLISH_EVENT && event.getState() != EventState.PENDING) {
            throw new DataConflictException("Можно опубликовать событие только если оно в ожидании публикации");
        }
        if (dto.getStateAction() == AdminStateAction.REJECT_EVENT && event.getState() == EventState.PUBLISHED) {
            throw new DataConflictException("Нельзя отменить событие которое уже опубликовано");
        }

        if (dto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
            if (dto.getEventDate() != null) {
                if (dto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new DataConflictException("Начало события не может быть раньше чем через час от текущего момента");
                }
            } else {
                if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                    throw new DataConflictException("Начало события не может быть раньше чем через час от текущего момента");
                }
            }
        }

        EventMapper.updateEventFields(event, dto);

        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventFullDto> findAllEventForAdmin(List<Long> users, List<EventState> states,
                                                   List<Long> categories, LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd, int from, int size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new DataConflictException("rangeStart не может быть позже rangeEnd");
        }

        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();

        if (users != null && !users.isEmpty()) {
            builder.and(qEvent.initiator.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            builder.and(qEvent.state.in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }
        if (rangeStart != null) {
            builder.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            builder.and(qEvent.eventDate.loe(rangeEnd));
        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Event> query = queryFactory
                .selectFrom(qEvent)
                .where(builder)
                .orderBy(qEvent.id.asc())
                .offset(from)
                .limit(size);

        List<Event> events = query.fetch();

        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

    @Override
    public List<EventShortDto> findEventsForPublic(String text, List<Long> categories, Boolean paid,
                                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                   boolean onlyAvailable, EventSortValue sort,
                                                   int from, int size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new DataConflictException("rangeStart не может быть позже rangeEnd");
        }

        QEvent qEvent = QEvent.event;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qEvent.state.eq(EventState.PUBLISHED));
        if (text != null && !text.isBlank()) {
            builder.and(qEvent.annotation.containsIgnoreCase(text).or(qEvent.description.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            builder.and(qEvent.category.id.in(categories));
        }
        if (paid != null) {
            builder.and(qEvent.paid.eq(paid));
        }
        if (rangeStart == null && rangeEnd == null) {
            builder.and(qEvent.eventDate.goe(LocalDateTime.now()));
        }
        if (rangeStart != null) {
            builder.and(qEvent.eventDate.goe(rangeStart));
        }
        if (rangeEnd != null) {
            builder.and(qEvent.eventDate.loe(rangeEnd));
        }
//        if (onlyAvailable) {
//            builder.and(qEvent.participantLimit) вернуться когда будет Таблица заявок
//        }

        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        JPAQuery<Event> query = queryFactory
                .selectFrom(qEvent)
                .where(builder)
                .offset(from)
                .limit(size);

        if (sort == EventSortValue.EVENT_DATE) {
            query.orderBy(qEvent.eventDate.asc());
        } else {
            query.orderBy(qEvent.id.asc());
        }
        sendHit(request);

        List<Event> events = query.fetch();

        List<String> uris = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStats> stats = statsClient.getStats(
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now().plusYears(1),
                uris,
                true
        );

        Map<String, Long> viewsMap = stats.stream()
                .collect(Collectors.toMap(
                        ViewStats::getUri,
                        ViewStats::getHits
                ));

        return events.stream()
                .map(event -> {
                    String uri = "/events/" + event.getId();
                    Long views = viewsMap.getOrDefault(uri, 0L);
                    return EventMapper.mapToEventShortDto(event, views); // Передаем views
                })
                .collect(Collectors.toList());
    }

    private void sendHit(HttpServletRequest request) {
        EndpointHit hit = new EndpointHit();
        hit.setApp("ewm-main-service");
        hit.setUri(request.getRequestURI()); // Например, "/events"
        hit.setIp(request.getRemoteAddr());
        hit.setTimestamp(LocalDateTime.now());
        statsClient.hit(hit);
    }


}
