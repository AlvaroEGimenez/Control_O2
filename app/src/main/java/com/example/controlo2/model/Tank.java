package com.example.controlo2.model;

import java.io.Serializable;

public class Tank implements Serializable {

    private int number;
    private int pressure;
    private boolean onRecharge;
    private String owner;
    private String dueDate;
    private String provider;
    private int capacity;
    private String observations;

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}
