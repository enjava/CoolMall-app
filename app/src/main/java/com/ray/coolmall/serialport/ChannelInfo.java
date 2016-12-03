package com.ray.coolmall.serialport;

import java.util.Set;

import static java.lang.Integer.parseInt;

/**
 * Created by en on 2016/12/1.
 */

public class ChannelInfo {
    private String name;//货道名
    private int price;//价格
    private String productName;//商品名
    private int stock;//库存
    private int volume;//容量
    private long id;//商品ID

    public ChannelInfo(Set<String> sets) {
        if (sets==null)
            return;
        for (String set:sets){
            if(set.indexOf("price:")!=-1)
                this.price= parseInt(set.replace("price:",""));
            if(set.indexOf("channelname:")!=-1)
               name=set.replace("channelname:","");
            if(set.indexOf("stock:")!=-1)
                this.stock= parseInt(set.replace("stock:",""));
            if(set.indexOf("volume:")!=-1)
                this.volume= parseInt(set.replace("volume:",""));
            if(set.indexOf("id:")!=-1)
                this.id=Long.parseLong(set.replace("id:",""));
        }
    }

    public ChannelInfo() {
    }

    public ChannelInfo(String name, int price, String productName, int stock, int volume, long id) {
        this.name = name;
        this.price = price;
        this.productName = productName;
        this.stock = stock;
        this.volume = volume;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
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



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", productName='" + productName + '\'' +
                ", stock=" + stock +
                ", volume=" + volume +
                ", id=" + id +
                '}';
    }
}
