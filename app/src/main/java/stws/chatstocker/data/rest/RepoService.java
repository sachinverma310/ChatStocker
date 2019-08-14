package stws.chatstocker.data.rest;

import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Single;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RepoService {

    @POST("iqurious/api/user_login.php")
    Single<JsonObject> getRepositories(@Body JsonObject jsonObject);
//
//    @GET("repos/{owner}/{name}")
//    Single<Repo> getRepo(@Path("owner") String owner, @Path("name") String name);
}
