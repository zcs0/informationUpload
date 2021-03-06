package com.informationUpload.utils;

import android.location.Location;

import com.tencent.map.geolocation.TencentGeofence;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.mapsdk.raster.model.GeoPoint;

public class Utils {
	public static GeoPoint of(TencentLocation location) {
		return new GeoPoint((int) (location.getLatitude() * 1E6),
				(int) (location.getLongitude() * 1E6));
	}

	public static GeoPoint of(double latitude, double longitude) {
		return new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
	}

	public static double fmt(double d) {
		long i = (long) (d * 1e6);
		return i / 1e6;
	}

	public static String toString(TencentGeofence geofence) {
		return geofence.getTag() + " " + geofence.getLatitude() + ","
				+ geofence.getLongitude();
	}

	public static String toString(Location location) {
		return location.getLatitude() + "," + location.getLongitude();
	}
}
