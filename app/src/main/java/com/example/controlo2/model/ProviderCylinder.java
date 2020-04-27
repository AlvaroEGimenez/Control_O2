package com.example.controlo2.model;

import java.io.Serializable;

public class ProviderCylinder implements Serializable {
    private String mail;
    private String name;

    public ProviderCylinder() {
    }

    public ProviderCylinder(String mail, String name) {
        this.mail = mail;
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
