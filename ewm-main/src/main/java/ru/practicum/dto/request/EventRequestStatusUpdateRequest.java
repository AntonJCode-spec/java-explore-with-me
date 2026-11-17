package ru.practicum.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Список заявок не может быть пустым")
    List<Long> requestIds;
    @NotNull(message = "Новый статус заявок не может быть пустым")
    EventRequestStatusUpdate status;
}
