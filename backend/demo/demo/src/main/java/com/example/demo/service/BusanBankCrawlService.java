package com.example.demo.service;

import com.example.demo.dto.BusanProductSummary;
import com.example.demo.dto.ProductCategory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusanBankCrawlService {

    // 예금/적금(전체상품)
    private static final String DEPOSIT_SEED_URL =
            "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012009001";

    // 예: 입출금 상품 목록, 대출 상품 목록, 카드 상품 목록 페이지
    private static final String CHECKING_SEED_URL = 
            "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012001001";
    private static final String LOAN_SEED_URL = 
            "https://www.busanbank.co.kr/ib20/mnu/FPMLON092100000";
    private static final String CARD_SEED_URL = 
            "https://www.busanbank.co.kr/ib20/mnu/FPMCRD122000001";

    public List<BusanProductSummary> crawlProducts(ProductCategory category, int limit) {
        return switch (category) {
            case DEPOSIT -> crawlByFpcdStyle(category, DEPOSIT_SEED_URL, limit);
            case CHECKING -> crawlByFpcdStyle(category, CHECKING_SEED_URL, limit);
            case LOAN -> crawlByFpcdStyle(category, LOAN_SEED_URL, limit);
            case CARD -> {
             List<BusanProductSummary> first = crawlByFpcdStyle(category, CARD_SEED_URL, limit);
                yield first.isEmpty() ? crawlCardStyle(category, CARD_SEED_URL, limit) : first;
            }
            default -> List.of();
        };
    }

    /**
     * 부산은행 예금 페이지에서 성공했던 방식: a.FPCD_DTL + fpcd 속성 기반
     * 입출금/대출도 같은 구조면 그대로 동작함.
     */
    private List<BusanProductSummary> crawlByFpcdStyle(ProductCategory category, String seedUrl, int limit) {
        if (seedUrl == null || seedUrl.startsWith("TODO")) {
            // 아직 URL 확정 전이면 빈 리스트
            return List.of();
        }

        try {
            Document doc = Jsoup.connect(seedUrl)
                    .userAgent("ChatBNKBot/1.0")
                    .timeout(8000)
                    .get();

            List<BusanProductSummary> out = new ArrayList<>();

            for (Element a : doc.select("a.FPCD_DTL[fpcd]")) {
                String name = a.text().trim();
                String fpcd = a.attr("fpcd").trim();
                if (name.isBlank() || fpcd.isBlank()) continue;

                Element card = a.closest("li.clearfix");

                String desc = null;
                String extraText = null;

                if (card != null) {
                    Element descEl = card.selectFirst(".desc");
                    if (descEl != null) desc = descEl.text().trim();

                    Element tagEl = card.selectFirst(".tag");
                    if (tagEl != null) extraText = tagEl.text().replaceAll("\\s+", " ").trim();
                }

                // 예금 상세 URL 패턴(예금에서 확인됨)
                // 다른 카테고리도 fpcd 상세가 동일하게 열리면 그대로 사용 가능
                String detailUrl = "https://www.busanbank.co.kr/ib20/mnu/FPMPDTDT0000001?FPCD=" + fpcd;

                out.add(new BusanProductSummary(
                        category,
                        name,
                        fpcd,
                        desc,
                        extraText,
                        detailUrl
                ));

                if (out.size() >= limit) break;
            }

            return out;
        } catch (Exception e) {
            throw new IllegalStateException("부산은행 크롤링 실패 category=" + category + " url=" + seedUrl, e);
        }
    }

    /**
     * 카드 페이지는 구조가 다를 가능성이 높아서 따로 함수 분리.
     * 우선 “링크/텍스트 기반”으로 최대한 뽑아오는 기본 구현.
     * 나중에 debug로 data-* 코드나 정확한 selector 찾으면 여기만 보강하면 됨.
     */
    private List<BusanProductSummary> crawlCardStyle(ProductCategory category, String seedUrl, int limit) {
        if (seedUrl == null || seedUrl.startsWith("TODO")) {
            return List.of();
        }

        try {
            Document doc = Jsoup.connect(seedUrl)
                    .userAgent("ChatBNKBot/1.0")
                    .timeout(8000)
                    .get();

            List<BusanProductSummary> out = new ArrayList<>();

            for (Element a : doc.select("a")) {
                String name = a.text().trim();
                if (name.isBlank()) continue;

                // 카드 관련 키워드가 포함된 링크만 대충 추리기(초기 MVP)
                if (!(name.contains("카드") || name.contains("Card") || name.contains("CARD"))) continue;

                String href = a.absUrl("href");
                if (href == null || href.isBlank()) continue;

                out.add(new BusanProductSummary(
                        category,
                        name,
                        null,
                        null,
                        null,
                        href
                ));

                if (out.size() >= limit) break;
            }

            return out;

        } catch (Exception e) {
            throw new IllegalStateException("부산은행 카드 크롤링 실패 url=" + seedUrl, e);
        }
    }
}
