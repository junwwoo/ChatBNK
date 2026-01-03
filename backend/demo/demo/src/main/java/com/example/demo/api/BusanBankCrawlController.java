package com.example.demo.api;

import com.example.demo.service.BusanBankCrawlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.BusanSavingProductSummary;

import java.util.List;

@RestController
@RequestMapping("/api/busanbank")
public class BusanBankCrawlController {

    private final BusanBankCrawlService crawlService;

    public BusanBankCrawlController(BusanBankCrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @GetMapping("/saving-products")
    public List<BusanSavingProductSummary> savingProducts(
        @RequestParam(defaultValue = "30") int limit
    ) {
    return crawlService.crawlSavingProducts(limit);
    }

    @GetMapping("/saving-products/debug")
    public List<BusanBankCrawlService.LinkDebug> savingProductsDebug(
        @RequestParam(defaultValue = "200") int limit
    ) {
    return crawlService.debugSavingAnchors(limit);
    }

    @GetMapping("/saving-products/debug2")
    public List<BusanBankCrawlService.NodeDebug> savingProductsDebug2(
        @RequestParam(defaultValue = "10") int limit
    ) {
      return crawlService.debugSavingProductNodes(limit);
    }

    @GetMapping("/saving-products/find")
public BusanBankCrawlService.ProductNodeDebug findProductNode(
        @RequestParam String keyword
) {
    return crawlService.debugFindByName(keyword);
}
    @GetMapping("/checking-products")
    public List<BusanSavingProductSummary> checkingProducts(
        @RequestParam(defaultValue = "20") int limit
    ) {
    return service.crawlCheckingProducts(limit);
    }
}
