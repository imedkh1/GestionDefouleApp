package com.example.gestiondefouleapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {
    // invoke the suitable constructor of the ArrayAdapter class
    public HistoryAdapter(@NonNull Context context, ArrayList<HistoryItem> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.historyitem, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        HistoryItem currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        ImageView numbersImage = currentItemView.findViewById(R.id.smallprofileImg);
        assert currentNumberPosition != null;
        numbersImage.setImageResource(R.drawable.socialdistancered);

        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.HistoryInfoTV);
        textView1.setText(currentNumberPosition.getTime()+" , "+currentNumberPosition.getDate()
        );

        // then return the recyclable view
        return currentItemView;
    }
}
