# API 문서화 규칙 (OpenAPI/Swagger)
설명: SpringDoc 및 Swagger UI를 사용하여 REST API를 문서화하는 표준입니다.

## 구성
- **라이브러리**: `springdoc-openapi-starter-webmvc-ui` (Boot 3용 v2.x)
- **경로**: Swagger UI는 `/swagger-ui.html`, 문서는 `/v3/api-docs`

## 주석 표준
- **컨트롤러 레벨**: 도메인별로 엔드포인트를 그룹화하기 위해 `@Tag` 사용 (예: `Onboarding`, `Report`)
- **메서드 레벨**:
  - `@Operation`: 요약 및 설명 필수
  - `@ApiResponse`: 응답 코드 정의 (200, 400, 404, 500)
- **DTO 레벨**:
  - `@Schema`: 필드, 예제, 검증 제약 조건 설명

## 예제
```java
@Tag(name = "Onboarding", description = "사용자 온보딩 플로우 API")
@RestController
public class OnboardingController {

    @Operation(summary = "온보딩 시작", description = "새 세션 생성")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "세션 생성됨"),
        @ApiResponse(responseCode = "400", description = "잘못된 입력")
    })
    @PostMapping("/start")
    public ResponseEntity<SessionResponse> start(@RequestBody @Valid StartRequest request) { ... }
}
```

## 강제
- 모든 공개 API는 Swagger 문서가 있어야 함
- 적절한 보안 그룹화 없이 내부/관리자 API 노출하지 않기
