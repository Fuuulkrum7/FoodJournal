package com.example.journal;

public class Dish {
    private String user_id;
    private String local_id;
    private String dish;
    private String mass;
    private String calories;
    private String eating;
    private String date;
    private String time;

    Dish(String user_id, String local_id, String dish, String mass, String calories, String eating, String date, String time) {
        this.user_id = user_id;
        this.local_id = local_id;
        this.dish = dish;
        this.mass = mass;
        this.calories = calories;
        this.eating = eating;
        this.date = date;
        this.time = time;
    }
}
