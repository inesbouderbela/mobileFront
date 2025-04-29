package com.example.myapplication.Model;

import java.time.LocalDate;
import java.util.List;

import java.util.List;

public class Seance {
    private Long idSpectacle;
    private String title; // <-- title au lieu de titre
    private String secondaryImage;
    private String description;
    private String motsCles;
    private String dateSeance;
    private String heureDebut;
    private String heureFin;
    private String nomSalle;
    private String nomLieu;
    private String adresseLieu;
    private Double latitude;
    private Double longitude;
    private List<Acteur> acteurs;

    public Long getIdSpectacle() { return idSpectacle; }
    public String getTitle() { return title; } // <-- title
    public String getSecondaryImage() { return secondaryImage; }
    public String getDescription() { return description; }
    public String getMotsCles() { return motsCles; }
    public String getDateSeance() { return dateSeance; }
    public String getHeureDebut() { return heureDebut; }
    public String getHeureFin() { return heureFin; }
    public String getNomSalle() { return nomSalle; }
    public String getNomLieu() { return nomLieu; }
    public String getAdresseLieu() { return adresseLieu; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public List<Acteur> getActeurs() { return acteurs; }
}




