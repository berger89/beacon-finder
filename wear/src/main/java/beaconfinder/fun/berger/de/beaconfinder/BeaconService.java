package beaconfinder.fun.berger.de.beaconfinder;

import com.google.gson.JsonElement;

import org.altbeacon.beacon.Beacon;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Berger on 29.06.2016.
 */
public interface BeaconService {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://192.168.2.102:8080/beacon.management/rest/beaconres/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @GET("beaconlocation/{id}")
    Call<beaconfinder.fun.berger.de.beaconfinder.Beacon> getBeaconLocation(@Path("id") int repo);

}
