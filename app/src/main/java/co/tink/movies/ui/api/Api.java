package co.tink.movies.ui.api;

import java.util.List;

import co.tink.movies.ui.items.Movie;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Cantador on 09.09.17.
 */

public interface Api {

  @GET("movies.json")
  Call<List<Movie>> getMovies();
}
