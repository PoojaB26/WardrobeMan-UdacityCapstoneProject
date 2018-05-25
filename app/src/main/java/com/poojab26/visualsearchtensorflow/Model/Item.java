
package com.poojab26.visualsearchtensorflow.Model;

public class Item {

    private String itemLabel;
    private String itemDownloadUrl;

    public String getItemLabel() {
        return itemLabel;
    }

    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }

    public String getItemDownloadUrl() {
        return itemDownloadUrl;
    }

    public void setItemDownloadUrl(String itemDownloadUrl) {
        this.itemDownloadUrl = itemDownloadUrl;
    }



/*
    public String getProductLabel() {
        return itemLabel;
    }

    public void setProductLabel(String productLabel) {
        this.itemLabel = productLabel;
    }

    public String getProductUrl() {
        return itemDownloadUrl;
    }

    public void setProductUrl(String productUrl) {
        this.itemDownloadUrl = productUrl;
    }*/

    public Item(String itemLabel, String itemDownloadUrl){
        this.itemLabel = itemLabel;
        this.itemDownloadUrl = itemDownloadUrl;
    }

    public Item(){}
}