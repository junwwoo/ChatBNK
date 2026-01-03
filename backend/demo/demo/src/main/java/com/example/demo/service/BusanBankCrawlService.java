package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusanBankCrawlService {

    private static final String SAVING_SEED_URL =
            "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012009001";

    public List<ProductLink> crawlSavingProducts() {
        try {
            Document doc = Jsoup.connect(SAVING_SEED_URL)
                    .userAgent("ChatBNKBot/1.0")
                    .timeout(8000)
                    .get();

            List<ProductLink> results = new ArrayList<>();

            // 1) 모든 a 태그 수집
            for (Element a : doc.select("a[href]")) {
                String href = a.absUrl("href");
                String text = a.text().trim();

                // 2) 부산은행 내부 상품 상세로 보이는 링크만 필터
                if (!href.startsWith("https://www.busanbank.co.kr/ib20/mnu/")) continue;
                if (text.isBlank()) continue;

                // 3) "상세", "바로가기" 등 UI 링크 제외
                if (text.contains("페이지") || text.contains("이전") || text.contains("다음")) continue;

                results.add(new ProductLink(text, href));
            }

            return results;

        } catch (Exception e) {
            throw new IllegalStateException("부산은행 예금 목록 크롤링 실패", e);
        }
    }

    public record ProductLink(String name, String url) {}
}
