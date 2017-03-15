package com.mouzhai.excelreader.model;

/**
 * 密码表实体类
 * <p>
 * Created by Mouzhai on 2017/2/21.
 */

public class Password {

    String sn;
    int pass;
    String mac;
    String pno;
    String encryption;
    String date;
    int description;
    int key;

    public Password(String sn, int pass, String mac, String pno, String encryption, String date, int description, int key) {
        this.sn = sn;
        this.pass = pass;
        this.mac = mac;
        this.pno = pno;
        this.encryption = encryption;
        this.date = date;
        this.description = description;
        this.key = key;
    }

    @Override
    public String toString() {
        return "Password{" +
                "sn='" + sn + '\'' +
                ", pass=" + pass +
                ", mac='" + mac + '\'' +
                ", pno='" + pno + '\'' +
                ", encryption='" + encryption + '\'' +
                ", date=" + date +
                ", description=" + description +
                ", key=" + key +
                '}';
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getPass() {
        return pass;
    }

    public void setPass(int pass) {
        this.pass = pass;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
