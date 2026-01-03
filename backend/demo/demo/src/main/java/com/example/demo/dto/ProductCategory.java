package com.example.demo.dto;

public enum ProductCategory {
    DEPOSIT,     // 예금/적금(목돈/저축)
    CHECKING,    // 입출금(자유입출금/통장) - 사용자가 말한 "출금"을 여기에 대응
    LOAN,        // 대출
    CARD,        // 카드
    UNKNOWN
}
