package sync.slamtalk.map.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.service.BasketballCourtService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/map")
public class BasketballCourtController {
    private final BasketballCourtService basketballCourtService;


    //전체 농구장 간략 정보
    @GetMapping
    @Operation(
            summary = "전체 농구장 간략 정보", // 기능 제목 입니다
            description = "이 기능은 마커에 띄울 전체 농구장의 간략 정보 응답을 보내는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<List<BasketballCourtDto>> getAllCourtSummaryInfo() {
        List<BasketballCourtDto> courtDetails = basketballCourtService.getAllCourtSummaryInfo();
        return (ApiResponse.ok(courtDetails, "농구장 목록을 성공적으로 가져왔습니다."));
    }


    //특정 농구장 전체 정보
    @GetMapping("/{courtId}")
    @Operation(
            summary = "마커 클릭 농구장 전체 정보", // 기능 제목 입니다
            description = "이 기능은 클릭한 마커에 해당하는 농구장의 전체 정보 응답을 보내는 기능입니다.", // 기능 설명
            tags = {"지도"}
    )
    public ApiResponse<BasketballCourtDto> getCourtFullInfoById(@PathVariable Long courtId) {

            BasketballCourtDto basketballCourtDto = basketballCourtService.getCourtFullInfoById(courtId);
            return ApiResponse.ok(basketballCourtDto, "농구장 상세 정보를 성공적으로 가져왔습니다.");

    }

}