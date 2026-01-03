package com.example.demo.api;

import com.example.demo.service.BusanBankCrawlService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/busanbank")
public class BusanBankCrawlController {

    private final BusanBankCrawlService crawlService;

    public BusanBankCrawlController(BusanBankCrawlService crawlService) {
        this.crawlService = crawlService;
    }

    @GetMapping("/saving-products")
    public List<BusanBankCrawlService.ProductLink> savingProducts() {
        return crawlService.crawlSavingProducts();
    }
}
