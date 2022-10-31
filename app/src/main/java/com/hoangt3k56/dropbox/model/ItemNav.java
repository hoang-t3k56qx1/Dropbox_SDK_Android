package com.hoangt3k56.dropbox.model;

public class ItemNav {
    private int src;
    private String name;

    public ItemNav(int src, String name) {
        this.src = src;
        this.name = name;
    }

    public ItemNav() {
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ItemNav{" +
                "src=" + src +
                ", name='" + name + '\'' +
                '}';
    }
}
