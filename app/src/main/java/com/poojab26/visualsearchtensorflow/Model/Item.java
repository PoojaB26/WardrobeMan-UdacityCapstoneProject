
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



    public Item(String itemLabel, String itemDownloadUrl){
        this.itemLabel = itemLabel;
        this.itemDownloadUrl = itemDownloadUrl;
    }

    public Item(){}
}