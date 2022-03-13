package com.example.journal;

public class Dish {
    private String name;
    private int mass;
    private int calories;
    private String time;

    public Dish(String name, int mass, int calories, String time) {
        this.name = name;
        this.mass = mass;
        this.calories = calories;
        this.time = time;
    }

    public String getName()
    {
        return name;
    }

    public int getMass() {
        return mass;
    }

    public int getCalories() {
        return calories;
    }

    public String getTime() {
        return time;
    }
}
