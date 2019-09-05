package com.example.tripper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class MapFragment extends Fragment implements MapFragmentContract.View {

    public MapFragmentPresenter presenter;
    MapView map;
    Context context;
    IMapController mapController;

    FloatingActionButton properties;
    FloatingActionButton createRoute;
    FloatingActionButton clearMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        map = view.findViewById(R.id.mapview);
context = this.getContext();
        presenter = new MapFragmentPresenter(this, this.context);

        /*
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(48.4, -1.9);
        waypoints.add(endPoint);
        Road road = roadManager.getRoad(waypoints);
        if (road.mStatus != Road.STATUS_OK) {
            Log.d("Road Status", "" + road.mStatus);
        }
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
*/
        /*Drawable nodeIcon = getResources().getDrawable(R.drawable.ic_menu_settings);
        for (int i = 0; i < road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step " + i);
            map.getOverlays().add(nodeMarker);
        }*/

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                //Toast.makeText(context, p.getLatitude() + " - " + p.getLongitude(), Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                presenter.addMarker(p, map);
                return false;
            }
        };


        MapEventsOverlay OverlayEvents = new MapEventsOverlay(mReceive);
        map.getOverlays().add(OverlayEvents);


        createRoute = view.findViewById(R.id.create_route);
        createRoute.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                presenter.calculateRoad();
            }
        });

        properties = view.findViewById(R.id.properties);
        properties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context.getApplicationContext(), PropertiesActivity.class);
                startActivity(i);
            }
        });


        clearMap = view.findViewById(R.id.clear_map);
        clearMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                presenter.clearMap();
            }
        });


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
        map.invalidate();
        Log.d("overlays left", "" + map.getOverlays().size());
    }

    @Override
    public void drawRoads(ArrayList<Polyline> roads) {
        map.getOverlays().addAll(roads);
    }

    @Override
    public void defaultSettings() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        mapController.setZoom(11.0d);
        mapController.setCenter(new GeoPoint(51.13, 19.63));
    }
}
