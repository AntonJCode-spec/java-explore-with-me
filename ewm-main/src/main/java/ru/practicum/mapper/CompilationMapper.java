package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public static Compilation mapToCompilation(NewCompilationDto dto, Set<Event> events) {
        Compilation compilation = new Compilation();

        compilation.setEvents(events);
        compilation.setPinned(dto.isPinned());
        compilation.setTitle(dto.getTitle());

        return compilation;
    }

    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();

        dto.setId(compilation.getId());
        List<EventShortDto> eventsDto = compilation.getEvents().stream()
                        .map(event -> EventMapper.mapToEventShortDto(event, 0L))
                                .toList();
        dto.setEvents(eventsDto);
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());

        return dto;
    }
}
