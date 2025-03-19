package org.example.expert.domain.manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message; // 로그 상세 메시지
    private Long userId; // 요청한 사용자 ID
    private LocalDateTime createdAt; // 로그 생성 시간

    public Log(String message, Long userId) {
        this.message = message;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }
}
