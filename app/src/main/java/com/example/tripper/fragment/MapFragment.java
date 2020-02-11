package com.example.tripper.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.tripper.R;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;


public class MapFragment extends Fragment implements MapEventsReceiver, LocationListener {

    private MapViewModel mapViewModel;
    private TripViewModel tripViewModel;
    private UserViewModel userViewModel;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private MapView map;
    private Context context;
    private NavController navController;

    private ImageButton zoomIn;

    private LocationManager locationManager;
    private Location location = null;

    private CompassOverlay compassOverlay;
    private MyLocationNewOverlay myLocationNewOverlay;

    private SpeedDialView speedDialView;


    private ArrayList<Marker> markers = new ArrayList<>();
    private ArrayList<Polyline> polylines = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = ViewModelProviders.of(requireActivity()).get(UserViewModel.class);
        mapViewModel = ViewModelProviders.of(requireActivity()).get(MapViewModel.class);
        tripViewModel = ViewModelProviders.of(requireActivity()).get(TripViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context = this.getContext();
        navController = Navigation.findNavController(view);

        map = view.findViewById(R.id.mapview);
        zoomIn = view.findViewById(R.id.zoom_in);
        speedDialView = view.findViewById(R.id.speedDial);

        zoomIn.setOnClickListener(view1 -> zoomIn());

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(this);

        compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        compassOverlay.enableCompass();

        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.setOptionsMenuEnabled(true);

        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);
        map.setFlingEnabled(true);
        map.setTileSource(mapViewModel.getTileSource());

        map.getOverlays().add(OverlayEvents);
        map.getOverlays().add(myLocationNewOverlay);
        map.getOverlays().add(compassOverlay);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        IMapController mapController = map.getController();
        mapController.setZoom(mapViewModel.getCurrentZoomLevel());
        mapController.setCenter(mapViewModel.getCurrentCenter());

        addSpeedDialElement(R.id.fab_properties, R.drawable.ic_properties, R.string.fab_properties);
        addSpeedDialElement(R.id.fab_clear_markers, R.drawable.ic_clear_map, R.string.fab_clear_map);
        addSpeedDialElement(R.id.fab_clear_routes, R.drawable.ic_clear_map, R.string.fab_clear_routes);
        addSpeedDialElement(R.id.fab_create_route, R.drawable.ic_play, R.string.fab_create_route);

        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.fab_create_route:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        removeAllRoutes();
                        mapViewModel.setCurrentPoints(markers.stream().map(Marker::getPosition).collect(Collectors.toCollection(ArrayList::new)));
                        drawRoads(mapViewModel.calculateRoad());
                    }
                    return false;
                case R.id.fab_clear_markers:
                    removeAllMarkers();
                    return false;

                case R.id.fab_properties:
                    navController.navigate(R.id.mapSettingsFragment, null);
                    return false;
                case R.id.fab_clear_routes:
                    removeAllRoutes();
                    return false;
                default:
                    return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        map.onResume();
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();

        removeAllMarkers();
        removeAllRoutes();
        if (mapViewModel.getCurrentPoints().size() > 0) {
            for (GeoPoint point : mapViewModel.getCurrentPoints()) {
                longPressHelper(point);
            }
            map.getController().animateTo(mapViewModel.getCurrentPoints().get(0));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        map.onPause();
        compassOverlay.disableCompass();
        myLocationNewOverlay.disableFollowLocation();
        myLocationNewOverlay.disableMyLocation();

        mapViewModel.setCurrentZoomLevel(map.getZoomLevelDouble());
        mapViewModel.setCurrentCenter(new GeoPoint(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude()));
        mapViewModel.setCurrentPoints(markers.stream().map(Marker::getPosition).collect(Collectors.toCollection(ArrayList::new)));
    }

    private void removeAllMarkers() {
        map.getOverlays().removeAll(markers);
        markers.clear();
    }

    private void removeAllRoutes() {
        map.getOverlays().removeAll(polylines);
        polylines.clear();
    }

    private void removeMarker(Marker marker) {
        map.getOverlays().remove(marker);
        markers.remove(marker);
    }

    private void drawRoads(ArrayList<ArrayList<GeoPoint>> markers) {
        Random rnd = new Random();
        ArrayList<Polyline> routes = new ArrayList<>();
        RoadManager roadManager = new MapQuestRoadManager("QJmEiN5bbsOvOAv4MKvuuuEgepqLOqec");
        roadManager.addRequestOption("routeType=" + mapViewModel.getNavigationType().toLowerCase());

        for (ArrayList<GeoPoint> markerList : markers) {

            if (markerList.size() > 0) {
                Road singleRoad = roadManager.getRoad(markerList);

                if (singleRoad.mStatus != Road.STATUS_OK) {
                    Log.d("Road Status", "" + singleRoad.mStatus);
                } else {
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(singleRoad);
                    roadOverlay.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                    roadOverlay.setWidth(8);
                    routes.add(roadOverlay);
                    tripViewModel.addCreatedRoute(roadOverlay, markerList);
                }

                /*Drawable nodeIcon = getResources().getDrawable(R.drawable.ic_play);
                for (int i = 0; i < singleRoad.mNodes.size(); i++) {
                    RoadNode node = singleRoad.mNodes.get(i);
                    Marker nodeMarker = new Marker(map);
                    nodeMarker.setPosition(node.mLocation);
                    nodeMarker.setIcon(nodeIcon);
                    nodeMarker.setTitle("Step " + i);
                    nodeMarker.setSnippet(node.mInstructions);
                    nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                    Drawable icon = getResources().getDrawable(R.drawable.ic_menu_explore);
                    nodeMarker.setImage(icon);
                    map.getOverlays().add(nodeMarker);
                }*/
            }
        }
        for (Polyline road : routes
        ) {
            road.setOnClickListener((polyline, mapView, eventPos) -> {

                PopupMenu popupMenu = new PopupMenu(getActivity(), zoomIn);

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.save:
                            if (userViewModel.getCurrentUser() == null) {
                                navController.navigate(R.id.nav_sign_in);
                            } else {
                                tripViewModel.saveTrip(polyline, userViewModel.getCurrentUser().getId(), "New trip", "Route type: " + mapViewModel.getNavigationType(), mapViewModel.getNavigationType());
                            }
                            return true;
                        case R.id.remove:
                            mapView.getOverlays().remove(polyline);
                            polylines.remove(polyline);
                            return true;
                        default:
                            return false;
                    }
                });
                popupMenu.inflate(R.menu.route_menu);
                popupMenu.show();
                return false;
            });

        }
        polylines.addAll(routes);
        map.getOverlays().addAll(routes);
    }

    private void zoomIn() {
        if (location != null) {
            GeoPoint myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
            map.getController().animateTo(myPosition);
        }
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        Marker marker = new Marker(map);
        marker.setPosition(p);
        marker.setTitle("Element");
        marker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            removeMarker(marker1);
            return false;
        });
        markers.add(marker);
        map.getOverlays().add(marker);
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
