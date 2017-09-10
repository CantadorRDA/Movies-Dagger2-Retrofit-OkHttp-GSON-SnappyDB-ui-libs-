package co.tink.movies.dagger.component;

import co.tink.movies.dagger.module.ApplicationModule;
import co.tink.movies.dagger.module.NetworkModule;
import co.tink.movies.ui.activities.ActivityMain;

import javax.inject.Singleton;

import co.tink.movies.ui.fragments.FragmentMoviesStrip;
import dagger.Component;

/**
 * Created by Cantador on 10.09.17.
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface NetworkComponent {
  void inject(FragmentMoviesStrip fragmentMoviesStrip);
}