package com.example.android.moveez.utilities;

import android.util.Log;

import com.example.android.moveez.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to help with handling JSON received from themoviedb.org API.
 */

public class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    /**
     * Take a string representation of movies and return a list of individual Movie objects.
     * @param moviesString The movies retrieved from the server in String format.
     * @return The parsed list of Movie objects from passed moviesString.
     * @throws JSONException In case conversion of moviesString to moviesJsonFull fails.
     */
    public static List<Movie> getMoviesFromJson(String moviesString) throws JSONException {

        // Base level JSON array of all movies returned by query from themoviedb.org API.
        final String RESULTS = "results";

        List<Movie> movies = new ArrayList<>();

        JSONObject moviesJsonFull = new JSONObject(moviesString);
        if (moviesJsonFull.has(RESULTS)) {
            // Parse out the array of movies from the full request
            JSONArray moviesJsonArray = moviesJsonFull.getJSONArray(RESULTS);

            /* Parse out each Movie from the JSON array and add it to list of Movie objects
             * to be returned.
             */
            for (int i = 0; i < moviesJsonArray.length(); i++) {
                // Get a single movie
                JSONObject jsonMovie = moviesJsonArray.getJSONObject(i);

                // Parse out the details we care about for the grid for the given movie
                int id = checkAndSetInt(jsonMovie, "id");
                String posterPath = setValueForKey(jsonMovie, "poster_path").toString();

                // Construct new Movie object with the parsed info
                Movie movie = new Movie(id, posterPath);

                movies.add(i, movie);
            }
        } else {
            // Something went wrong with parsing the String of JSON data.  Can't do anything
            Log.e("JsonUtils", "No valid movies found in JSON :(");
        }
        return movies;
    }

    /**
     * Check if given movie has a given int value for key and if so whether it's null
     * @param jsonObject Movie to check
     * @param key The key in the movie JSON object to check for
     * @return The int value for the key passed in.
     */
    private static int checkAndSetInt(JSONObject jsonObject, String key) {
        int intValue = 0;
        if (jsonObject.has(key)) {
            String stringValue = setValueForKey(jsonObject, key).toString();
            if (stringValue != null) {
                intValue = Integer.parseInt(stringValue);
            }
        }
        return intValue;
    }

    /**
     * Check if given movie has a given double value for key and if so whether it's null
     * @param jsonObject Movie to check
     * @param key The key in the movie JSON object to check for
     * @return The double value for the key passed in.
     */
    private static double checkAndSetDouble(JSONObject jsonObject, String key) {
        double doubleValue = 0;
        if (jsonObject.has(key)) {
            String stringValue = setValueForKey(jsonObject, key).toString();
            if (stringValue != null) {
                doubleValue = Double.parseDouble(stringValue);
            }
        }
        return doubleValue;
    }

    /**
     * Check if given movie has a given boolean value for key and if so whether it's null
     * @param jsonObject Movie to check
     * @param key The key in the movie JSON object to check for
     * @return The boolean value for the key passed in.
     */
    private static boolean checkAndSetBoolean(JSONObject jsonObject, String key) {
        boolean booleanValue = false;
        if (jsonObject.has(key)) {
            String stringValue = setValueForKey(jsonObject, key).toString();
            if (stringValue != null) {
                booleanValue = Boolean.parseBoolean(stringValue);
            }
        }
        return booleanValue;
    }

    /**
     * Take in movie JSON object and return the value for the key.
     * @param movie JSON object containing movie details.
     * @param key The key to be looked up in the movie JSON object.
     * @return The value for the key in the given JSON movie object.
     */
    private static Object setValueForKey(JSONObject movie, String key) {
        if (movie.has(key)) {
            try {
                // Cases are unique when key is average rating since they're Doubles vs Strings.
                if (key.equals("vote_average")) {
                    return movie.getDouble(key);
                } else if (key.equals("runtime")) {
                    return movie.getInt(key);
                } else {
                    return movie.getString(key);
                }
            } catch (JSONException jse) {
                jse.printStackTrace();
                if (key.equals("vote_average")) {
                    return 0.0;
                } else {
                    return "n/a";
                }
            }
        } else {
            if (key.equals("vote_average")) {
                return 0.0;
            } else {
                return "n/a";
            }
        }
    }

    /**
     * Parse out detailed info from a single Movie object.
     * @param movieString The movie from which to parse details.
     * @return The Movie object constructed from the parsed details.
     * @throws JSONException In the event there is an error converting
     *                       the passed String to a JSONObject.
     */
    public static Movie getMovieDetails(String movieString) throws JSONException {
        // Get a single movie
        JSONObject jsonMovie = new JSONObject(movieString);

        // Parse out the details for the given movie
        String title = setValueForKey(jsonMovie, "title").toString();
        String status = setValueForKey(jsonMovie, "status").toString();
        String releaseDate = setValueForKey(jsonMovie, "release_date").toString();
        String homePage = setValueForKey(jsonMovie, "homepage").toString();
        String posterPath = setValueForKey(jsonMovie, "poster_path").toString();
        String overview = setValueForKey(jsonMovie, "overview").toString();
        int id = checkAndSetInt(jsonMovie, "id");
        int budget = checkAndSetInt(jsonMovie, "budget");
        int revenue = checkAndSetInt(jsonMovie, "revenue");
        int voteCount = checkAndSetInt(jsonMovie, "vote_count");
        double voteAverage = checkAndSetDouble(jsonMovie, "vote_average");
        int runtime = checkAndSetInt(jsonMovie, "runtime");
        boolean adult = checkAndSetBoolean(jsonMovie, "adult");

        // Construct new Movie object with the parsed info
        return new Movie(title, status, releaseDate, homePage, posterPath,
                overview, id, budget, revenue, voteCount, voteAverage, runtime, adult);
    }
}
