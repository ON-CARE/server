package com.example.controller.map;

import com.example.dto.map.Facility;
import com.example.service.map.KakaoLocalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@Tag(name = "지도 API", description = "시설 정보 검색을 위한 API")
class FacilityController {
    private final KakaoLocalSearchService kakaoLocalSearchService;

    public FacilityController(KakaoLocalSearchService kakaoLocalSearchService) {
        this.kakaoLocalSearchService = kakaoLocalSearchService;
    }

    @Operation(summary = "주변 노인복지 시설 검색", description = "사용자 위치 주변의 노인복지 시설을 검색합니다.")
    @GetMapping("/nearby")
    @SecurityRequirement(name = "apiKey")  // Authorization 헤더가 필요한 메서드에 설정
    public ResponseEntity<List<Facility>> getNearbyFacilities() {
        double longitude = 126.9784; // 예시 경도 값 (추후 사용자 위치 기반으로 변경 가능)
        double latitude = 37.5665; // 예시 위도 값 (추후 사용자 위치 기반으로 변경 가능)

        List<Facility> facilities = kakaoLocalSearchService.searchNearbyFacilities(longitude, latitude);
        return ResponseEntity.ok(facilities);
    }
}

