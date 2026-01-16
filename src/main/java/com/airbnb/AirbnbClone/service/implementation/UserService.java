package com.airbnb.AirbnbClone.service.implementation;

import com.airbnb.AirbnbClone.dto.ProfileUpdateRequestDto;
import com.airbnb.AirbnbClone.dto.UserDto;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.exceptions.ResourceNotFoundException;
import com.airbnb.AirbnbClone.mapper.UserMapper;
import com.airbnb.AirbnbClone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.airbnb.AirbnbClone.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + username));
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    public UserDto updateProfile(ProfileUpdateRequestDto profileUpdateRequestDto) {
        User user = getCurrentUser();
        if(profileUpdateRequestDto.getName() != null){
            user.setName(profileUpdateRequestDto.getName());
        }
        if(profileUpdateRequestDto.getDateOfBirth() != null){
            user.setDateOfBirth(profileUpdateRequestDto.getDateOfBirth());
        }
        if(profileUpdateRequestDto.getGender() != null){
            user.setGender(profileUpdateRequestDto.getGender());
        }

        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    public  UserDto getMyProfile() {
        User user = getCurrentUser();
        return userMapper.toUserDto(user);
    }
}
