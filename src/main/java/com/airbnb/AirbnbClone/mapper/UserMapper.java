package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.UserDto;
import com.airbnb.AirbnbClone.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);
}
