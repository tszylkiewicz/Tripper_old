package com.example.tripper.fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.tripper.MainActivity;
import com.example.tripper.R;
import com.example.tripper.algorithm.Centroid;
import com.example.tripper.model.Trip;
import com.example.tripper.model.User;
import com.example.tripper.viewmodel.MapViewModel;
import com.example.tripper.viewmodel.TripViewModel;
import com.example.tripper.viewmodel.UserViewModel;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

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
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import io.reactivex.disposables.CompositeDisposable;


public class MapFragment extends Fragment implements MapEventsReceiver, LocationListener {

    private MapViewModel mapViewModel;
    private TripViewModel tripViewModel;
    private UserViewModel userViewModel;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private MapView map;
    private Context context;
    private IMapController mapController;
    private NavController navController;

    private ImageButton zoomIn;

    private LocationManager locationManager;
    private Location location = null;

    //Overlays
    private CompassOverlay compassOverlay;
    private MyLocationNewOverlay myLocationNewOverlay;

    private SpeedDialView speedDialView;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

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
        mapViewModel.getDays().observe(requireActivity(), msg -> System.out.println("Zmieniono z mapa na: " + msg));

        mapViewModel.getCentroids().observe(requireActivity(), centroids -> {
            drawCentroids(centroids);
        });
    }

    private void drawCentroids(ArrayList<GeoPoint> centroids) {
        for (GeoPoint geo :
                centroids) {
            Marker newMarker = new Marker(map);
            newMarker.setPosition(geo);
            newMarker.setTitle("Centroid");
            newMarker.setOnMarkerClickListener((marker1, mapView) -> {
                removeMarker(marker1);
                return false;
            });
            map.getOverlays().add(newMarker);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        context = this.getContext();

        map = view.findViewById(R.id.mapview);
        zoomIn = view.findViewById(R.id.zoom_in);
        ImageButton zoomOut = view.findViewById(R.id.zoom_out);

        MapEventsOverlay OverlayEvents = new MapEventsOverlay(this);
        map.getOverlays().add(OverlayEvents);

        zoomIn.setOnClickListener(view1 -> zoomIn());
        zoomOut.setOnClickListener(view12 -> zoomOut());

        compassOverlay = new CompassOverlay(context, new InternalCompassOrientationProvider(context), map);
        myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), map);

        compassOverlay.enableCompass();

        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.setOptionsMenuEnabled(true);

        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);
        map.setFlingEnabled(true);
        map.getOverlays().add(myLocationNewOverlay);
        map.getOverlays().add(compassOverlay);

        map.setTileSource(mapViewModel.getTileSource());

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        mapController = map.getController();
        mapController.setZoom(mapViewModel.getCurrentZoomLevel());
        mapController.setCenter(mapViewModel.getCurrentCenter());


        speedDialView = view.findViewById(R.id.speedDial);
        addSpeedDialElement(R.id.fab_properties, R.drawable.ic_properties, R.string.fab_properties);
        addSpeedDialElement(R.id.fab_clear_markers, R.drawable.ic_clear_map, R.string.fab_clear_map);
        addSpeedDialElement(R.id.fab_clear_routes, R.drawable.ic_clear_map, R.string.fab_clear_routes);
        addSpeedDialElement(R.id.fab_create_route, R.drawable.ic_play, R.string.fab_create_route);

        navController = Navigation.findNavController(view);
        speedDialView.setOnActionSelectedListener(speedDialActionItem -> {
            switch (speedDialActionItem.getId()) {
                case R.id.fab_create_route:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        removeAllRoutes();
                        drawRoads(mapViewModel.calculateRoad(new ArrayList<>()));
                    }
                    return false;
                case R.id.fab_clear_markers:
                    removeAllMarkers();
                    return true;

                case R.id.fab_properties:
                    navController.navigate(R.id.mapSettingsFragment, null);
                    return true;
                case R.id.fab_clear_routes:
                    removeAllRoutes();
                    return true;
                default:
                    return false;
            }
        });

        removeAllMarkers();
        /*ArrayList<GeoPoint> testPunkty = mapViewModel.getCurrentPoints();
        if (testPunkty.size() > 0) {
            for (GeoPoint testPoint : testPunkty) {
                longPressHelper(testPoint);
            }
            map.getController().animateTo(testPunkty.get(0));
        }*/
        ArrayList<GeoPoint> testPunkty = new ArrayList<>();
        testPunkty.add(new GeoPoint(51.760420, 19.407577));
        testPunkty.add(new GeoPoint(51.757175, 19.425164));
        testPunkty.add(new GeoPoint(51.763931, 19.412367));
        testPunkty.add(new GeoPoint(51.766401, 19.415226));
        testPunkty.add(new GeoPoint(51.764568, 19.421320));
        testPunkty.add(new GeoPoint(51.776187, 19.437574));
        testPunkty.add(new GeoPoint(51.778947, 19.446647));
        testPunkty.add(new GeoPoint(51.780009, 19.447795));
        testPunkty.add(new GeoPoint(51.780699, 19.447065));
        testPunkty.add(new GeoPoint(51.778781, 19.451303));
        testPunkty.add(new GeoPoint(51.780732, 19.456903));
        testPunkty.add(new GeoPoint(51.776776, 19.454704));
        testPunkty.add(new GeoPoint(51.776285, 19.455155));
        testPunkty.add(new GeoPoint(51.775953, 19.454812));
        testPunkty.add(new GeoPoint(51.774771, 19.455091));
        testPunkty.add(new GeoPoint(51.772879, 19.455788));
        testPunkty.add(new GeoPoint(51.770644, 19.464006));
        testPunkty.add(new GeoPoint(51.769628, 19.465669));
        testPunkty.add(new GeoPoint(51.768054, 19.469961));
        testPunkty.add(new GeoPoint(51.779788, 19.463809));
        testPunkty.add(new GeoPoint(51.780419, 19.468186));
        testPunkty.add(new GeoPoint(51.782437, 19.469205));
        testPunkty.add(new GeoPoint(51.785496, 19.474616));
        testPunkty.add(new GeoPoint(51.785290, 19.470764));
        testPunkty.add(new GeoPoint(51.792483, 19.471519));
        testPunkty.add(new GeoPoint(51.763779, 19.457791));
        testPunkty.add(new GeoPoint(51.759068, 19.458263));
        testPunkty.add(new GeoPoint(51.745045, 19.462705));
        testPunkty.add(new GeoPoint(51.759743, 19.474864));
        testPunkty.add(new GeoPoint(51.760533, 19.479864));
        testPunkty.add(new GeoPoint(51.754974, 19.482192));
        testPunkty.add(new GeoPoint(51.754368, 19.444021));
        if (testPunkty.size() > 0) {

            map.getController().animateTo(testPunkty.get(0));
            ArrayList<ArrayList<GeoPoint>> centrs = mapViewModel.calculateRoad(testPunkty);
            for (int i = 0; i < centrs.size(); i++) {
                ArrayList<GeoPoint> clist = centrs.get(i);
                for (GeoPoint pts : clist) {
                    customDrawPoint(pts, i, clist.indexOf(pts));
                }
            }
            drawRoads(centrs);
        }
    }

    private void customDrawPoint(GeoPoint pts, int i, int x) {
        Marker marker = new Marker(map);
        marker.setPosition(pts);
        marker.setTitle("Element " +x);
        switch (i) {
            case 0:
                marker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                break;
            case 1:
                marker.setIcon(getResources().getDrawable(R.drawable.marker1));
                break;
            case 2:
                marker.setIcon(getResources().getDrawable(R.drawable.marker2));
                break;
            case 3:
                marker.setIcon(getResources().getDrawable(R.drawable.marker3));
                break;
            default:
                break;
        }
        map.getOverlays().add(marker);
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
        // map.getOverlays().addAll(mapViewModel.getMarkers());
        if (mapViewModel.getMarkers().size() > 0) {
            ArrayList<Marker> oldMarkers = new ArrayList<>(mapViewModel.getMarkers());
            mapViewModel.removeAllMarkers();
            for (Marker marker : oldMarkers
            ) {
                Marker newMarker = new Marker(map);
                newMarker.setPosition(marker.getPosition());
                newMarker.setTitle("Element");
                newMarker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
                newMarker.setOnMarkerClickListener((marker1, mapView) -> {
                    removeMarker(marker1);
                    return false;
                });
                mapViewModel.addMarker(newMarker);
                map.getOverlays().add(newMarker);
            }
        }

        //System.out.println("Dni mapa resume: " + mapViewModel.getDays().getValue());
        //System.out.println("Dni mapa resume: " + mapViewModel.getTransportType().getValue());
    }

    @Override
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

        mapViewModel.setCurrentZoomLevel(map.getZoomLevelDouble());
        mapViewModel.setCurrentCenter(new GeoPoint(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude()));
    }

    private void removeAllMarkers() {
        map.getOverlays().removeAll(mapViewModel.getMarkers());
        mapViewModel.removeAllMarkers();
    }

    private void removeAllRoutes() {
        map.getOverlays().removeAll(tripViewModel.getCreatedRoutes().keySet());
        //map.getOverlays().removeAll(mapViewModel.getRoutes());
        mapViewModel.removeAllRoutes();
    }

    private void removeMarker(Marker marker) {
        mapViewModel.removeMarker(marker);
        map.getOverlays().remove(marker);
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
                    System.out.println("Grupa: "+markerList.size()+" -> "+roadOverlay.getDistance());
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
                                tripViewModel.saveTrip(polyline, userViewModel.getCurrentUser().getId(), "Name", "Description", "car");
                                //tripViewModel.savePoints(polyline, userViewModel.getCurrentUser().getId());
                            }
                            return true;
                        case R.id.remove:
                            mapViewModel.removeRoute(polyline);
                            mapView.getOverlays().remove(polyline);
                            return true;
                        default:
                            return false;
                    }
                });
                popupMenu.inflate(R.menu.route_menu);
                popupMenu.show();
                //mapViewModel.removeRoute(polyline);
                //mapView.getOverlays().remove(polyline);
                return false;
            });

        }
        map.getOverlays().addAll(routes);
    }

    private void zoomIn() {
        if (location != null) {
            GeoPoint myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
            map.getController().animateTo(myPosition);
        }
    }

    private void zoomOut() {
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
        Marker marker = new Marker(map);
        marker.setPosition(p);
        marker.setTitle("Element");
        marker.setIcon(getResources().getDrawable(R.drawable.ic_marker));
        marker.setOnMarkerClickListener((marker1, mapView) -> {
            removeMarker(marker1);
            return false;
        });
        mapViewModel.addMarker(marker);
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
