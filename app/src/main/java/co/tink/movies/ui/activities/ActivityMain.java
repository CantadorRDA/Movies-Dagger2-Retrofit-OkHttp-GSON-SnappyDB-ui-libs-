package co.tink.movies.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import javax.inject.Inject;

import co.tink.movies.MoviesApplication;
import co.tink.movies.R;
import co.tink.movies.ui.fragments.FragmentMoviesStrip;
import co.tink.movies.ui.util.NetworkCheck;
import retrofit2.Retrofit;

public class ActivityMain extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

      FragmentManager fragmentManager = getSupportFragmentManager();
      FragmentTransaction transaction = fragmentManager.beginTransaction();
      transaction.replace(R.id.content_frame_layout, new FragmentMoviesStrip(), "");
      transaction.commit();

  }

}
