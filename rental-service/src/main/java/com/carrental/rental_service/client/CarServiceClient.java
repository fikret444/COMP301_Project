package com.carrental.rental_service.client;

import com.carrental.rental_service.dto.CarDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "car-service")
public interface CarServiceClient {
    
    @GetMapping("/api/cars/{id}")
    CarDto getCarById(@PathVariable("id") String id);
}

