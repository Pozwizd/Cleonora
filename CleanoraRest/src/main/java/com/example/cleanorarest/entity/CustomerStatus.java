package com.example.cleanorarest.entity;

import lombok.Getter;

@Getter
public enum CustomerStatus {
    NEW("Новый"),
    ACTIVE("Активный"),
    BLOCKED("Удалённый"),;

    CustomerStatus(String statusName) {
        this.statusName = statusName;
    }

    private final String statusName;

}