package com.example.project2android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EntryReqAdapter extends RecyclerView.Adapter<EntryReqViewHolder> {

    private ArrayList<String> entryReqs;
    private ArrayList<String> fulfills;
    private EntryReqFragment entryReqFragment;

    Context context;

    public EntryReqAdapter(ArrayList<String> entryReqs, ArrayList<String> fulfills,
                           Context context, EntryReqFragment entryReqFragment) {
        this.entryReqs = entryReqs;
        this.fulfills = fulfills;

        this.context = context;
        this.entryReqFragment = entryReqFragment;
    }

    @NonNull
    @Override
    public EntryReqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the layout
        View photoView = inflater.inflate(R.layout.entryreq_line, parent, false);

        EntryReqViewHolder viewHolder = new EntryReqViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryReqViewHolder viewHolder, int position) {
        final int index = viewHolder.getAdapterPosition();

        System.out.println("Index: " + index);
        viewHolder.getEntryreq_question().setText(entryReqs.get(index));
        viewHolder.getEntryreq_fulfill().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("To fulfill the requirement:");
                alertDialogBuilder.setMessage(fulfills.get(index));
                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return entryReqs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
