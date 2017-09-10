package com.example.android.moveez.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;

import com.example.android.moveez.R;
import com.example.android.moveez.data.Movie;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utilities for communicating with themoviedb.org
 */

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    // TODO: Be sure to add your API_KEY here
    private static final String API_KEY_BASE = "";
    private static final String API_KEY_QUERY = "?api_key=";
    private static final String API_KEY = API_KEY_QUERY + API_KEY_BASE;

    // Set language preference in query
    private static final String LANGUAGE = "&language=en-US&page=1";

    // Based URL strings, Part 1 (A) & Part 2 (B)
    private static final String MOVIES_BASE_URL_A = "https://api.themoviedb.org/3/movie/";
    private static final String MOVIES_BASE_URL_B = API_KEY + LANGUAGE;

    // Sorting methods
    private static final String POPULAR_MOVIES = "popular";
    private static final String TOP_RATED_MOVIES = "top_rated";
    private static final String LATEST_MOVIES = "now_playing";

    public static URL buildUrl(String sort) {
        String urlString = "";

        if (sort == null) {
            urlString = MOVIES_BASE_URL_A + LATEST_MOVIES + MOVIES_BASE_URL_B;
        } else if (sort.equals(POPULAR_MOVIES) || sort.equals(TOP_RATED_MOVIES)) {
            switch (sort) {
                case POPULAR_MOVIES:
                    urlString = MOVIES_BASE_URL_A + POPULAR_MOVIES + MOVIES_BASE_URL_B;
                    break;
                case TOP_RATED_MOVIES:
                    urlString = MOVIES_BASE_URL_A + TOP_RATED_MOVIES + MOVIES_BASE_URL_B;
                    break;
                default:
                    urlString = MOVIES_BASE_URL_A + LATEST_MOVIES + MOVIES_BASE_URL_B;
            }
        } else {
            // "sort" == a specific movie ID in this case.
            urlString = MOVIES_BASE_URL_A + sort + MOVIES_BASE_URL_B;
        }

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Check on whether device is connected to the network.
     * @return true if connected, false otherwise.
     */
    public static boolean isNetworkUp(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * Loads the correct movie cover photo into the proper imageView.
     * @param context Context of calling activity (to pull necessary Strings).
     * @param imageView The ImageView into which the photo should be loaded.
     * @param movie The movie from which the photo should be pulled.
     */
    public static void loadImage(Context context, ImageView imageView, Movie movie) {
        String url = getPosterPath(context, movie);

        // Set proper image for given movie
        Picasso.with(context).load(url).into(imageView);
    }

    /**
     * Helper method for constructing the full path (URL) based on given movie's partial path.
     * @param context The context for the caller (to pull in necessary Strings).
     * @param movie The specific movie for which to get the cover photo URL.
     * @return The fully constructed URL for the given movie's cover photo.
     */
    private static String getPosterPath(Context context, Movie movie) {
        String baseUrl = context.getString(R.string.base_url);
        String imageSize = context.getString(R.string.image_size_url);
        String imageUrl = movie.getPosterPath();

        return baseUrl + imageSize + imageUrl;
    }
}
