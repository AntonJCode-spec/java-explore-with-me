package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Описание события не может быть пустым")
    @Size(min = 20, max = 2000, message = "Длина описания должна быть от 20 до 2000 символов")
    private String annotation;

    @NotNull(message = "id категории не может быть пустым")
    private Long category;

    @NotBlank(message = "Полное описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Полное описание должно быть от 20 до 7000 символов")
    private String description;

    @NotNull(message = "Время события не может быть пустым")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Место события не может быть пустым")
    @Valid
    private Location location;

    private boolean paid;

    @PositiveOrZero(message = "Количество участников не может быть меньше нуля")
    private int participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Заголовок события не может быть пустым")
    @Size(min = 3, max = 120, message = "Заголовок события должен быть от 3 до 120 символов")
    private String title;
}
