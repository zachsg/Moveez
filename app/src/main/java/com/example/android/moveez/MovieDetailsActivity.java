package com.example.android.moveez;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.moveez.data.Movie;
import com.example.android.moveez.utilities.JsonUtils;
import com.example.android.moveez.utilities.NetworkUtils;

import org.w3c.dom.Text;

import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    private Integer mMovieID;

    private TextView mTitleTV;
    private ImageView mPosterIV;
    private TextView mReleaseDateTV;
    private TextView mRuntimeTV;
    private TextView mRatingTV;
    private TextView mDescriptionTV;

    private ProgressBar mLoadingPB;
    private ScrollView mDetailsSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mTitleTV = (TextView) findViewById(R.id.tv_details_title);
        mPosterIV = (ImageView) findViewById(R.id.iv_details_poster);
        mReleaseDateTV = (TextView) findViewById(R.id.tv_details_releasedate);
        mRuntimeTV = (TextView) findViewById(R.id.tv_details_runtime);
        mRatingTV = (TextView) findViewById(R.id.tv_details_rating);
        mDescriptionTV = (TextView) findViewById(R.id.tv_details_description);

        mLoadingPB = (ProgressBar) findViewById(R.id.pb_loading);
        mDetailsSV = (ScrollView) findViewById(R.id.sv_details);

        Intent movieIntent = this.getIntent();
        Bundle bundle = movieIntent.getExtras();

        if (!bundle.isEmpty()) {
            Movie movie = (Movie) bundle.getSerializable("movie");
            try {
                mMovieID = movie.getId();
                new MovieDetailsActivity.MovieTask().execute(mMovieID.toString());
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        } else {
            // TODO: Show error state (failed to pass in movie clicked)
        }
    }

    private class MovieTask extends AsyncTask<String, Void, Movie> {

        @Override
        protected void onPreExecute() {
            mDetailsSV.setVisibility(View.INVISIBLE);
            mLoadingPB.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Movie doInBackground(String... strings) {
            // Pass in the ID of the movie we're getting details for
            URL url = NetworkUtils.buildUrl(mMovieID.toString());
            try {
                String moviesString = NetworkUtils.getResponseFromHttpUrl(url);
                Log.v(LOG_TAG, moviesString);
                return JsonUtils.getMovieDetails(moviesString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            mTitleTV.setText(movie.getTitle());
            mReleaseDateTV.setText(movie.getReleaseDate().substring(0, 4));
            mRuntimeTV.setText(formatRuntime(movie.getRuntime()));
            mRatingTV.setText(formatRating(movie.getVoteCount(), movie.getVoteAverage()));
            mDescriptionTV.setText(movie.getOverview());

            // Load the movie poster into the ImageView
            NetworkUtils.loadImage(MovieDetailsActivity.this, mPosterIV, movie);

            mLoadingPB.setVisibility(View.INVISIBLE);
            mDetailsSV.setVisibility(View.VISIBLE);
        }

        private String formatRuntime(int runtime) {
            int hours = runtime / 60;
            int minutes = runtime - (hours*60);
            if (hours > 0) return String.format(Locale.ENGLISH, "%dhr, %dmin", hours, minutes);
            else return String.format(Locale.ENGLISH, "%dmin", minutes);
        }

        private String formatRating(int ratingCount, double ratingAverage) {
            return String.format(Locale.ENGLISH, "%.1f/10 (%d ratings)", ratingAverage, ratingCount);
        }
    }
}
