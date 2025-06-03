package com.example.navproject.ui.PropertiesMenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navproject.R;
import com.example.navproject.ui.UserDataBaseHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private List<Property> propertyList;
    private Context context;

    private int userId;

    public PropertyAdapter(List<Property> properties, Context context, int userId) {
        this.propertyList = properties;
        this.context = context;
        this.userId = userId;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property p = propertyList.get(position);
        holder.title.setText(p.title);
        holder.price.setText("$" + p.price);
        holder.location.setText(p.location);
        holder.description.setText(p.description);

        // Load image using Picasso or Glide
        Picasso.get().load(p.imageUrl).into(holder.thumbnail);

        holder.btnFavorite.setOnClickListener(v ->
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show());

        holder.btnReserve.setOnClickListener(v ->
                Toast.makeText(context, "Reserve clicked for " + p.title, Toast.LENGTH_SHORT).show());

        holder.btnFavorite.setOnClickListener(v -> {
            UserDataBaseHelper dbHelper = new UserDataBaseHelper(context);
            dbHelper.insertFavorite(userId, p.id);  // p.id must match your property_id
            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
            dbHelper.logFavoritesTable();
        });



    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, location, description;
        ImageView thumbnail;
        Button btnFavorite, btnReserve;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.propertyTitle);
            price = itemView.findViewById(R.id.propertyPrice);
            location = itemView.findViewById(R.id.propertyLocation);
            description = itemView.findViewById(R.id.propertyDescription);
            thumbnail = itemView.findViewById(R.id.propertyImage);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }
}
