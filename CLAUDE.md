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
# 1. 환경변수 설정 (선택 — 미설정 시 기본값 사용)
cd backend/demo/demo
cp .env.example .env   # 필요 시 비밀번호 수정

# 2. 인프라 (MySQL 8 + Redis 7)
docker compose up -d

# 3. 백엔드 (localhost:8080)
./gradlew bootRun

# 4. 프론트엔드 (localhost:3000)
cd frontend/bnk-app && npm install && npm start
```

## 환경변수

DB 자격증명은 환경변수로 관리한다. `.env` 파일은 Git에서 제외되며, `.env.example`을 참고하여 생성한다.

| 변수 | 용도 | 기본값 |
|------|------|--------|
| `DB_USERNAME` | MySQL 사용자명 | `chatbnk` |
| `DB_PASSWORD` | MySQL 비밀번호 | `chatbnkpass` |
| `DB_ROOT_PASSWORD` | MySQL root 비밀번호 | `rootpass` |

각 디렉토리의 CLAUDE.md에 상세 정보가 있다.
