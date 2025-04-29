package com.example.myapplication.Model;

public class Spectacle_Detail {
    private Long id;

    private String titre;
    private String description;

    private String secondaryImage;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return titre;
    }
    public String getDescription() {
        return description;
    }

    public void setTitle(String titre) {
        this.titre = titre;
    }

    public String getSecondaryImage() {
        return secondaryImage;
    }

    public void setSecondaryImage(String coverImage) {
        this.secondaryImage = coverImage;
    }
}
