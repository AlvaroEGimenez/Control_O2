package com.example.controlo2;

import java.io.Serializable;

public class Tank implements Serializable {

    private int number;
    private int pressure;

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
}
