package com.example.demo.dto;

public record BusanProductSummary(
        ProductCategory category,
        String name,
        String code,          // fpcd 또는 카드/대출 상품 식별자(없으면 null 가능)
        String description,   // 목록에서 뽑힌 한 줄 설명(없으면 null)
        String extraText,     // 최고금리/혜택요약 등(없으면 null)
        String detailUrl      // 상세 페이지 URL(없으면 null 가능)
) {}
