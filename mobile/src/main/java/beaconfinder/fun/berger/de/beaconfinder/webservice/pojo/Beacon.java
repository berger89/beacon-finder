package beaconfinder.fun.berger.de.beaconfinder.webservice.pojo;

import java.io.Serializable;

public class Beacon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1302935866334228654L;

	private int majMin;

	private String uuid;

	private String locationName;

	public Beacon() {
	}

	public Beacon(int majMin, String uuid, String locationName) {
		this.majMin = majMin;
		this.uuid = uuid;
		this.locationName = locationName;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getMajMin() {
		return majMin;
	}

	public void setMajMin(int majMin) {
		this.majMin = majMin;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

}
