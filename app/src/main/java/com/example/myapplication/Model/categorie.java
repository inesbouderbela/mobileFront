package com.example.myapplication.Model;

public class categorie {
    String nom;
    int image;

    public categorie(String nom, int image) {
        this.nom = nom;
        this.image = image;
    }

    public String getNom() { return nom; }

    public int getImage() { return image; }
}
