package stws.chatstocker;

import android.app.Activity;
import android.app.Application;

import stws.chatstocker.di.component.ApplicationComponent;
import stws.chatstocker.di.component.DaggerApplicationComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.DaggerApplication;


public class BaseApplication extends Application implements HasActivityInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityDispatchingInjector;
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationComponent component = DaggerApplicationComponent.builder().application(this).build();
        component.inject(this);

    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityDispatchingInjector;
    }
}