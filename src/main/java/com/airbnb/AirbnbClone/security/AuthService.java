package com.airbnb.AirbnbClone.security;

import com.airbnb.AirbnbClone.dto.LoginRequestDto;
import com.airbnb.AirbnbClone.dto.LoginResponseDto;
import com.airbnb.AirbnbClone.dto.SignupRequestDto;
import com.airbnb.AirbnbClone.dto.UserDto;
import com.airbnb.AirbnbClone.entity.User;
import com.airbnb.AirbnbClone.entity.enums.UserRoles;
import com.airbnb.AirbnbClone.mapper.UserMapper;
import com.airbnb.AirbnbClone.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService service;
//    private final SessionService sessionService;

    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto , HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail() , loginRequestDto.getPassword())
        );
        User result = (User) authentication.getPrincipal();

        String accessToken = service.generateAccessToken(result);
        String refreshToken = service.generateRefreshToken(result);
//        sessionService.generateNewSession(result , refreshToken);


        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(result.getId())
                .name(result.getName())
                .email(result.getEmail())
                .build();
    }

    public UserDto signUp(SignupRequestDto signUpRequest) {
        userRepository.findByEmail(signUpRequest.getEmail()).ifPresent(existingUser -> {
            throw new BadCredentialsException("User already exists");
        });;

            User result = User.builder()
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .email(signUpRequest.getEmail())
                    .name(signUpRequest.getName())
                    .roles(Set.of(UserRoles.GUEST))
                    .build();

        userRepository.save(result);
        return userMapper.toUserDto(result);
    };

    public  LoginResponseDto refresh(String refreshToken) {
     Long userIdFromToken = service.getUserIdFromToken(refreshToken);
//     sessionService.validateSession(refreshToken);
     User user = userRepository.findById(userIdFromToken).orElseThrow();
     String accessToken = service.generateAccessToken(user);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();


    }

//    public void logout(String username){
//        User user = userRepository.findByEmail(username).orElseThrow();
//        sessionService.deleteByUser(user);
//
//    }
}
