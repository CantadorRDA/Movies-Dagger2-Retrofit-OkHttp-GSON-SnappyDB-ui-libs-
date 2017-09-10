package co.tink.movies;

import co.tink.movies.dagger.component.DaggerNetworkComponent;
import co.tink.movies.dagger.component.NetworkComponent;
import co.tink.movies.dagger.module.ApplicationModule;
import co.tink.movies.dagger.module.NetworkModule;

/**
 * Created by Cantador on 10.09.17.
 */

public class MoviesApplication extends android.app.Application {

  private NetworkComponent networkComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    networkComponent = DaggerNetworkComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .networkModule(new NetworkModule(getResources().getString(R.string.base_url)))
        .build();

  }

  public NetworkComponent getNetworkComponent() {
    return networkComponent;
  }
}