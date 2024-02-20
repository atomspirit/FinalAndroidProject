package com.example.finalproject.Domains;

public class Game {
    /**
     * This class will be used to store the data of a game
     *
     * @param title - the title of the game
     * @param description - the description of the game
     * @param image - type int, the image of the game in the resource
     *
     */
    private String title;
    private String description;
    private int image; // Drawable resource ID

    public Game(String title, String description, int image) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(int image) {
        this.image = image;
    }

}
