package com.example.easyshop.models;

public class PaymentMethod {
    public enum PaymentType { CARD, ONLINE, COD }

    public int id;
    public PaymentType type;
    public String cardBrand; // e.g. "Mastercard"
    public String last4;     // e.g. "3947"
    public String label;     // e.g. "**** **** **** 3947", "Online Payment", "Cash on delivery"
    public int iconRes;

    public PaymentMethod(int id, PaymentType type, String cardBrand, String last4, String label, int iconRes) {
        this.id = id;
        this.type = type;
        this.cardBrand = cardBrand;
        this.last4 = last4;
        this.label = label;
        this.iconRes = iconRes;
    }
}