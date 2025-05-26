package com.example.watchme.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchme.MovieDetailsActivity;
import com.example.watchme.R;
import com.example.watchme.models.Movie;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<Movie> bannerMovies;

    public BannerAdapter(Context context, List<Movie> bannerMovies) {
        this.context = context;
        this.bannerMovies = bannerMovies;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Movie movie = bannerMovies.get(position % bannerMovies.size());

        int resId = context.getResources().getIdentifier(movie.bannerName, "drawable", context.getPackageName());
        holder.ivBanner.setImageResource(resId != 0 ? resId : R.drawable.placeholder_banner);

        holder.ivBanner.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailsActivity.class);
            intent.putExtra("movie_id", movie.movieId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBannerImage);
        }
    }
}
