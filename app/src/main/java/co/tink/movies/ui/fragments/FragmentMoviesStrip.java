package co.tink.movies.ui.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import co.tink.movies.MoviesApplication;
import co.tink.movies.R;
import co.tink.movies.ui.adapters.AdapterGenres;
import co.tink.movies.ui.adapters.AdapterMoviesStrip;
import co.tink.movies.ui.api.Api;
import co.tink.movies.ui.items.Movie;
import co.tink.movies.ui.util.NetworkCheck;
import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Cantador on 09.09.17.
 */

public class FragmentMoviesStrip extends Fragment {

  @Inject
  Retrofit retrofit;

  private RecyclerView genresRecycler;
  private RecyclerView moviesStripRecycler;
  private ProgressBar progressBar;
  private ImageView blurredImg;
  private FloatingActionMenu sortFAB;
  private FloatingActionButton sortByYear;
  private FloatingActionButton sortByRating;
  private SwipeRefreshLayout swipeRefresh;
  private DrawerLayout drawerLayout;

  private AdapterMoviesStrip adapter;

  private NetworkCheck networkCheck;

  private DB snappydb;

  private boolean initialResponce = true;
  private boolean sorted = false;
  private String genre = null;

  private SharedPreferences prefs;

  private List<Movie> moviesList = new ArrayList<>();
  private List<String> genresList = new ArrayList<>();

  private static final String FIRST_LAUNCH = "first_launch";

  private View.OnClickListener onFabClick() {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (v == sortByYear) {
          compareByYear();
          adapter.notifyDataSetChanged();
        } else if (v == sortByRating) {
          compareByRating();
          adapter.notifyDataSetChanged();
        }
        sortFAB.close(true);
      }
    };
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MoviesApplication) getActivity().getApplication()).getNetworkComponent().inject(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_movies_strip, container, false);
    initViews(rootView);
    return rootView;
  }

  @Override
  public void onResume(){
    super.onResume();
    networkCheck = new NetworkCheck();
    prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    if (prefs.contains(FIRST_LAUNCH)){
      initialize();
    } else {
      if (networkCheck.isNetworkAvailable(getActivity())){
        initialize();
        prefs.edit().putBoolean(FIRST_LAUNCH, false).apply();
      } else {
        Toast.makeText(getActivity(), getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        drawerLayout.setVisibility(View.GONE);
      }
    }
  }

  private void initialize(){
    sortFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (sortFAB.isOpened()) {
          sortFAB.close(true);
        }
      }
    });
    sortByYear.setOnClickListener(onFabClick());
    sortByRating.setOnClickListener(onFabClick());
    genres();
    moviesStrip();
    swipeRefresh();
  }

  private void initViews(View rootView) {
    genresRecycler = rootView.findViewById(R.id.genres_recycler);
    moviesStripRecycler = rootView.findViewById(R.id.movies_strip_recycler);
    blurredImg = rootView.findViewById(R.id.blurred_img);
    progressBar = rootView.findViewById(R.id.progress_bar);
    sortFAB = rootView.findViewById(R.id.sort_fab);
    sortByYear = rootView.findViewById(R.id.sort_by_year);
    sortByRating = rootView.findViewById(R.id.sort_by_rating);
    swipeRefresh = rootView.findViewById(R.id.swipe_refresh);
    drawerLayout = rootView.findViewById(R.id.drawer_layout);
  }

  private void genres() {

    if (networkCheck.isNetworkAvailable(getActivity())) {

      final Call<List<Movie>> movies = retrofit.create(Api.class).getMovies();

      movies.enqueue(new Callback<List<Movie>>() {
        @Override
        public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
          genresList(response.body());
          storeInSnappyDB(response.body(), "movies");
        }

        @Override
        public void onFailure(Call<List<Movie>> call, Throwable t) {
          Log.d(getResources().getString(R.string.app_name), t.toString());
        }
      });
    } else {

      try {
        snappydb = DBFactory.open(getActivity());
        List<Movie> savedList = snappydb.get("movies", ArrayList.class);
        snappydb.close();
        genresList(savedList);
      } catch (SnappydbException e){

      }

    }
  }

  public void moviesStrip() {

    if (networkCheck.isNetworkAvailable(getActivity())) {

      final Call<List<Movie>> movies = retrofit.create(Api.class).getMovies();

      movies.enqueue(new Callback<List<Movie>>() {
        @Override
        public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {

          if (genre != null) {
            moviesList.addAll(sortByGenre(response.body()));
          } else {
            moviesList.addAll(response.body());
          }

          if (initialResponce) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            adapter = new AdapterMoviesStrip(
                getActivity(), FragmentMoviesStrip.this, linearLayoutManager, moviesList);
            moviesStripRecycler.setLayoutManager(linearLayoutManager);
            moviesStripRecycler.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            initialResponce = !initialResponce;
          } else {
            adapter.notifyDataSetChanged();
          }

          if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
          }

        }

        @Override
        public void onFailure(Call<List<Movie>> call, Throwable t) {
          Log.d(getResources().getString(R.string.app_name), t.toString());
        }
      });

    } else {
      try {
        snappydb = DBFactory.open(getActivity());
        List<Movie> savedList = snappydb.get("movies", ArrayList.class);
        snappydb.close();

        if (genre != null) {
          moviesList.addAll(sortByGenre(savedList));
        } else {
          moviesList.addAll(savedList);
        }

        if (initialResponce) {
          LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
          linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
          adapter = new AdapterMoviesStrip(
              getActivity(), FragmentMoviesStrip.this, linearLayoutManager, moviesList);
          moviesStripRecycler.setLayoutManager(linearLayoutManager);
          moviesStripRecycler.setAdapter(adapter);
          progressBar.setVisibility(View.GONE);
          initialResponce = !initialResponce;
        } else {
          moviesStripRecycler.post(new Runnable() {
            @Override
            public void run() {
              adapter.notifyDataSetChanged();
            }
          });
        }

      } catch (SnappydbException e){

      }
    }


  }

  public void setBlurImage(Bitmap bitmap) {
    Blurry.with(getActivity()).from(bitmap).into(blurredImg);
  }

  public void setGenre(String genre) {
    this.genre = genre;
    initialResponce = !initialResponce;
    moviesList = new ArrayList<>();
    moviesStrip();
  }

  public List<Movie> sortByGenre(List<Movie> moviesList) {

    List<Movie> sortedList = new ArrayList<>();

    for (Movie movie : moviesList) {
      for (String genre : movie.getGenres()) {
        if (genre.equals(this.genre)) {
          sortedList.add(movie);
          break;
        }
      }
    }

    return sortedList;
  }

  private void compareByYear() {
    Collections.sort(moviesList, new Comparator<Movie>() {
      @Override
      public int compare(Movie first, Movie second) {
        return Integer.compare(second.getReleaseYear(), first.getReleaseYear());
      }
    });
  }

  private void compareByRating() {
    Collections.sort(moviesList, new Comparator<Movie>() {
      @Override
      public int compare(Movie first, Movie second) {
        return Double.compare(second.getRating(), first.getRating());
      }
    });
  }

  private void sortGenresAlphabetically() {
    Collections.sort(genresList, new Comparator<String>() {
      @Override
      public int compare(String first, String second) {
        return first.compareToIgnoreCase(second);
      }
    });
  }

  private void swipeRefresh() {
    if (networkCheck.isNetworkAvailable(getActivity())) {

      swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
      swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          try {
            if (snappydb!=null)
              snappydb.destroy();
          } catch (SnappydbException e) {

          }
          initialResponce = !initialResponce;
          moviesList = new ArrayList<>();
          moviesStrip();
        }
      });
    } else {
      swipeRefresh.setEnabled(false);
      swipeRefresh.setRefreshing(false);
    }
  }

  private void storeInSnappyDB(List<Movie> list, String key) {
    try {
      snappydb = DBFactory.open(getActivity());
      snappydb.put(key, list);
      snappydb.close();
    } catch (SnappydbException e) {

    }
  }

  private void genresList(List<Movie> list){
    for (Movie movie : list) {
      for (String genre : movie.getGenres()) {
        if (!genresList.contains(genre)) {
          genresList.add(genre);
        }
      }
    }

    sortGenresAlphabetically();

    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    AdapterGenres adapter = new AdapterGenres(
        getActivity(), FragmentMoviesStrip.this, linearLayoutManager, genresList, genresRecycler);
    genresRecycler.setLayoutManager(linearLayoutManager);
    genresRecycler.setAdapter(adapter);
  }
}
