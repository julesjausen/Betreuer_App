package com.example.myapplication.models;

public class Betreuer {
    private String name;



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String beschreibung;

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    private String betreuerUid;

    public String getBetreuerUid() {
        return betreuerUid;
    }

    public void setBetreuerUid(String betreuerUid) {
        this.betreuerUid = betreuerUid;
    }
}
