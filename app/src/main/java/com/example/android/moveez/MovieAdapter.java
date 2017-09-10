package com.example.android.moveez;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moveez.data.Movie;
import com.example.android.moveez.utilities.NetworkUtils;

import java.util.List;

/**
 * Adapter for the MovieGrid Activity's RecyclerView.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<Movie> mMovieData;

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForMovie = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForMovie, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);
        Context context = holder.mMoviePosterIV.getContext();
        NetworkUtils.loadImage(context, holder.mMoviePosterIV, movie);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.size();
    }

    public void setMovieData(List<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView mMoviePosterIV;

        MovieAdapterViewHolder(View view) {
            super(view);
            mMoviePosterIV = view.findViewById(R.id.iv_movie_poster);

            // Handle clicks of each movie in the grid
            mMoviePosterIV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();

            int position = getAdapterPosition();
            Movie currentMovie = mMovieData.get(position);

            // Package the movie for transit to MovieDetailsActivity
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie", currentMovie);

            // Create Intent and pass in the bundle containing the clicked movie
            Intent detailsIntent = new Intent(view.getContext(), MovieDetailsActivity.class);
            detailsIntent.putExtras(bundle);

            if (detailsIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(detailsIntent);
            }
        }
    }
}
