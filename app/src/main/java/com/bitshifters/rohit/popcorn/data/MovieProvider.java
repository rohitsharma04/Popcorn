package com.bitshifters.rohit.popcorn.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by rohit on 3/4/16.
 */
@ContentProvider(authority = MovieProvider.AUTHORITY, database = MoviesDatabase.class)
public class MovieProvider {
    public static final String AUTHORITY = "com.bitshifters.rohit.popcorn.data.MovieProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String [] COLUMNS = {"_id","poster_path","overview",
            "release_date","title","backdrop","popularity","vote_count","vote_average"};

    public static final int ID_ID = 0, POSTER_PATH_ID = 1, OVERVIEW_ID = 2, RELEASE_DATE_ID = 3
            ,TITLE_ID = 4, BACKDROP_ID = 5, POPULARITY_ID = 6, VOTE_COUNT_ID = 7, VOTE_AVERAGE_ID = 8;

    interface Path{
        String MOVIES = "movies";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MoviesDatabase.MOVIES) public static class Movies{
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie",
                defaultSort = MovieColumns.VOTE_AVERAGE + " DESC")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id){
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }
}
