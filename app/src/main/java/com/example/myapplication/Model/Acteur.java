package com.example.myapplication.Model;

import java.io.Serializable;

public class Acteur   implements Serializable {

    private Long id;
    private String nom;
    private String prenom;
    private String photo;

    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getPhoto() { return photo; }
}

