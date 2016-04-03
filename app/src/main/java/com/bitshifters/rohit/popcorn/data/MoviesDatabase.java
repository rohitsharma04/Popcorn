package com.bitshifters.rohit.popcorn.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by rohit on 3/4/16.
 */
@Database(version = MoviesDatabase.VERSION)
public class MoviesDatabase {

    private MoviesDatabase(){}

    public static final int VERSION = 1;

    @Table(MovieColumns.class)
    public static final String MOVIES = "movies";

}
