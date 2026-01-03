package com.example.demo.api;

import com.example.demo.dto.BusanProductSummary;
import com.example.demo.dto.ProductCategory;
import com.example.demo.service.BusanBankCrawlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 기존 "예금 전용 + 디버그" 컨트롤러를
 * "부산은행 4카테고리 통합" 컨트롤러로 정리한 버전.
 *
 * - category=DEPOSIT / CHECKING / LOAN / CARD
 * - limit 기본 30
 */
@RestController
@RequestMapping("/api/busanbank")
public class BusanBankCrawlController {

    private final BusanBankCrawlService crawlService;

    public BusanBankCrawlController(BusanBankCrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @GetMapping("/products")
    public List<BusanProductSummary> products(
            @RequestParam ProductCategory category,
            @RequestParam(defaultValue = "30") int limit
    ) {
        return crawlService.crawlProducts(category, limit);
    }
}
