package com.debugging.course.utils;

import java.time.LocalDate;

public class Customer {
    private String customerId;
    private String name;
    private String email;
    private boolean premium;
    private LocalDate memberSince;
    private int loyaltyYears;

    public Customer(String customerId, String name, String email, boolean premium) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.premium = premium;
        this.memberSince = LocalDate.now();
        this.loyaltyYears = 0;
    }

    public Customer(String customerId, String name, String email, boolean premium, int loyaltyYears) {
        this(customerId, name, email, premium);
        this.loyaltyYears = loyaltyYears;
        this.memberSince = LocalDate.now().minusYears(loyaltyYears);
    }

    // Getters and setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public LocalDate getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(LocalDate memberSince) {
        this.memberSince = memberSince;
    }

    public int getLoyaltyYears() {
        return loyaltyYears;
    }

    public void setLoyaltyYears(int loyaltyYears) {
        this.loyaltyYears = loyaltyYears;
    }

    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', premium=%s, loyaltyYears=%d}",
                customerId, name, premium, loyaltyYears);
    }
}