package beaconfinder.fun.berger.de.beaconfinder.webservice.pojo;

import java.io.Serializable;

public class Beacon implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1302935866334228654L;

    private int id;

    private String uuid;

    private String location;

    private int maj;

    private int min;

    public Beacon() {
    }

    public Beacon(int majMin, String uuid, String location) {
        this.id = majMin;
        this.uuid = uuid;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaj() {
        return maj;
    }

    public void setMaj(int maj) {
        this.maj = maj;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
