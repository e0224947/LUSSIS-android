package com.sa45team7.lussis.rest.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nhatton on 1/17/18.
 */

public class Stationery implements Comparable<Stationery> {

    @SerializedName("ItemNum")
    private String itemNum;

    @SerializedName("Category")
    private String category;

    @SerializedName("Description")
    private String description;

    @SerializedName("ReorderLevel")
    private int reorderLevel;

    @SerializedName("ReorderQty")
    private int reorderQty;

    @SerializedName("UnitOfMeasure")
    private String unitOfMeasure;

    @SerializedName("AvailableQty")
    private int availableQty;

    @SerializedName("BinNum")
    private String binNum;

    public String getItemNum() {
        return itemNum;
    }

    public void setItemNum(String itemNum) {
        this.itemNum = itemNum;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public int getReorderQty() {
        return reorderQty;
    }

    public void setReorderQty(int reorderQty) {
        this.reorderQty = reorderQty;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }

    public String getBinNum() {
        return binNum;
    }

    public void setBinNum(String binNum) {
        this.binNum = binNum;
    }

    @Override
    public int compareTo(@NonNull Stationery o) {
        if (category.equals(o.category)) return itemNum.compareTo(o.itemNum);
        return category.compareTo(o.category);
    }
}
