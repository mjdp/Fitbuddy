package ph.dlsu.s11.fitbuddy.model;

import java.io.Serializable;

public class User implements Serializable {

    public String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String[] getMeals() {
        return meals;
    }

    public void setMeals(String[] meals) {
        this.meals = meals;
    }

    public String emailAddress;
    public String phoneNumber;
    public String height;
    public String weight;
    public String[] meals;




}
