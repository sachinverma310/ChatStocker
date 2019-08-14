package stws.chatstocker.di.module;



import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import stws.chatstocker.di.ViewModelKey;
import stws.chatstocker.utils.ViewModelFactory;
import stws.chatstocker.viewmodel.LoginViewModel;
import stws.chatstocker.viewmodel.OtpViewModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;



@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindListViewModel(LoginViewModel listViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OtpViewModel.class)
    abstract ViewModel bindOtpViewModel(OtpViewModel optViewModel);

//    @Binds
//    @IntoMap
//    @ViewModelKey(DetailsViewModel.class)
//    abstract ViewModel bindDetailsViewModel(DetailsViewModel detailsViewModel);
//
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
