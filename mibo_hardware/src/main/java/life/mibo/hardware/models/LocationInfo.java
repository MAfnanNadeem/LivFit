package life.mibo.hardware.models;

/**
 * Created by Fer on 04/04/2019.
 */
import org.json.JSONException;
import org.json.JSONObject;

import life.mibo.hardware.encryption.MCrypt;


public class LocationInfo {

    private String locationId;
    private String gymId;
    private String locationName;
    private String address;
    private String city;
    private String country;
    private String latitude;
    private String longitude;

    public LocationInfo(String locationId, String gymID, String locationName, String address, String city,
                        String country, String latitude, String longitude) {
        this.locationId = locationId;
        this.gymId = gymID;
        this.locationName = locationName;
        this.address = address;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LocationInfo() {
        this.locationId = "";
        this.gymId = "";
        this.locationName = "";
        this.address = "";
        this.city = "";
        this.country = "";
        this.latitude = "";
        this.longitude = "";
    }

    public JSONObject toJSON() {
        JSONObject locationInfo = new JSONObject();
        try {
            locationInfo.accumulate("locationId", getLocationId());
            locationInfo.accumulate("gymID", getGymId());
            locationInfo.accumulate("locationName", getLocationName());
            locationInfo.accumulate("address", getAddress());
            locationInfo.accumulate("city", getCity());
            locationInfo.accumulate("country", getCountry());
            locationInfo.accumulate("latitude", getLatitude());
            locationInfo.accumulate("longitude", getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationInfo;
    }

    public void setLocationInfoFromJSONAPI(JSONObject locationInfo){
        MCrypt mcrypt = new MCrypt();
        try {
        setLocationId(new String( mcrypt.decrypt(locationInfo.getString("locationId"))));
        setGymId(new String( mcrypt.decrypt(locationInfo.getString("gymId"))));
        setLocationName(new String( mcrypt.decrypt(locationInfo.getString("locationName"))));
        setAddress(new String( mcrypt.decrypt(locationInfo.getString("address"))));
        setCity(new String( mcrypt.decrypt(locationInfo.getString("city"))));
        setCountry(new String( mcrypt.decrypt(locationInfo.getString("country"))));
        setLatitude(new String( mcrypt.decrypt(locationInfo.getString("latitude"))));
        setLongitude(new String( mcrypt.decrypt(locationInfo.getString("longitude"))));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
