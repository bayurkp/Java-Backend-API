package com.bay.data;

public class Order {
    private int id;
    private int buyer;
    private String note;
    private int total;
    private int discount;
    private int isPaid;

//    public Order(int id, int buyer, String note, double total, int discount, int isPaid) {
//        this.id = id;
//        this.buyer = buyer;
//        this.note = note;
//        this.total = total;
//        this.discount = discount;
//        this.isPaid = isPaid;
//    }

    public Order() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyer() {
        return buyer;
    }

    public void setBuyer(int buyer) {
        this.buyer = buyer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int isPaid() {
        return isPaid;
    }

    public void setPaid(int paid) {
        isPaid = paid;
    }
}
