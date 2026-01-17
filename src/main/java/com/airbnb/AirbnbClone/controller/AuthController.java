package com.airbnb.AirbnbClone.controller;


import com.airbnb.AirbnbClone.dto.LoginRequestDto;
import com.airbnb.AirbnbClone.dto.LoginResponseDto;
import com.airbnb.AirbnbClone.dto.SignupRequestDto;
import com.airbnb.AirbnbClone.dto.UserDto;
import com.airbnb.AirbnbClone.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping(path = "/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto user , HttpServletResponse response){
        return ResponseEntity.ok(authService.login(user , response));
    }


    @PostMapping(path = "/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignupRequestDto user){
        return  new ResponseEntity<>(authService.signUp(user) , HttpStatus.CREATED);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<LoginResponseDto> refresh(HttpServletRequest request){
        String refreshToken =   Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("refresh token is not present"));
        log.info("refreshToken {}" , refreshToken);
        return ResponseEntity.ok(authService.refresh(refreshToken));
    }
//
//    @DeleteMapping(path = "/logout/{username}")
//    public ResponseEntity<String> logout(@PathVariable String username){
//         authService.logout(username);
//
//        return ResponseEntity.ok("User Successfully logout");
//    }
}


