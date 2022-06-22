package com.example.gestiondefouleapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PlacesAdapter extends ArrayAdapter<PlacesItem> {
    // invoke the suitable constructor of the ArrayAdapter class
    public PlacesAdapter(@NonNull Context context, ArrayList<PlacesItem> arrayList) {

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
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.placeitem, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        PlacesItem currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        ImageView numbersImage = currentItemView.findViewById(R.id.smallPlaceImg);
        assert currentNumberPosition != null;


        // then according to the position of the view assign the desired TextView 1 for the same
        TextView textView1 = currentItemView.findViewById(R.id.PlaceenameTV);
        textView1.setText(currentNumberPosition.getName());
        TextView textView2 = currentItemView.findViewById(R.id.PlaceStateTV);
        if (currentNumberPosition.getState()==-1){
            textView2.setText("attention cet endroit est surpeuplé");
            textView2.setTextColor(Color.parseColor("#DE1D1D"));
            numbersImage.setImageResource(R.drawable.ic_store_crowded);

        }else if (currentNumberPosition.getState()==0){
            textView2.setText("cet endroit est assez surpeuplé");
            textView2.setTextColor(Color.parseColor("#D9B62A"));
            numbersImage.setImageResource(R.drawable.ic_store_medium);

        }else{
            textView2.setText("cet endroit est presque vide");
            textView2.setTextColor(Color.parseColor("#42CA70"));
            numbersImage.setImageResource(R.drawable.ic_store);

        }


        // then return the recyclable view
        return currentItemView;
    }

}