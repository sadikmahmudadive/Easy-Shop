package com.example.easyshop.models;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    public String displayName;

    public enum PaymentType { CARD, ONLINE, COD }

    private int id;
    private PaymentType type;
    private String cardBrand; // e.g. "Mastercard"
    private String last4;     // e.g. "3947"
    private String label;     // e.g. "**** **** **** 3947", "Online Payment", "Cash on delivery"
    private int iconRes;

    public PaymentMethod(int id, PaymentType type, String cardBrand, String last4, String label, int iconRes) {
        this.id = id;
        this.type = type;
        this.cardBrand = cardBrand;
        this.last4 = last4;
        this.label = label;
        this.iconRes = iconRes;
    }

    public int getId() {
        return id;
    }

    public PaymentType getType() {
        return type;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public String getLast4() {
        return last4;
    }

    public String getLabel() {
        return label;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    /**
     * Returns a user-friendly display string for this payment method.
     */
    public String getDisplayName() {
        switch (type) {
            case CARD:
                return (cardBrand != null ? cardBrand + " " : "") +
                        (last4 != null ? "**** **** **** " + last4 : label != null ? label : "");
            case ONLINE:
                return label != null ? label : "Online Payment";
            case COD:
                return label != null ? label : "Cash on delivery";
            default:
                return label != null ? label : "";
        }
    }
}