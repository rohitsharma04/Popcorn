#Popcorn 
###Popular Movies App
This is an android app which I made from scratch for Android Nanodegree Developer course. This app reveals the power of adaptive UI both for phone and tablet devices. This app also has additional features which were not the requirement for Nanodegree program. 

##Features
* Search Movies
* Sort Movies Based on
  * Popularity
  * Rating
  * Currently Playing
  * Upcoming
* Add Movies to Favorite List
    * Favorite Movies are stored locally
    * Accessible even when offline.
* Watch Trailers and other video clips
* Read reviews of the movies
* Share movie overviews & trailers

##Screenshots

###Mobile

![screen](/screenshots/device-2016-04-06-001239.png) ![screen](/screenshots/device-2016-04-06-001322.png)
![screen](/screenshots/device-2016-04-06-001415.png) ![screen](/screenshots/device-2016-04-06-001509.png)
![screen](/screenshots/device-2016-04-06-001642.png) ![screen](/screenshots/device-2016-04-06-001746.png)

###Tab

![screen](/screenshots/device-2016-04-05-235926.png) 
![screen](/screenshots/device-2016-04-06-000332.png)
![screen](/screenshots/device-2016-04-06-000434.png) 
![screen](/screenshots/device-2016-04-06-000154.png)
![screen](/screenshots/device-2016-04-06-001005.png)

### How to use the source
This app uses [TheMovieDbOrg](https://www.themoviedb.org/documentation/api) API to retrieve movies.
You must provide your own API KEY in order to build the app.
Add your API KEY in this file
    ```
    /app/src/main/java/com/bitshifters/rohit/popcorn/api/MovieDbOrgApiService.java
    ```

```
Copyright 2016 Rohit Sharma (skyrohithigh)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
