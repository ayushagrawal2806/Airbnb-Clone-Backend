package com.airbnb.AirbnbClone.mapper;

import com.airbnb.AirbnbClone.dto.UserDto;
import com.airbnb.AirbnbClone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "gender" , target = "gender")
    @Mapping(source = "dateOfBirth" , target = "dateOfBirth")
    UserDto toUserDto(User user);
}
