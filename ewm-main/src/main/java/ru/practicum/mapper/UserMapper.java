package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

@UtilityClass
public class UserMapper {

    public static User mapToUser(NewUserRequest newUserRequest) {
        User user = new User();

        user.setEmail(newUserRequest.getEmail());
        user.setName(newUserRequest.getName());

        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());

        return userDto;
    }

    public static UserShortDto mapToUserShortDto(User user) {
        UserShortDto dto = new UserShortDto();

        dto.setId(user.getId());
        dto.setName(user.getName());

        return dto;
    }
}
