package com.example.myapplication;

public class Arbeit {
    private String betreuerUid;
    private String nameDerArbeit;
    private String zustand;

    // Standardkonstruktor für Firebase
    public Arbeit() {}

    // Getter und Setter
    public String getBetreuerUid() { return betreuerUid; }
    public void setBetreuerUid(String betreuerUid) { this.betreuerUid = betreuerUid; }
    public String getNameDerArbeit() { return nameDerArbeit; }
    public void setNameDerArbeit(String nameDerArbeit) { this.nameDerArbeit = nameDerArbeit; }
    public String getZustand() { return zustand; }
    public void setZustand(String zustand) { this.zustand = zustand; }
}