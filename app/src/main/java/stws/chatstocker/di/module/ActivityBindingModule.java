package stws.chatstocker.di.module;



import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import stws.chatstocker.di.PerActivity;
import stws.chatstocker.view.LoginActivity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;


@Module(includes = ContextModule.class)
public abstract class ActivityBindingModule {
    @PerActivity
    @ContributesAndroidInjector
    abstract LoginActivity bindMainActivity();

}
