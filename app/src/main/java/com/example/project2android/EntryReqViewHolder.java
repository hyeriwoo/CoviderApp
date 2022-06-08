package com.example.project2android;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2android.R;

public class EntryReqViewHolder extends RecyclerView.ViewHolder {

    private View view;
    private TextView entryreq_question;
    private ImageButton entryreq_fulfill;

    public EntryReqViewHolder(@NonNull View itemView) {
        super(itemView);

        entryreq_question = (TextView) itemView.findViewById(R.id.entryreq_q);
        entryreq_fulfill = (ImageButton) itemView.findViewById(R.id.entryreq_fulfill);
    }

    /*
    GETTERS
     */

    public View getView() {
        return view;
    }

    public TextView getEntryreq_question() {
        return entryreq_question;
    }

    public ImageButton getEntryreq_fulfill() {
        return entryreq_fulfill;
    }
}
