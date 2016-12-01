package com.ray.coolmall.serialport;

/**
 * Created by en on 2016/12/1.
 */

public class ChannelInfo {
    private String name;
    private String price;
    private String productName;
    private int stock;
    private int volume;

    public ChannelInfo() {
    }

    public ChannelInfo(String name, String price, String productName, int stock, int volume) {
        this.name = name;
        this.price = price;
        this.productName = productName;
        this.stock = stock;
        this.volume = volume;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", productName='" + productName + '\'' +
                ", stock=" + stock +
                ", volume=" + volume +
                '}';
    }
}
