package ru.practicum.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.EventNotFoundException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.storage.CompilationRepository;
import ru.practicum.storage.event.EventRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService{

    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto createNewCompilation(NewCompilationDto dto) {
        if (compilationRepository.existsByTitle(dto.getTitle())) {
            throw new DataConflictException("Подборка с таким названием уже существует");
        }

        Set<Event> events = new HashSet<>();
        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(dto.getEvents());

            if (events.size() != dto.getEvents().size()) {
                throw new EventNotFoundException("Одно или несколько событий не найдены");
            }

            for (Event event : events) {
                if (event.getState() != EventState.PUBLISHED) {
                    throw new DataConflictException("В подборку можно добавлять только опубликованные события");
                }
            }
        }

        Compilation compilation = CompilationMapper.mapToCompilation(dto, events);

        compilationRepository.save(compilation);

        return CompilationMapper.mapToCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Подборка с таким id не найдена");
        }

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с таким id не существует"));

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            if (!compilation.getTitle().equals(dto.getTitle())
                    && compilationRepository.existsByTitle(dto.getTitle())) {
                throw new DataConflictException("Подборка с таким названием уже существует");
            }
            compilation.setTitle(dto.getTitle());
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            Set<Event> events = new HashSet<>();
            if (!dto.getEvents().isEmpty()) {
                events = eventRepository.findByIdIn(dto.getEvents());

                if (events.size() != dto.getEvents().size()) {
                    throw new EventNotFoundException("Одно или несколько событий не найдены");
                }

                for (Event event : events) {
                    if (event.getState() != EventState.PUBLISHED) {
                        throw new DataConflictException("В подборку можно добавлять только опубликованные события");
                    }
                }
            }
            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);
        return CompilationMapper.mapToCompilationDto(compilation);
    }
}
