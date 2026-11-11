package ru.practicum.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location{

    @NotNull(message = "Координаты обязательное поле")
    private Float lat;

    @NotNull(message = "Координаты обязательное поле")
    private Float lon;
}
