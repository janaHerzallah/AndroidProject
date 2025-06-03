package com.example.navproject.ui.Reservations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navproject.R;
import com.example.navproject.ui.PropertiesMenu.Property;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private final List<ReservationItem> reservations;
    private final Context context;

    public ReservationAdapter(List<ReservationItem> reservations, Context context) {
        this.reservations = reservations;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button btnFavorite, btnReserve;

        TextView title, price, location, description, timestamp;
        ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.propertyTitle);
            price = itemView.findViewById(R.id.propertyPrice);
            location = itemView.findViewById(R.id.propertyLocation);
            description = itemView.findViewById(R.id.propertyDescription);
            timestamp = itemView.findViewById(R.id.propertyExtraInfo);  // Add this TextView to XML
            thumbnail = itemView.findViewById(R.id.propertyImage);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReservationItem r = reservations.get(position);
        Property p = r.property;

        holder.title.setText(p.title);
        holder.price.setText("$" + p.price);
        holder.location.setText(p.location);
        holder.description.setText(p.description);
        holder.timestamp.setText("Reserved on: " + r.timestamp);
        Picasso.get().load(p.imageUrl).into(holder.thumbnail);

        holder.btnFavorite.setVisibility(View.GONE);
        holder.btnReserve.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
}
