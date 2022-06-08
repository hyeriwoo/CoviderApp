package com.example.project2android;

import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SurveyViewHolder extends RecyclerView.ViewHolder {

    private TextView survey_q;
    private RadioGroup answerGroup;

    public SurveyViewHolder(@NonNull View itemView) {
        super(itemView);

        survey_q = (TextView) itemView.findViewById(R.id.survey_q);
        answerGroup = (RadioGroup) itemView.findViewById(R.id.survey_answerGroup);
    }

    /*
    GETTERS
     */
    public TextView getSurvey_q() {
        return survey_q;
    }

    public RadioGroup getAnswerGroup() {
        return answerGroup;
    }
}
