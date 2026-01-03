package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class IntentService {

    public Intent classify(String q) {
    if (q == null) return Intent.UNKNOWN;
    String s = q.replaceAll("\\s+", "").toLowerCase();

    // 예금 / 적금
    if (s.contains("예금") || s.contains("적금") || s.contains("저축") || s.contains("돈모")) {
        return Intent.SAVING;
    }

    // 출금 / 입출금
    if (s.contains("출금") || s.contains("입출금") || s.contains("통장") || s.contains("체크")) {
        return Intent.CHECKING;
    }

    // 대출
    if (s.contains("대출") || s.contains("빌리") || s.contains("론") || s.contains("자금")) {
        return Intent.LOAN;
    }

    return Intent.UNKNOWN;
}

    public enum Intent {
    SAVING,        // 예금/적금
    CHECKING,      // 입출금/출금
    LOAN,          // 대출
    UNKNOWN
}
}
