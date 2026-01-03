# ChatBNK

자연어 기반 부산은행 금융상품 검색 챗봇 (Backend 중심 프로젝트)

사용자의 일상적인 질문을 이해하여
BNK부산은행 공식 홈페이지를 실시간 크롤링하고
Redis 캐시 + MySQL 로그 저장을 통해 빠르고 신뢰성 있게
금융상품 정보를 제공하는 백엔드 중심 프로젝트입니다.

# 프로젝트 개요

ChatBNK는 금융 취약자도 쉽게 사용할 수 있는 금융 정보 챗봇을 목표로 시작한 개인 프로젝트입니다.
복잡한 메뉴 탐색 없이,

“예금 알려줘”

“입출금 통장 추천”

“대출 상품 뭐 있어?”

“카드 추천해줘”

와 같은 자연어 질문만으로
BNK부산은행의 공식 금융상품 정보를 요약 제공합니다.

## 공식 홈페이지 기반 크롤링만 사용

## 핵심 기능
## 1️. 자연어 기반 의도 분류

사용자의 질문을 분석하여 아래 4가지 카테고리로 분류합니다.

카테고리	예시 질문
예금	“예금 알려줘”, “돈 모으고 싶어”
입출금	“입출금 통장 추천”
대출	“대출 상품 알려줘”
카드	“카드 추천해줘”
## 2️. BNK부산은행 공식 페이지 크롤링

각 카테고리별 실제 공식 상품 목록 페이지를 Jsoup으로 크롤링합니다.

```text
카테고리	URL
예금	/mnu/FPMDPO012009001
입출금	/mnu/FPMDPO012001001
대출	/mnu/FPMLON092100000
카드	/mnu/FPMCRD122000001
```text

공통 파서 전략

상품명

상품 코드(FPCD)

설명

최고 금리/혜택 요약

상세 페이지 URL 자동 생성

## 3. Redis 캐시 적용 (성능 최적화)

동일 질문 재요청 시 크롤링 없이 Redis에서 즉시 응답

첫 요청: cacheHit=false

이후 요청: cacheHit=true

효과

응답 속도 대폭 개선

외부 사이트 요청 최소화

실시간 + 캐시 구조 모두 경험 가능

## 4️. MySQL 로그 저장

사용자의 모든 질의는 MySQL에 저장됩니다.

저장 정보 예시:
질의 문장
분류된 카테고리
캐시 히트 여부
요청 시각


## 기술 스택
Backend

Java 21
Spring Boot 4.0.1
Spring Web MVC
Spring Data JPA
Spring Data Redis
Jsoup (HTML 크롤링)
Infra
MySQL 8 (Docker)
Redis (Docker)
Tooling
Gradle
Swagger (OpenAPI)
Git / GitHub

# 프로젝트 구조
```text
src/main/java/com/example/demo
├── api
│   ├── ChatQueryController.java        # 자연어 질의 API
│   └── BusanBankCrawlController.java   # 크롤링 테스트/디버그 API
│
├── service
│   ├── ChatQueryService.java            # 의도 분류 + 캐시 처리
│   └── BusanBankCrawlService.java       # 부산은행 크롤링 로직
│
├── dto
│   ├── ChatResponse.java
│   └── BusanProductSummary.java         # 공통 상품 응답 DTO
│
├── repository
│   └── QueryLogRepository.java          # 질의 로그 저장
│
├── entity
│   └── QueryLog.java
│
├── config
│   └── RedisConfig.java
│
└── DemoApplication.java
```text

## API 예시
자연어 질의
GET /api/chat/query?q=카드 추천&limit=5

응답 예시
```text
{
  "category": "CARD",
  "cacheHit": true,
  "products": [
    {
      "category": "CARD",
      "name": "부산 동백전 체크카드",
      "code": "0010500125",
      "description": "부산지역 경제활성화를 위한 지역화폐 카드",
      "detailUrl": "https://www.busanbank.co.kr/ib20/mnu/FPMPDTDT0000001?FPCD=0010500125"
    }
  ]
}
```text

## 실행 방법
## 1. Docker로 DB & Redis 실행
실행
docker compose up -d
종료
docker compose down

## 2️. 애플리케이션 실행
./gradlew bootRun

## 3️. Swagger 접속
http://localhost:8080/swagger-ui.html
