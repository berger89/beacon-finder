package beaconfinder.fun.berger.de.beaconfinder.rest;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Created by Berger on 29.06.2016.
 */
public interface BeaconService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.2.110:8080/beacon.management/rest/beaconres/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET
    Call<Boolean> getBeaconLocation(@Url String pathMethod);

}
