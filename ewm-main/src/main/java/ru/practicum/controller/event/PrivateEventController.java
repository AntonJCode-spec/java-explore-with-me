package ru.practicum.controller.event;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEventsByInitiatorId(@PathVariable Long userId,
                                                     @RequestParam(defaultValue = "0") int from,
                                                     @RequestParam(defaultValue = "10") int size) {
        return eventService.getEventsByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventByInitiatorIdAndEventId(@PathVariable Long userId,
                                                         @PathVariable Long eventId) {
        return eventService.findEventByInitiatorIdAndEventId(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        return eventService.createEvent(newEventDto, userId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody UpdateEventUserRequest dto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        return eventService.updateEvent(dto, userId, eventId);
    }



}
