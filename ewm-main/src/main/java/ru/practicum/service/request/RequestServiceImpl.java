package ru.practicum.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.EventRequestStatusUpdate;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.RequestNotFoundException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.Request;
import ru.practicum.model.RequestStatus;
import ru.practicum.model.User;
import ru.practicum.storage.event.EventRepository;
import ru.practicum.storage.request.RequestRepository;
import ru.practicum.storage.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> findRequestsByUserId(Long userId) {

        List<Request> requests = requestRepository.findByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::mapToParticipantRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id не существует"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("События с таким id не существует"));
        if (event.getState() != EventState.PUBLISHED) {
            throw new DataConflictException("Нельзя участвовать в событии которое ещё не опубликовано");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new DataConflictException("Невозможно оставить повторный запрос на событие");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new DataConflictException("Нельзя оставлять запрос на участие в своём же событии");
        }

        if (event.getParticipantLimit() > 0
                && requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new DataConflictException("Лимит заявок на участие заполнен");
        }
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        requestRepository.save(request);

        return RequestMapper.mapToParticipantRequestDto(request);
    }

    @Override
    public ParticipationRequestDto canceledRequest(Long requestId, Long userId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new RequestNotFoundException("Запроса с таким id не существует"));

        request.setStatus(RequestStatus.CANCELED);

        requestRepository.save(request);

        return RequestMapper.mapToParticipantRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> findRequestsByEventIdForInitiator(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id не существует"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("События с таким id не существует"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataConflictException("Просматривать запросы на участие в событии может только инициатор");
        }

        List<Request> requests = requestRepository.findByEventId(eventId);

        return requests.stream()
                .map(RequestMapper::mapToParticipantRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id не существует"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("События с таким id не существует"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new DataConflictException("Рассматривать запросы на участие в событии может только инициатор");
        }

        List<Request> requests = requestRepository.findByIdInAndEventId(dto.getRequestIds(), eventId);

        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new DataConflictException("Заявка с id=" + request.getId() + " не находится в статусе рассмотрения");
            }
        }

        List<Request> confirmedRequest = new ArrayList<>();
        List<Request> rejectedRequest = new ArrayList<>();
        long participantLimit = event.getParticipantLimit();

        if (dto.getStatus() == EventRequestStatusUpdate.REJECTED) {
            for (Request request : requests) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequest.add(request);
            }
            return RequestMapper.mapToStatusUpdateResult(confirmedRequest, rejectedRequest);
        }

        long currentCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (participantLimit > 0 && currentCount >= participantLimit) {
            throw new DataConflictException("Лимит подтверждённых заявок уже достигнут");
        }

        for (Request request : requests) {
            if (participantLimit == 0 || currentCount < participantLimit) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequest.add(request);
                currentCount++;
            } else {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequest.add(request);
            }
            requestRepository.save(request);
        }

        if (participantLimit > 0 && currentCount >= participantLimit) {
            List<Request> allPending = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
            for (Request pending : allPending) {
                pending.setStatus(RequestStatus.REJECTED);
                requestRepository.save(pending);
                rejectedRequest.add(pending);
            }
        }

        return RequestMapper.mapToStatusUpdateResult(confirmedRequest, rejectedRequest);
    }
}
