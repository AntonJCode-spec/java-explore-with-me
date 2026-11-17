package ru.practicum.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.UserEmailConflictException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.storage.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new UserEmailConflictException("Пользователь с таким email уже существует");
        }

        User userToSave = UserMapper.mapToUser(newUserRequest);

        userRepository.save(userToSave);

        return UserMapper.mapToUserDto(userToSave);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }

        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Параметры размеров выборки не корректны");
        }

        List<User> result;

        if (ids == null || ids.isEmpty()) {
            result = userRepository.findUsersWithOffsetAndSize(from, size);
        } else {
            result = userRepository.findUsersWithOffsetAndSizeWhereIdIn(ids, from, size);
        }

        return result.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}
