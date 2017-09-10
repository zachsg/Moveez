package com.example.android.moveez;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.moveez.data.Movie;
import com.example.android.moveez.utilities.JsonUtils;
import com.example.android.moveez.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MovieGridActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieGridActivity.class.getSimpleName();

    // Movie grid display RecyclerView and adapter
    private RecyclerView mMovieGridRV;
    private MovieAdapter mAdapter;

    // How many columns the movie grid should have
    private final static int SPAN_COUNT = 2;

    // For showing error messages/states
    private TextView mErrorTv;

    // For showing when background operations are occurring, waiting for results
    private ProgressBar mLoadingPB;

    // For sorting the movies in the grid
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String LATEST_MOVIES = "now_playing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_grid);

        mMovieGridRV = (RecyclerView) findViewById(R.id.rv_movie_grid);

        GridLayoutManager layoutManager = new GridLayoutManager(this, SPAN_COUNT);
        mMovieGridRV.setLayoutManager(layoutManager);

        mAdapter = new MovieAdapter();
        mMovieGridRV.setAdapter(mAdapter);

        mErrorTv = (TextView) findViewById(R.id.tv_error);
        mLoadingPB = (ProgressBar) findViewById(R.id.pb_loading);

        loadMovies(null);
    }

    private void loadMovies(String sort) {
        if (sort == null) new MovieTask().execute();
        else new MovieTask().execute(sort);
    }

    /**
     * Show the grid of movies.
     */
    private void showMovies() {
        // If the error display is visible, hide it
        if (mErrorTv.getVisibility() == View.VISIBLE) {
            mErrorTv.setVisibility(View.INVISIBLE);
        }

        // If the loading bar is visible, hide it
        if (mLoadingPB.getVisibility() == View.VISIBLE) {
            mLoadingPB.setVisibility(View.INVISIBLE);
        }

        // Show the grid of movies
        mMovieGridRV.setVisibility(View.VISIBLE);
    }

    /**
     * Show the error message/state.
     */
    private void showError() {
        // If the grid of movies is visible, hide it
        if (mMovieGridRV.getVisibility() == View.VISIBLE) {
            mMovieGridRV.setVisibility(View.INVISIBLE);
        }

        // if the loading bar is visible, hide it
        if (mLoadingPB.getVisibility() == View.VISIBLE) {
            mLoadingPB.setVisibility(View.INVISIBLE);
        }

        // Show the error message
        mErrorTv.setVisibility(View.VISIBLE);
    }

    private class MovieTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Show loading indicator
            mLoadingPB.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            // Default sorting is by top rated movies
            String sort = "top_rated";

            // If an alternative sorting method was passed, us that instead
            if (strings.length != 0) sort = strings[0];

            URL url = NetworkUtils.buildUrl(sort);
            try {
                String moviesString = NetworkUtils.getResponseFromHttpUrl(url);
                Log.v(LOG_TAG, moviesString);
                return JsonUtils.getMoviesFromJson(moviesString);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            // Hide loading indicator
            mLoadingPB.setVisibility(View.INVISIBLE);

            if (movies == null) {
                showError();
            } else {
                mAdapter.setMovieData(movies);
                showMovies();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_grid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String sort;
        switch (item.getItemId()) {
            case R.id.action_sort_now_playing:
                sort = LATEST_MOVIES;
                break;
            case R.id.action_sort_popular:
                sort = POPULAR_MOVIES;
                break;
            case R.id.action_sort_top_rated:
                sort = TOP_RATED_MOVIES;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        mAdapter.setMovieData(null);
        loadMovies(sort);
        return true;
    }
}
