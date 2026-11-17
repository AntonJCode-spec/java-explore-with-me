package ru.practicum.service.request;

import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findRequestsByUserId(Long userId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto canceledRequest(Long requestId, Long userId);

    List<ParticipationRequestDto> findRequestsByEventIdForInitiator(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);
}
