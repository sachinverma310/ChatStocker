package stws.chatstocker.di.component;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Retrofit;
import stws.chatstocker.BaseApplication;
import stws.chatstocker.di.PerActivity;
import stws.chatstocker.di.module.ActivityBindingModule;
import stws.chatstocker.di.module.ApplicationModule;
import stws.chatstocker.di.module.ContextModule;
import stws.chatstocker.view.LoginActivity;
import stws.chatstocker.viewmodel.LoginViewModel;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Binds;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {ContextModule.class, ApplicationModule.class, AndroidSupportInjectionModule.class, ActivityBindingModule.class})
public interface ApplicationComponent extends AndroidInjector<BaseApplication> {

    void inject(BaseApplication application);

    //    void inject(LoginActivity loginActivity);

    //    AppCompatActivity activity();
//    Context provideContext();
    Retrofit provideRetrofit();
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        ApplicationComponent build();
    }
}