package com.example.demo.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import com.example.demo.dto.BusanSavingProductSummary;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusanBankCrawlService {

    private static final String SAVING_SEED_URL =
            "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012009001";
    private static final String CHECKING_URL =
            "https://www.busanbank.co.kr/ib20/mnu/FPMDPO012001001";

    // onclick 안에서 상품코드(FPCD 같은 값)를 뽑아내기 위한 정규식
    // 예: goDetail('FPCD0000000...') 또는 fnDetail("FPCD...")
    private static final Pattern CODE_PATTERN = Pattern.compile("['\"](FPCD\\w+)['\"]");

    public List<BusanSavingProductSummary> crawlSavingProducts(int limit) {
    try {
        Document doc = Jsoup.connect(SAVING_SEED_URL)
                .userAgent("ChatBNKBot/1.0")
                .timeout(8000)
                .get();

        List<BusanSavingProductSummary> out = new ArrayList<>();

        for (Element a : doc.select("a.FPCD_DTL[fpcd]")) {
            String name = a.text().trim();
            String fpcd = a.attr("fpcd").trim();
            if (name.isBlank() || fpcd.isBlank()) continue;

            String mkpdCd = a.attr("mkpd_cd").trim();       // 있으면 저장
            String fpHlvDvcd = a.attr("fp_hlv_dvcd").trim(); // 있으면 저장

            // 같은 카드(리스트 아이템) 단위에서 설명/금리 추출
            Element card = a.closest("li.clearfix");
            String desc = "";
            String maxRateText = "";

            if (card != null) {
                Element descEl = card.selectFirst(".desc");
                if (descEl != null) desc = descEl.text().trim();

                // "최고 연 2.05%" 같은 텍스트가 tag 영역에 있음 (너가 outerHtml에서 확인됨)
                Element tagEl = card.selectFirst(".tag");
                if (tagEl != null) maxRateText = tagEl.text().replaceAll("\\s+", " ").trim();
            }

            String detailUrl = "https://www.busanbank.co.kr/ib20/mnu/FPMPDTDT0000001?FPCD=" + fpcd;

            out.add(new BusanSavingProductSummary(
                    name, fpcd, mkpdCd, fpHlvDvcd, desc, maxRateText, detailUrl
            ));

            if (out.size() >= limit) break;
        }

        return out;

    } catch (Exception e) {
        throw new IllegalStateException("부산은행 예금 목록 크롤링 실패", e);
    }
}

    private static boolean isNoiseName(String name) {
        // Swagger 결과에서 보였던 것들 + 흔한 UI 텍스트 차단
        String[] noise = {
                "본문으로 바로가기", "로그인", "인증센터", "Global", "스킨설정", "확대/축소",
                "마이메뉴등록", "화면인쇄", "엑셀저장", "검색", "상세조건검색", "바로가기",
                "썸네일로보기", "목록으로보기", "정렬", "영업점가입", "인터넷가입", "스마트폰가입",
                "이전", "다음", "닫기", "FAMILY SITE", "BNK금융네트워크"
        };
        for (String n : noise) {
            if (name.contains(n)) return true;
        }
        // 너무 짧은 건 버튼일 확률 높음
        return name.length() <= 1;
    }

    private static String extractProductCode(String onclick) {
        if (onclick == null || onclick.isBlank()) return null;
        Matcher m = CODE_PATTERN.matcher(onclick);
        if (m.find()) return m.group(1);
        return null;
    }

    private static String buildDetailUrlFromCode(String code) {
        // 여기 URL은 “실제 상세페이지 패턴” 확인 후 맞추면 됨.
        // MVP 단계에서는 코드가 제대로 뽑히는지 확인하는 게 1차 목표.
        // 일단 부산은행 도메인 + 공통 상세 뷰로 가정한 형태(placeholder)
        // ↓↓↓ 다음 단계에서 정확한 상세 URL로 교체할 예정
        return "https://www.busanbank.co.kr/ib20/mnu/FPMPDTDT0000001?FPCD=" + code;
    }

    public record ProductLink(String name, String url) {}

    public List<LinkDebug> debugSavingAnchors(int limit) {
    try {
        Document doc = Jsoup.connect(SAVING_SEED_URL)
                .userAgent("ChatBNKBot/1.0")
                .timeout(8000)
                .get();

        List<LinkDebug> out = new ArrayList<>();

        for (Element a : doc.select("a")) {
            String name = a.text().trim();
            if (name.isBlank()) continue;

            String href = a.attr("href");
            String absHref = a.absUrl("href");
            String onclick = a.attr("onclick");

            // 메뉴/상단 네비게이션에서 흔히 나오는 javascript 링크는 제외
            if (href.startsWith("javascript:goMenu(")) continue;
            if (href.startsWith("javascript:goSiteMenu(")) continue;
            if (href.startsWith("javascript:_gf_topSiteMenu_Init(")) continue;

            // UI 버튼류 제거
            if (isNoiseName(name)) continue;

            // 상품명처럼 보이는 텍스트만 남기기
            // (정기/예금/적금/저축/특판/주택청약 등 키워드 기반)
            boolean looksLikeProduct =
                    name.contains("예금") || name.contains("적금") || name.contains("저축") ||
                    name.contains("정기") || name.contains("특판") || name.contains("청약") ||
                    name.contains("내맘대로") || name.contains("LIVE") || name.contains("BNK");

            if (!looksLikeProduct) continue;

            // 디버그용으로 href/onclick을 그대로 확인
            out.add(new LinkDebug(
                    name,
                    href,
                    absHref,
                    onclick.length() > 200 ? onclick.substring(0, 200) : onclick
            ));

            if (out.size() >= limit) break;
        }

        return out;

    } catch (Exception e) {
        throw new IllegalStateException("부산은행 예금 목록 디버그 크롤링 실패", e);
    }
  }

  public record LinkDebug(String name, String href, String absHref, String onclick) {}



  public List<NodeDebug> debugSavingProductNodes(int limit) {
    try {
        Document doc = Jsoup.connect(SAVING_SEED_URL)
                .userAgent("ChatBNKBot/1.0")
                .timeout(8000)
                .get();

        List<NodeDebug> out = new ArrayList<>();

        for (Element a : doc.select("a")) {
            String name = a.text().trim();
            if (name.isBlank()) continue;

            // 상품명처럼 보이는 것만
            boolean looksLikeProduct =
                    name.contains("예금") || name.contains("적금") || name.contains("저축") ||
                    name.contains("정기") || name.contains("특판") || name.contains("청약") ||
                    name.contains("내맘대로") || name.contains("LIVE") || name.contains("BNK");

            if (!looksLikeProduct) continue;
            if (isNoiseName(name)) continue;

            // 상품 a태그 자체 속성 덤프
            Map<String, String> aAttrs = extractAttrs(a);

            // 부모/조부모 속성도 덤프 (이벤트 위임 대비)
            Element parent = a.parent();
            Map<String, String> pAttrs = parent != null ? extractAttrs(parent) : Map.of();

            Element gp = (parent != null) ? parent.parent() : null;
            Map<String, String> gpAttrs = gp != null ? extractAttrs(gp) : Map.of();

            // outerHtml 일부도 같이(너무 길면 잘라서)
            String aHtml = trimLen(a.outerHtml(), 300);
            String pHtml = parent != null ? trimLen(parent.outerHtml(), 300) : "";
            String gpHtml = gp != null ? trimLen(gp.outerHtml(), 300) : "";

            out.add(new NodeDebug(name, aAttrs, pAttrs, gpAttrs, aHtml, pHtml, gpHtml));
            if (out.size() >= limit) break;
        }

        return out;

    } catch (Exception e) {
        throw new IllegalStateException("부산은행 예금 목록 디버그2 실패", e);
    }
}

private static Map<String, String> extractAttrs(Element el) {
    Map<String, String> m = new LinkedHashMap<>();
    el.attributes().forEach(attr -> m.put(attr.getKey(), attr.getValue()));

    // 자주 보는 것들은 보기 좋게 보강
    if (el.hasAttr("class")) m.put("_class", el.className());
    if (el.hasAttr("id")) m.put("_id", el.id());

    // href/onclick은 항상 확인
    if (el.hasAttr("href")) m.put("_href", el.attr("href"));
    if (el.hasAttr("onclick")) m.put("_onclick", el.attr("onclick"));

    return m;
}

private static String trimLen(String s, int max) {
    if (s == null) return "";
    return s.length() > max ? s.substring(0, max) : s;
}

public record NodeDebug(
        String name,
        Map<String, String> aAttrs,
        Map<String, String> parentAttrs,
        Map<String, String> grandParentAttrs,
        String aOuterHtml,
        String parentOuterHtml,
        String grandParentOuterHtml
) {}

public ProductNodeDebug debugFindByName(String keyword) {
    try {
        Document doc = Jsoup.connect(SAVING_SEED_URL)
                .userAgent("ChatBNKBot/1.0")
                .timeout(8000)
                .get();

        // a 태그 중에서 keyword를 포함하는 첫 번째를 찾기
        Element targetA = null;
        for (Element a : doc.select("a")) {
            String name = a.text().trim();
            if (!name.isBlank() && name.contains(keyword)) {
                targetA = a;
                break;
            }
        }

        if (targetA == null) {
            return new ProductNodeDebug(
                    keyword,
                    Map.of("error", "NOT_FOUND"),
                    Map.of(),
                    Map.of(),
                    Map.of(),
                    "", "", "", ""
            );
        }

        Element p1 = targetA.parent();
        Element p2 = (p1 != null) ? p1.parent() : null;
        Element p3 = (p2 != null) ? p2.parent() : null;

        return new ProductNodeDebug(
                targetA.text().trim(),
                extractAttrs(targetA),
                p1 != null ? extractAttrs(p1) : Map.of(),
                p2 != null ? extractAttrs(p2) : Map.of(),
                p3 != null ? extractAttrs(p3) : Map.of(),
                trimLen(targetA.outerHtml(), 500),
                p1 != null ? trimLen(p1.outerHtml(), 500) : "",
                p2 != null ? trimLen(p2.outerHtml(), 500) : "",
                p3 != null ? trimLen(p3.outerHtml(), 500) : ""
        );

    } catch (Exception e) {
        throw new IllegalStateException("부산은행 상품 DOM 검색 디버그 실패", e);
    }
}

public record ProductNodeDebug(
        String name,
        Map<String, String> aAttrs,
        Map<String, String> parentAttrs,
        Map<String, String> grandParentAttrs,
        Map<String, String> greatGrandParentAttrs,
        String aOuterHtml,
        String parentOuterHtml,
        String grandParentOuterHtml,
        String greatGrandParentOuterHtml
) {}
    public List<BusanSavingProductSummary> crawlCheckingProducts(int limit) {
    return crawlProductList(CHECKING_URL, limit);
}
}
