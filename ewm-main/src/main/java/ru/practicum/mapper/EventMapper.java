package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.event.AdminStateAction;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UserStateAction;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.LocationModel;
import ru.practicum.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public static Event mapToEvent(NewEventDto dto, Category category, User user) {
        Event event = new Event();

        event.setAnnotation(dto.getAnnotation());
        event.setCategory(category);
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setInitiator(user);

        LocationModel location = new LocationModel(dto.getLocation().getLat(), dto.getLocation().getLon());
        event.setLocation(location);

        event.setPaid(dto.isPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);

        event.setState(EventState.PENDING);
        event.setTitle(dto.getTitle());
        event.setCreatedOn(LocalDateTime.now());

        return event;
    }

    public static EventFullDto mapToEventFullDto(Event event) {
        EventFullDto dto = new EventFullDto();

        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(0L);
        dto.setCreatedOn(event.getCreatedOn());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(UserMapper.mapToUserShortDto(event.getInitiator()));
        Location location = new Location(event.getLocation().getLat(), event.getLocation().getLon());
        dto.setLocation(location);
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState());
        dto.setTitle(event.getTitle());
        dto.setViews(0L);

        return dto;
    }

    public static EventFullDto mapToEventFullDto(Event event, Long views) {
        EventFullDto dto = mapToEventFullDto(event);
        dto.setViews(views);
        return dto;
    }

    public static Event updateEventFields(Event eventToUpdate, UpdateEventUserRequest dto) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            eventToUpdate.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            eventToUpdate.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            eventToUpdate.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            eventToUpdate.setLocation(new LocationModel(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }
        if (dto.getPaid() != null) {
            eventToUpdate.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(UserStateAction.SEND_TO_REVIEW)) {
                eventToUpdate.setState(EventState.PENDING);
            } else if (dto.getStateAction().equals(UserStateAction.CANCEL_REVIEW)) {
                eventToUpdate.setState(EventState.CANCELED);
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            eventToUpdate.setTitle(dto.getTitle());
        }
        return eventToUpdate;
    }

    public static Event updateEventFields(Event eventToUpdate, UpdateEventAdminRequest dto) {
        if (dto.getAnnotation() != null && !dto.getAnnotation().isBlank()) {
            eventToUpdate.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            eventToUpdate.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            eventToUpdate.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            eventToUpdate.setLocation(new LocationModel(dto.getLocation().getLat(), dto.getLocation().getLon()));
        }
        if (dto.getPaid() != null) {
            eventToUpdate.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            eventToUpdate.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            eventToUpdate.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getStateAction() != null) {
            if (dto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                eventToUpdate.setState(EventState.PUBLISHED);
                eventToUpdate.setPublishedOn(LocalDateTime.now());
            } else if (dto.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
                eventToUpdate.setState(EventState.CANCELED);
            }
        }
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            eventToUpdate.setTitle(dto.getTitle());
        }
        return eventToUpdate;
    }

    public static EventShortDto mapToEventShortDto(Event event, Long views) {
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.mapToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(0L);
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(UserMapper.mapToUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(views);

        return dto;
    }


}
