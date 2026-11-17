package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.model.Request;

import java.util.List;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto mapToParticipantRequestDto(Request request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();

        dto.setId(request.getId());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(request.getStatus());
        dto.setCreated(request.getCreated());

        return dto;
    }

    public static EventRequestStatusUpdateResult mapToStatusUpdateResult(List<Request> confirmed, List<Request> rejected) {
        List<ParticipationRequestDto> confirmedDto = confirmed.stream()
                .map(RequestMapper::mapToParticipantRequestDto)
                .toList();

        List<ParticipationRequestDto> rejectedDto = rejected.stream()
                .map(RequestMapper::mapToParticipantRequestDto)
                .toList();

        return new EventRequestStatusUpdateResult(confirmedDto, rejectedDto);
    }
}
