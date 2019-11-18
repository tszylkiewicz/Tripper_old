package com.example.tripper;


import android.Manifest;
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

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

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


public class MapFragment extends Fragment implements MapFragmentContract.View, MapEventsReceiver {

    private MapFragmentPresenter presenter;
    private MapView map;
    private Context context;
    private IMapController mapController;

    public MapFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = view.findViewById(R.id.mapview);
        context = this.getContext();
        presenter = new MapFragmentPresenter(this, this.context);

        /*Drawable nodeIcon = getResources().getDrawable(R.drawable.ic_menu_settings);
        for (int i = 0; i < road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step " + i);
            map.getOverlays().add(nodeMarker);
        }*/

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(this);
        map.getOverlays().add(OverlayEvents);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        mLocationOverlay.enableMyLocation();
        mLocationOverlay.enableFollowLocation();


        SpeedDialView speedDialView = view.findViewById(R.id.speedDial);
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_properties, R.drawable.ic_properties)
                        .setLabel(getString(R.string.fab_properties))
                        .setFabBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .setFabImageTintColor(ContextCompat.getColor(context, R.color.text))
                        .setLabelColor(ContextCompat.getColor(context, R.color.primaryText))
                        .setLabelBackgroundColor(Color.WHITE)
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_clear_map, R.drawable.ic_clear_map)
                        .setLabel(getString(R.string.fab_clear_map))
                        .setFabBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .setFabImageTintColor(ContextCompat.getColor(context, R.color.text))
                        .setLabelColor(ContextCompat.getColor(context, R.color.primaryText))
                        .setLabelBackgroundColor(Color.WHITE)
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_create_route, R.drawable.ic_play)
                        .setLabel(getString(R.string.fab_create_route))
                        .setFabBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .setFabImageTintColor(ContextCompat.getColor(context, R.color.text))
                        .setLabelColor(ContextCompat.getColor(context, R.color.primaryText))
                        .setLabelBackgroundColor(Color.WHITE)
                        .create()
        );
        speedDialView.addActionItem(
                new SpeedDialActionItem.Builder(R.id.fab_my_location, R.drawable.ic_my_location)
                        .setLabel(getString(R.string.fab_my_location))
                        .setFabBackgroundColor(ContextCompat.getColor(context, R.color.primaryColor))
                        .setFabImageTintColor(ContextCompat.getColor(context, R.color.text))
                        .setLabelColor(ContextCompat.getColor(context, R.color.primaryText))
                        .setLabelBackgroundColor(Color.WHITE)
                        .create()
        );

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
                    Intent i = new Intent(context.getApplicationContext(), PropertiesActivity.class);
                    startActivity(i);
                    return false;
                case R.id.fab_my_location:
                    Toast.makeText(context, "Localization", Toast.LENGTH_LONG).show();
                    return false;
                default:
                    return false;
            }
        });

        CompassOverlay mCompassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        mCompassOverlay.enableCompass();
        map.getOverlays().add(mCompassOverlay);

        final Context context = this.getActivity();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(mScaleBarOverlay);

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);

        ImageButton zoomIn = view.findViewById(R.id.zoom_in);
        ImageButton zoomOut = view.findViewById(R.id.zoom_out);
        zoomIn.setOnClickListener(view1 -> zoomIn());
        zoomOut.setOnClickListener(view12 -> zoomOut());

        return view;
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
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
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        presenter.addMarker(p, map, getResources().getDrawable(R.drawable.ic_marker));
        return false;
    }
}
