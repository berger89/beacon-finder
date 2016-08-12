package beaconfinder.fun.berger.de.beaconfinder.rest;

import java.io.Serializable;
import java.math.BigDecimal;

public class Beacon implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1302935866334228654L;

    private int id;

    private int major;

    private int minor;

    private String uuid;

    private BigDecimal lat;

    private BigDecimal lon;

    private BigDecimal altitude;

    private String location;

    public Beacon() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int maj) {
        this.major = maj;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int min) {
        this.minor = min;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
