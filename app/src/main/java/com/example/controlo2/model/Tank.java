package com.example.controlo2.model;

import java.io.Serializable;

public class Tank implements Serializable {

    private int number;
    private int pressure;
    private boolean onRecharge;

    public Tank() {
    }

    public Tank(int number, int pressure) {
        this.number = number;
        this.pressure = pressure;
    }

   public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isOnRecharge() {
        return onRecharge;
    }

    public void setOnRecharge(boolean onRecharge) {
        this.onRecharge = onRecharge;
    }
}
