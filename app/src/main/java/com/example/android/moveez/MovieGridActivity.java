package com.example.android.moveez;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class MovieGridActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String LOG_TAG = MovieGridActivity.class.getSimpleName();

    private static final int LOADER_ID = 1;

    private static final String SORT_METHOD_KEY = "sort_key";
    // How many columns the movie grid should have
    private final static int SPAN_COUNT = 2;
    // For sorting the movies in the grid
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String LATEST_MOVIES = "now_playing";
    // Movie grid display RecyclerView and adapter
    private RecyclerView mMovieGridRV;
    private MovieAdapter mAdapter;
    // For showing error messages/states
    private TextView mErrorTv;
    // For showing when background operations are occurring, waiting for results
    private ProgressBar mLoadingPB;

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

        loadMovies(null, savedInstanceState);
    }

    private void loadMovies(String sort, Bundle savedInstanceState) {
        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.getLoader(LOADER_ID);
        if (loaderManager == null) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            Bundle bundle = new Bundle();
            if (savedInstanceState != null) sort = savedInstanceState.getString(SORT_METHOD_KEY);
            bundle.putString(SORT_METHOD_KEY, sort);
            loaderManager.restartLoader(LOADER_ID, bundle, this);
        }
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

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                mMovieGridRV.setVisibility(View.INVISIBLE);
                mLoadingPB.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public List<Movie> loadInBackground() {
                // Read the sorting method passed in
                String sort = null;
                if (args != null) sort = args.getString(SORT_METHOD_KEY);
                if (sort == null) sort = "top_rated";

                URL url = NetworkUtils.buildUrl(sort);
                try {
                    String moviesString = NetworkUtils.getResponseFromHttpUrl(url);
                    return JsonUtils.getMoviesFromJson(moviesString);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        if (data != null && !data.isEmpty()) {
            mAdapter.setMovieData(data);
            showMovies();
        } else {
            showError();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
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
        loadMovies(sort, null);
        return true;
    }
}
