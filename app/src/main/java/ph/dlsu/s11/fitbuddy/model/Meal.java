package ph.dlsu.s11.fitbuddy.model;

import java.io.Serializable;

public class Meal implements Serializable {

    private String image;


    private String mealID;

    private String mealName;
    private String description;
    private String caloriesPerServing;
    private String fatsPerServing;
    private String carbsPerServing;
    private String proteinPerServing;
    private String mealChoice;
    private String Date;
    private String sodiumPerServing;

    public String getMealID() {
        return mealID;
    }

    public void setMealID(String mealID) {
        this.mealID = mealID;
    }


    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getSodiumPerServing() {
        return sodiumPerServing;
    }

    public void setSodiumPerServing(String sodiumPerServing) {
        this.sodiumPerServing = sodiumPerServing;
    }



    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getMealChoice() {
        return mealChoice;
    }

    public void setMealChoice(String mealChoice) {
        this.mealChoice = mealChoice;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaloriesPerServing() {
        return caloriesPerServing;
    }

    public void setCaloriesPerServing(String caloriesPerServing) {
        this.caloriesPerServing = caloriesPerServing;
    }

    public String getFatsPerServing() {
        return fatsPerServing;
    }

    public void setFatsPerServing(String fatsPerServing) {
        this.fatsPerServing = fatsPerServing;
    }

    public String getCarbsPerServing() {
        return carbsPerServing;
    }

    public void setCarbsPerServing(String carbsPerServing) {
        this.carbsPerServing = carbsPerServing;
    }

    public String getProteinPerServing() {
        return proteinPerServing;
    }

    public void setProteinPerServing(String proteinPerServing) {
        this.proteinPerServing = proteinPerServing;
    }

    public String getSugarPerServing() {
        return sugarPerServing;
    }

    public void setSugarPerServing(String sugarPerServing) {
        this.sugarPerServing = sugarPerServing;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    private String sugarPerServing;
    private String servings;
}
