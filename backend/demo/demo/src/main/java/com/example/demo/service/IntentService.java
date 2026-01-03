package com.example.demo.service;

import com.example.demo.dto.ProductCategory;
import org.springframework.stereotype.Service;

@Service
public class IntentService {

    public ProductCategory classify(String q) {
        if (q == null) return ProductCategory.UNKNOWN;
        String s = q.replaceAll("\\s+", "").toLowerCase();

        // 카드
        if (s.contains("카드") || s.contains("신용카드") || s.contains("체크카드") || s.contains("연회비") || s.contains("혜택")) {
            return ProductCategory.CARD;
        }

        // 대출
        if (s.contains("대출") || s.contains("빌리") || s.contains("상환") || s.contains("한도")) {
            return ProductCategory.LOAN;
        }

        // 입출금(출금/통장/계좌)
        if (s.contains("입출금") || s.contains("자유입출금") || s.contains("통장") || s.contains("계좌") || s.contains("출금")) {
            return ProductCategory.CHECKING;
        }

        // 예금/적금/저축
        if (s.contains("예금") || s.contains("적금") || s.contains("저축") || s.contains("목돈") || s.contains("돈모") || s.contains("돈모으")) {
            return ProductCategory.DEPOSIT;
        }

        return ProductCategory.UNKNOWN;
    }
}
