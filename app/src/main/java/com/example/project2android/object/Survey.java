package com.example.project2android.object;

import java.util.*;

public class Survey {
    private int user_id;
    private ArrayList<Boolean> answers;

    public Survey(ArrayList<Boolean> answers) {
        this.answers = answers;
    }

    public int getUserID() {return user_id;}
    public ArrayList<Boolean> getAnswers() {return this.answers;}

}
