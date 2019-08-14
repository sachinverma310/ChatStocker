package stws.chatstocker.di.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import stws.chatstocker.di.PerActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ActivityKey;

@Module
public abstract class ContextModule {

    //    @PerActivity
//    @Binds
//    abstract Activity provideActivity(AppCompatActivity appCompatActivity);
//    @Binds
//    abstract Context provideContext(Application application);
//    @Binds
//    @ActivityContext
//    abstract Activity provideActivtiyContext(AppCompatActivity appCompatActivity);
//    @Binds
//    @PerActivity
//    abstract Context activityContext(Activity activity);
    @Binds
    abstract Context provideContext(Application application);

}
