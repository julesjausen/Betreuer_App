package com.example.myapplication.models;

public class Arbeit {

    private String arbeitUid;
    private String beschreibung;
    private String betreuerUid;
    private String nameDerArbeit;
    private String rechnungsstatusBetreuer;
    private String rechnungsstatusZweitgutachter;
    private String zweitgutachterName;
    private String zweitgutachterUid;



    private String studienfach;
    private String zustand;

    // Standardkonstruktor für Firebase
    public Arbeit() {}

    // Getter und Setter
    public String getArbeitUid() { return arbeitUid; }
    public void setArbeitUid(String arbeitUid) { this.arbeitUid = arbeitUid; }
    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }

    public String getZweitgutachterName() {return zweitgutachterName;}

    public void setZweitgutachterName(String zweitgutachterName) {this.zweitgutachterName = zweitgutachterName;}

    public String getBetreuerUid() { return betreuerUid; }
    public void setBetreuerUid(String betreuerUid) { this.betreuerUid = betreuerUid; }

    public String getNameDerArbeit() { return nameDerArbeit; }
    public void setNameDerArbeit(String nameDerArbeit) { this.nameDerArbeit = nameDerArbeit; }

    public String getRechnungsstatusBetreuer() { return rechnungsstatusBetreuer; }
    public void setRechnungsstatusBetreuer(String rechnungsstatusBetreuer) { this.rechnungsstatusBetreuer = rechnungsstatusBetreuer; }

    public String getRechnungsstatusZweitgutachter() { return rechnungsstatusZweitgutachter; }
    public void setRechnungsstatusZweitgutachter(String rechnungsstatusZweitgutachter) { this.rechnungsstatusZweitgutachter = rechnungsstatusZweitgutachter; }

    public String getStudienfach() { return studienfach; }
    public void setStudienfach(String studienfach) { this.studienfach = studienfach; }

    public String getZustand() { return zustand; }
    public void setZustand(String zustand) { this.zustand = zustand; }

    public String getZweitgutachterUid() {return zweitgutachterUid;}

    public void setZweitgutachterUid(String zweitgutachterUid) {this.zweitgutachterUid = zweitgutachterUid;}
}

