package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.EventForAdminDto;
import ru.practicum.dto.event.EventForInitiatorDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventSortValue;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto createEvent(NewEventDto newEventDto, Long userId);

    List<EventForInitiatorDto> getEventsByInitiatorId(Long userId, int from, int size);

    EventForInitiatorDto findEventByInitiatorIdAndEventId(Long userId, Long eventId);

    EventFullDto findEventById(Long eventId, HttpServletRequest request);

    EventFullDto updateEvent(UpdateEventUserRequest dto, Long userId, Long eventId);

    EventForAdminDto updateEventByAdmin(UpdateEventAdminRequest dto, Long eventId);

    List<EventFullDto> findAllEventForAdmin(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from, int size);

    List<EventShortDto> findEventsForPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            EventSortValue sort,
                                            int from, int size,
                                            HttpServletRequest request);
}
