# Backend CLAUDE.md

Spring Boot 4.0.1 / Java 21 / Gradle 기반 백엔드. 소스 루트: `demo/demo/`

## Commands

```bash
cd demo/demo

# 인프라 실행 (MySQL 8 + Redis 7)
docker compose up -d

# 애플리케이션 실행
./gradlew bootRun

# 빌드
./gradlew build

# 테스트
./gradlew test

# Swagger UI: http://localhost:8080/swagger-ui.html
```

## 패키지 구조 (`demo/demo/src/main/java/com/example/demo/`)

- `api/` — REST 컨트롤러
  - `ChatQueryController` — `GET /api/chat/query?q={query}&limit={n}` 자연어 질의 처리
  - `BusanBankCrawlController` — `GET /api/busanbank/products?category={CAT}&limit={n}` 직접 크롤링
  - `QueryLogController` — `GET /api/logs/queries`, `GET /api/logs/cache-stats` 로그/통계
- `service/` — 비즈니스 로직
  - `ChatQueryService` — 오케스트레이션: 인텐트 분류 → 캐시 확인 → 크롤링 → 로그 저장
  - `IntentService` — 키워드 기반 자연어 분류 (대소문자/공백 무시)
    - CARD: "카드", "신용카드", "체크카드", "연회비", "혜택"
    - LOAN: "대출", "빌리", "상환", "한도"
    - CHECKING: "입출금", "자유입출금", "통장", "계좌", "출금"
    - DEPOSIT: "예금", "적금", "저축", "목돈", "돈모"
  - `BusanBankCrawlService` — Jsoup 웹 크롤링 (8초 타임아웃, UA: ChatBNKBot)
    - `crawlByFpcdStyle()`: 일반 상품 (예금/입출금/대출) — `a.FPCD_DTL[fpcd]` 셀렉터
    - `crawlCardStyle()`: 카드 상품 전용 HTML 구조
  - `QueryLogService` — 최근 50건 조회, 캐시 적중률 통계
- `dto/` — Record 기반 DTO
  - `BusanProductSummary` — category, name, code, description, extraText, detailUrl
  - `ChatResponse` — category, cacheHit, message, products
  - `QueryLogDto` — id, queryText, intent, cacheHit, resultCount, createdAt
  - `ProductCategory` enum — DEPOSIT, CHECKING, LOAN, CARD, UNKNOWN
- `domain/QueryLog` — JPA 엔티티 (queryText, intent, cacheHit, resultCount, createdAt)
- `repository/QueryLogRepository` — Spring Data JPA
- `config/JacksonConfig` — ObjectMapper에 JavaTimeModule 등록

## 캐싱

- Redis 키 패턴: `busan:{CATEGORY}:list:limit={n}`
- TTL: 10분 (`Duration.ofMinutes(10)`)
- JSON 직렬화된 `List<BusanProductSummary>` 저장

## 크롤링 대상 URL

| 카테고리 | URL |
|----------|-----|
| DEPOSIT | `busanbank.co.kr/ib20/mnu/FPMDPO012009001` |
| CHECKING | `busanbank.co.kr/ib20/mnu/FPMDPO012001001` |
| LOAN | `busanbank.co.kr/ib20/mnu/FPMLON092100000` |
| CARD | `busanbank.co.kr/ib20/mnu/FPMCRD122000001` |

상품 상세 URL: `busanbank.co.kr/ib20/mnu/FPMPDTDT0000001?FPCD={fpcd}`

## DB 설정 (application.yml)

- MySQL: `jdbc:mysql://localhost:3306/chatbnk`
- 자격증명: 환경변수 `DB_USERNAME`, `DB_PASSWORD`로 관리 (기본값: chatbnk/chatbnkpass)
- JPA: ddl-auto update, open-in-view false
- TimeZone: Asia/Seoul

## Docker (docker-compose.yml)

- `chatbnk-mysql` (MySQL 8.0) — port 3306, DB: chatbnk, utf8mb4
- `chatbnk-redis` (Redis 7-alpine) — port 6379
- DB 자격증명은 `.env` 파일에서 로드 (`.env.example` 참고)

## 주요 의존성

- spring-boot-starter-web, data-jpa, data-redis
- jsoup 1.17.2 (크롤링)
- springdoc-openapi 2.6.0 (Swagger)
- jackson-datatype-jsr310 (Java Time 직렬화)
- lombok
