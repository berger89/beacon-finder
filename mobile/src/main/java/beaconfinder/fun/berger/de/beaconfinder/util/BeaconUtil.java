package beaconfinder.fun.berger.de.beaconfinder.util;

import org.altbeacon.beacon.Beacon;

/**
 * Created by Berger on 07.01.2017.
 */

public class BeaconUtil {

    private String id;

    private Beacon eddystoneUID;
    private Beacon eddystoneTLM;
    private Beacon eddystoneURL;
    private Beacon iBeacon;
    private Beacon lastAddedBeacon;

    public void addBeacon(Beacon beacon) {
        if (beacon.getServiceUuid() == 0xfeaa) {
            if (beacon.getBeaconTypeCode() == 0x00) {
                eddystoneUID = beacon;
            } else if (beacon.getBeaconTypeCode() == 0x10) {
                eddystoneURL = beacon;
            } else if (beacon.getBeaconTypeCode() == 0x20) {
                eddystoneTLM = beacon;
            }
        } else {
            iBeacon = beacon;
        }
        lastAddedBeacon = beacon;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Beacon getEddystoneUID() {
        return eddystoneUID;
    }

    public void setEddystoneUID(Beacon eddystoneUID) {
        this.eddystoneUID = eddystoneUID;
    }

    public Beacon getEddystoneTLM() {
        return eddystoneTLM;
    }

    public void setEddystoneTLM(Beacon eddystoneTLM) {
        this.eddystoneTLM = eddystoneTLM;
    }

    public Beacon getiBeacon() {
        return iBeacon;
    }

    public void setiBeacon(Beacon iBeacon) {
        this.iBeacon = iBeacon;
    }

    public Beacon getEddystoneURL() {
        return eddystoneURL;
    }

    public void setEddystoneURL(Beacon eddystoneURL) {
        this.eddystoneURL = eddystoneURL;
    }

    public Beacon getLastAddedBeacon() {
        return lastAddedBeacon;
    }

    public void setLastAddedBeacon(Beacon lastAddedBeacon) {
        this.lastAddedBeacon = lastAddedBeacon;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (eddystoneUID != null)
            stringBuilder.append("Eddystone UID :\n" + eddystoneUID + "\n");
        if (eddystoneTLM != null)
            stringBuilder.append("Eddystone TLM :\n" + eddystoneTLM + "\n");
        if (eddystoneURL != null)
            stringBuilder.append("Eddystone URL :\n" + eddystoneURL + "\n");
        if (iBeacon != null)
            stringBuilder.append("iBeacon :\n" + iBeacon);
        return stringBuilder.toString();
    }
}
