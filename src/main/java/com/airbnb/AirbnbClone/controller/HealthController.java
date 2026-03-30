package com.airbnb.AirbnbClone.controller;



import com.airbnb.AirbnbClone.advice.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/health")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping
    public ApiResponse<String> healthCheck(){
        return new ApiResponse<>("Backend Running fine");
    }
}
