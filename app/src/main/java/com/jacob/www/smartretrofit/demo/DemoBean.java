package com.jacob.www.smartretrofit.demo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DemoBean implements Serializable {

    private static final long serialVersionUID = -686840386681616145L;
    /**
     * item : {"promotions":"【金秋滋补，拍下减40】更有原价买6免1，即立减322元！疗程（2盒）下单再赠<汇仁六味地黄丸>1盒！治疗阳痿早泄搭配<固本回元口服液>效果好！","termianl":"all","reducePrice":0,"price":27200,"therapy":"2盒一疗程！治疗阳痿早泄搭配<固本回元口服液>效果更好（详情见推荐套餐1）！","type":"item","slogan":"下单减40，更享原价买6免1！"}
     */

    private ItemBean item;

    public static DemoBean objectFromData(String str) {

        return new Gson().fromJson(str, DemoBean.class);
    }

    public static DemoBean objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), DemoBean.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<DemoBean> arrayDemoBeanFromData(String str) {

        Type listType = new TypeToken<ArrayList<DemoBean>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<DemoBean> arrayDemoBeanFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType   = new TypeToken<ArrayList<DemoBean>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public ItemBean getItem() {
        return item;
    }

    public void setItem(ItemBean item) {
        this.item = item;
    }

    public static class ItemBean implements Serializable {
        private static final long serialVersionUID = 7784966246049617331L;
        /**
         * promotions : 【金秋滋补，拍下减40】更有原价买6免1，即立减322元！疗程（2盒）下单再赠<汇仁六味地黄丸>1盒！治疗阳痿早泄搭配<固本回元口服液>效果好！
         * termianl : all
         * reducePrice : 0
         * price : 27200
         * therapy : 2盒一疗程！治疗阳痿早泄搭配<固本回元口服液>效果更好（详情见推荐套餐1）！
         * type : item
         * slogan : 下单减40，更享原价买6免1！
         */

        private String promotions;
        private String termianl;
        private int    reducePrice;
        private int    price;
        private String therapy;
        private String type;
        private String slogan;

        public static ItemBean objectFromData(String str) {

            return new Gson().fromJson(str, ItemBean.class);
        }

        public static ItemBean objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), ItemBean.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<ItemBean> arrayItemBeanFromData(String str) {

            Type listType = new TypeToken<ArrayList<ItemBean>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<ItemBean> arrayItemBeanFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType   = new TypeToken<ArrayList<ItemBean>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        public String getPromotions() {
            return promotions;
        }

        public void setPromotions(String promotions) {
            this.promotions = promotions;
        }

        public String getTermianl() {
            return termianl;
        }

        public void setTermianl(String termianl) {
            this.termianl = termianl;
        }

        public int getReducePrice() {
            return reducePrice;
        }

        public void setReducePrice(int reducePrice) {
            this.reducePrice = reducePrice;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public String getTherapy() {
            return therapy;
        }

        public void setTherapy(String therapy) {
            this.therapy = therapy;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }
    }
}
