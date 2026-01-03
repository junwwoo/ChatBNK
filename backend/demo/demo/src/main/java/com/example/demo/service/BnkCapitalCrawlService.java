package com.example.demo.service;

import com.example.demo.dto.BnkCapitalLoanSummary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BnkCapitalCrawlService {

    private static final String LOAN_URL =
            "https://web.bnkcapital.co.kr/view/main/view/MainView010M01";

    public List<BnkCapitalLoanSummary> crawlLoanProducts(int limit) {
        try {
            Document doc = Jsoup.connect(LOAN_URL)
                    .userAgent("ChatBNKBot/1.0")
                    .timeout(8000)
                    .get();

            List<BnkCapitalLoanSummary> result = new ArrayList<>();

            for (Element card : doc.select(".loan, .product, li")) {
                String name = card.select("h3, strong, .title").text();
                if (name.isBlank()) continue;

                String desc = card.select("p, .desc").text();
                String type = name.contains("신용") ? "신용대출" :
                              name.contains("자동차") ? "자동차대출" :
                              "기타대출";

                result.add(new BnkCapitalLoanSummary(
                        name,
                        desc,
                        type,
                        LOAN_URL
                ));

                if (result.size() >= limit) break;
            }

            return result;

        } catch (Exception e) {
            throw new IllegalStateException("BNK캐피탈 대출 크롤링 실패", e);
        }
    }
}
