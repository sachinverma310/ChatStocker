package stws.chatstocker.data.rest;

import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import retrofit2.http.Body;


public class RepoRepository {

    private final RepoService repoService;

    @Inject
    public RepoRepository(RepoService repoService) {
        this.repoService = repoService;
    }

    public Single<JsonObject> getRepositories(JsonObject jsonObject) {
        return repoService.getRepositories(jsonObject);
    }
//
//    public Single<Repo> getRepo(String owner, String name) {
//        return repoService.getRepo(owner, name);
//    }
}
