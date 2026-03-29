# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ChatBNK는 BNK 부산은행 금융상품 추천 챗봇이다. 사용자의 자연어 질의를 카테고리(예금/입출금/대출/카드)로 분류하고, BNK 웹사이트를 크롤링하여 상품 정보를 제공한다. Redis 캐싱 + MySQL 쿼리 로그 저장.

## 요청 흐름

```
[React Frontend :3000] → GET /api/chat/query?q=예금&limit=5 → [Spring Boot :8080]
    → ChatQueryController → ChatQueryService
        → IntentService (자연어 → DEPOSIT/CHECKING/LOAN/CARD/UNKNOWN)
        → Redis 캐시 확인 → 미스 시 BusanBankCrawlService (Jsoup 크롤링)
        → QueryLogRepository (MySQL 로그)
    → ChatResponse {category, cacheHit, products}
```

## Quick Start

```bash
# 1. 인프라 (MySQL 8 + Redis 7)
cd backend/demo/demo && docker compose up -d

# 2. 백엔드 (localhost:8080)
./gradlew bootRun

# 3. 프론트엔드 (localhost:3000)
cd frontend/bnk-app && npm install && npm start
```

각 디렉토리의 CLAUDE.md에 상세 정보가 있다.
