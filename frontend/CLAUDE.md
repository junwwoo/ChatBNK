# Frontend CLAUDE.md

React 19 / Create React App / Tailwind CSS 3 기반 프론트엔드. 소스 루트: `bnk-app/`

## Commands

```bash
cd bnk-app
npm install
npm start       # 개발 서버 (localhost:3000)
npm run build   # 프로덕션 빌드
npm test        # Jest + React Testing Library
```

## 컴포넌트 구조 (`bnk-app/src/`)

- `App.js` — 메인 컴포넌트
  - state: `results` (상품 목록), `selected` (모달용 선택 상품)
  - 현재 mock 데이터 사용 중 (백엔드 API 연동 대기)
  - 검색 키워드 하이라이팅 기능 포함
- `components/SearchBar.js` — 검색 입력 (빈 입력 방지, onSearch 콜백)
- `components/ResultCard.js` — 상품 카드 (title, summary HTML 렌더링, onClick)
- `components/SummaryModal.js` — 상품 상세 모달 (overlay, 닫기/서비스 이동 버튼)

## 스타일링

- Tailwind CSS 3.4.18 (`tailwind.config.js` — content: `./src/**/*.{js,jsx,ts,tsx}`)
- PostCSS + Autoprefixer
- BNK 브랜딩 컬러: 레드 계열 (`#dc2626`)
- 검색 매치 하이라이트: 노란색 배경

## 백엔드 연동 (TODO)

현재 `App.js`에서 mock 데이터로 1초 딜레이 시뮬레이션 중. 실제 연동 시:
- `GET http://localhost:8080/api/chat/query?q={query}&limit={n}`
- 응답: `{ category, cacheHit, products: [{ name, code, description, extraText, detailUrl }] }`
