package com.bay.data;

public class Review {
    private int order;
    private int star;
    private String description;

    public Review(int order, int star, String description) {
        this.order = order;
        this.star = star;
        this.description = description;
    }

    public Review() {
        super();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
