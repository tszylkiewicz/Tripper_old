package com.example.tripper.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tripper.MapFragmentContract;
import com.example.tripper.MapFragmentPresenter;
import com.example.tripper.R;
import com.example.tripper.viewmodel.ExploreViewModel;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.TripsViewModel;
import com.google.android.gms.common.internal.Constants;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;


public class MapFragment extends Fragment implements MapFragmentContract.View, MapEventsReceiver, LocationListener {

    private MapViewModel mapViewModel;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private MapFragmentPresenter presenter;
    private MapView map;
    private Context context;
    private IMapController mapController;

    private LocationManager locationManager;
    private Location location = null;

    //Overlays
    private CompassOverlay compassOverlay;
    private MyLocationNewOverlay myLocationNewOverlay;
    private RotationGestureOverlay rotationGestureOverlay;
    private ScaleBarOverlay scaleBarOverlay;


    private SpeedDialView speedDialView;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapViewModel = ViewModelProviders.of(requireActivity()).get(MapViewModel.class);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context = this.getContext();

        map = view.findViewById(R.id.mapview);
        ImageButton zoomIn = view.findViewById(R.id.zoom_in);
        ImageButton zoomOut = view.findViewById(R.id.zoom_out);

        presenter = new MapFragmentPresenter(this, getContext());

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(this);
        map.getOverlays().add(OverlayEvents);

        zoomIn.setOnClickListener(view1 -> zoomIn());
        zoomOut.setOnClickListener(view12 -> zoomOut());

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();

        compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);

        compassOverlay.enableCompass();

        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.setOptionsMenuEnabled(true);

        rotationGestureOverlay = new RotationGestureOverlay(map);
        rotationGestureOverlay.setEnabled(true);

        scaleBarOverlay = new ScaleBarOverlay(map);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);
        map.setFlingEnabled(true);
        map.getOverlays().add(myLocationNewOverlay);
        map.getOverlays().add(compassOverlay);
        map.getOverlays().add(scaleBarOverlay);

        speedDialView = view.findViewById(R.id.speedDial);
        addSpeedDialElement(R.id.fab_properties, R.drawable.ic_properties, R.string.fab_properties);
        addSpeedDialElement(R.id.fab_clear_map, R.drawable.ic_clear_map, R.string.fab_clear_map);
        addSpeedDialElement(R.id.fab_create_route, R.drawable.ic_play, R.string.fab_create_route);
        addSpeedDialElement(R.id.fab_my_location, R.drawable.ic_my_location, R.string.fab_my_location);
        addSpeedDialElement(R.id.fab_save_route, R.drawable.ic_clear_map, R.string.fab_save_route);

        final NavController navController = Navigation.findNavController(view);
        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.fab_create_route:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        presenter.calculateRoad();
                    }
                    return false;
                case R.id.fab_clear_map:
                    presenter.clearMap();
                    return false;

                case R.id.fab_properties:
                    navController.navigate(R.id.mapSettingsFragment, null);
                    return false;
                case R.id.fab_my_location:
                    if (location != null) {
                        GeoPoint myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                        map.getController().animateTo(myPosition);
                    }
                    return false;
                case R.id.fab_save_route:
                    navController.navigate(R.id.nav_sign_in, null);
                    return false;
                default:
                    return false;
            }
        });
    }

    public void onResume() {
        super.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
        try {
            //this fails on AVD 19s, even with the appcompat check, says no provided named gps is available
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0l, 0f, this);
        } catch (Exception ex) {
        }

        map.onResume();
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        scaleBarOverlay.enableScaleBar();
    }

    public void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        } catch (Exception ex) {
        }

        map.onPause();
        compassOverlay.disableCompass();
        myLocationNewOverlay.disableFollowLocation();
        myLocationNewOverlay.disableMyLocation();
        scaleBarOverlay.disableScaleBar();
    }

    @Override
    public void removeAllMarkers(ArrayList<Marker> markers) {
        map.getOverlays().removeAll(markers);
        Toast.makeText(context, "Map has been cleared", Toast.LENGTH_LONG).show();
        Log.d("overlays left", "" + map.getOverlays().size());
    }

    @Override
    public void removeAllRoads(ArrayList<Polyline> polylines) {
        map.getOverlays().removeAll(polylines);
    }

    @Override
    public void addMarker(Marker marker) {
        map.getOverlays().add(marker);

    }

    @Override
    public void removeMarker(Marker marker) {
        map.getOverlays().remove(marker);
    }

    @Override
    public void drawRoads(ArrayList<Polyline> roads) {
        map.getOverlays().addAll(roads);
    }

    @Override
    public void defaultSettings() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(13.0d);
        mapController.setCenter(new GeoPoint(51.13, 19.63));
    }

    @Override
    public void zoomIn() {
        if (map.canZoomIn()) {
            mapController.setZoom(map.getZoomLevelDouble() + 0.5d);
        }
    }

    @Override
    public void zoomOut() {
        if (map.canZoomOut()) {
            mapController.setZoom(map.getZoomLevelDouble() - 0.5d);
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        presenter.addMarker(p, map, getResources().getDrawable(R.drawable.ic_marker));
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager = null;
        location = null;

        myLocationNewOverlay = null;
        compassOverlay = null;
        scaleBarOverlay = null;
        rotationGestureOverlay = null;
    }

    private void addSpeedDialElement(int id, int drawable, int string) {
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(id, drawable)
                        .setLabel(getString(string))
                        .setFabBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .setFabImageTintColor(ContextCompat.getColor(context, R.color.text))
                        .setLabelColor(ContextCompat.getColor(context, R.color.primaryText))
                        .setLabelBackgroundColor(Color.WHITE)
                        .create()
        );
    }
}
