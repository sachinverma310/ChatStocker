package stws.chatstocker.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Retrofit;
import stws.chatstocker.ConstantsValues;
import stws.chatstocker.R;
import stws.chatstocker.data.rest.RepoRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import stws.chatstocker.data.rest.RepoService;

public class LoginViewModel extends ViewModel implements ConstantsValues {

    private RepoRepository repoRepository;
    @Inject
    Context activity;
    @Inject
    Retrofit retrofit;
    private CompositeDisposable disposable;

//    private final MutableLiveData<List<Repo>> repos = new MutableLiveData<>();
    private final MutableLiveData<Boolean> repoLoadError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();
    //    public LoginViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
//        super(dataManager, schedulerProvider);
//    }

    @Inject
    public LoginViewModel(RepoRepository repoRepository) {
        this.repoRepository = repoRepository;
        disposable = new CompositeDisposable();

    }





    public void fetchRepos() {
//        loading.setValue(true);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("api_auth_key", "1094dc7c448875b68e77f61de8e779c622791efcda4cbfca4811e9e49d3456e2");
        boolean numeric = true;

            jsonObject.addProperty("user_email", "dev@mailinator.com");
            jsonObject.addProperty("user_number", "");

        jsonObject.addProperty("password", "12345");
        jsonObject.addProperty("user_device_token","fghsfggfdhjjhdg");

        disposable.add(repoRepository.getRepositories(jsonObject).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject value) {
                        repoLoadError.setValue(false);
//                        repos.setValue(value);
                        loading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        repoLoadError.setValue(true);
                        loading.setValue(false);
                    }
                }));
    }
}
