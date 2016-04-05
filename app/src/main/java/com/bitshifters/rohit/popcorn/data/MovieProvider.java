package com.bitshifters.rohit.popcorn.data;

import android.net.Uri;

import de.triplet.simpleprovider.AbstractProvider;
import de.triplet.simpleprovider.Column;
import de.triplet.simpleprovider.Table;

/**
 * Created by rohit on 3/4/16.
 */
public class MovieProvider extends AbstractProvider {

    public static final String AUTHORITY = "com.bitshifters.rohit.popcorn.data.MovieProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int SCHEMA_VERSION = 2;

    public interface Path{
        String MOVIES = "movies";
        String VIDEOS = "videos";
        String REVIEWS = "reviews";
    }

    public static final Uri MOVIES_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.MOVIES).build();
    public static final Uri VIDEOS_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.VIDEOS).build();
    public static final Uri REVIEWS_URI = BASE_CONTENT_URI.buildUpon().appendPath(Path.REVIEWS).build();

    @Override
    protected String getAuthority() {
        return AUTHORITY;
    }

    @Override
    protected int getSchemaVersion() {
        return SCHEMA_VERSION;
    }

    @Table
    public class Review{
        @Column(Column.FieldType.INTEGER)
        public static final String MOVIE_ID = "movie_id";
        @Column(Column.FieldType.TEXT)
        public static final String ID = "_id";
        @Column(Column.FieldType.TEXT)
        public static final String AUTHOR = "_author";
        @Column(Column.FieldType.TEXT)
        public static final String CONTENT = "content";
        @Column(Column.FieldType.TEXT)
        public static final String URL = "url";
    }

    @Table
    public class Video{
        @Column(Column.FieldType.INTEGER)
        public static final String MOVIE_ID = "movie_id";
        @Column(Column.FieldType.TEXT)
        public static final String ID = "_id";
        @Column(Column.FieldType.TEXT)
        public static final String ISO6391 =  "iso6391";
        @Column(Column.FieldType.TEXT)
        public static final String ISO31661 = "iso31661";
        @Column(Column.FieldType.TEXT)
        public static final String KEY = "key";
        @Column(Column.FieldType.TEXT)
        public static final String NAME = "name";
        @Column(Column.FieldType.TEXT)
        public static final String SITE = "site";
        @Column(Column.FieldType.INTEGER)
        public static final  String SIZE = "size";
        @Column(Column.FieldType.TEXT)
        public static final String TYPE = "type";
    }

    @Table
    public class Movie {

        @Column(Column.FieldType.INTEGER)
        public static final String ID = "_id";

        @Column(Column.FieldType.TEXT)
        public static final String POSTER_PATH = "poster_path";

        @Column(Column.FieldType.TEXT)
        public static final String OVERVIEW = "overview";

        @Column(Column.FieldType.TEXT)
        public static final String RELEASE_DATE = "release_date";

        @Column(Column.FieldType.TEXT)
        public static final String TITLE = "title";

        @Column(Column.FieldType.TEXT)
        public static final String BACKDROP = "backdrop";

        @Column(Column.FieldType.REAL)
        public static final String POPULARITY = "popularity";

        @Column(Column.FieldType.INTEGER)
        public static final String VOTE_COUNT = "vote_count";

        @Column(Column.FieldType.REAL)
        public static final String VOTE_AVERAGE = "vote_average";

    }

}
