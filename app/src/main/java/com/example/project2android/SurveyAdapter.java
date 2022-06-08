package com.example.project2android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyViewHolder> {

    private ArrayList<String> survey_q;
    private ArrayList<Integer> survey_a;

    private Context context;

    public SurveyAdapter(ArrayList<String> survey_q,
                         Context context) {
        this.survey_q = survey_q;
        this.survey_a = new ArrayList<Integer>();
        for (int i = 0; i < survey_q.size(); i++) {
            survey_a.add(-1);
        }

        this.context = context;
    }

    @NonNull
    @Override
    public SurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        // Inflate the layout
        View photoView
                = inflater
                .inflate(R.layout.survey_line,
                        parent, false);

        SurveyViewHolder viewHolder
                = new SurveyViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        viewHolder.getSurvey_q().setText(survey_q.get(index));
        viewHolder.getAnswerGroup().findViewById(R.id.survey_yes).setId((index * 2 + 1));
        viewHolder.getAnswerGroup().findViewById(R.id.survey_no).setId((index * 2 + 2));
        viewHolder.getAnswerGroup().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int value = 0;
                if (i % 2 == 1) {
                    value = 1;
                }

                survey_a.set((int) Math.ceil(i / 2.0) - 1, value);
            }
        });
    }

    @Override
    public int getItemCount() {
        return survey_q.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public boolean surveyAnswered() {
        for (Integer i : survey_a) {
            if (i == -1) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<Boolean> getSurveyAnswers() {
        if (!surveyAnswered()) {
            return null;
        }

        ArrayList<Boolean> result = new ArrayList<Boolean>();
        int n = survey_a.size();
        for (int i = 0; i < n; i++) {
            result.add(survey_a.get(i) != 0);
        }

        return result;
    }
}
