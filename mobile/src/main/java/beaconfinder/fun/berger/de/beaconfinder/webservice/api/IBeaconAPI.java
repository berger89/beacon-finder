package beaconfinder.fun.berger.de.beaconfinder.webservice.api;

import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.webservice.pojo.Beacon;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Berger on 21.05.2016.
 */
public interface IBeaconAPI {

    @GET("/beacon-management/rest/beaconres/beacons")
    public Call<List<Beacon>> getBeacons();
}
