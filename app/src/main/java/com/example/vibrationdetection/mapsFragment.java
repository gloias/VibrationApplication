package com.example.vibrationdetection;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibrationdetection.data.AppDatabase;
import com.example.vibrationdetection.data.AsynchronousDatabase;
import com.example.vibrationdetection.data.entities.Accelerometer;
import com.example.vibrationdetection.data.entities.RoadPoint;
import com.example.vibrationdetection.sensors.AccelerometerSensor;
import com.example.vibrationdetection.sensors.LocationSensor;
import com.example.vibrationdetection.service.ForegroundConstants;
import com.example.vibrationdetection.service.ForegroundService;
import com.example.vibrationdetection.utils.Vector3D;
import com.google.android.gms.common.api.internal.BackgroundDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.concurrent.Executor;

public class mapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG ="Maps";
    private PageViewModel pageViewModel;
    private MapView mapView;
    private GoogleMap gmap;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private FloatingActionButton fab;
    public mapsFragment() {}
    private static boolean state=false;
    private LinearLayout bottomSheetLayout;
    private int gpsRecordCount = 0;
    private int accelRecordCount = 0;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView recyclerView = null;
    AccelerometerSensor accelerometerSensor;
    private LocationSensor locationSensor;

    private AsynchronousDatabase database;
    View root;

    public static mapsFragment newInstance(){
        return new mapsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        pageViewModel.setIndex(TAG);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.maps, container, false);
        mapView =(MapView) root.findViewById(R.id.map_view);
        Bundle mapViewBundle = null;
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                fabAction();
            }
        });


        initGoogleMap(savedInstanceState);

        return root;
    }

    private void fabAction() {
        if(state==false) {
            //for the shake of argument
            fab.setImageDrawable(getResources().getDrawable( R.drawable.ic_pause));
            fab.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorRed)));
            fab.setX(mapView.getWidth()-fab.getWidth());
            fab.setY(mapView.getHeight()-fab.getHeight());
            state = true;
            fabOnStartAction();
        }
        else{
            // close location services probably
            fab.setImageDrawable(getResources().getDrawable( R.drawable.ic_play_arrow_white));
            fab.setBackgroundTintList(ColorStateList.valueOf( getResources().getColor(R.color.colorBlue)));
            //fab.setX(mapView.getWidth()/2);
            //fab.setY(mapView.getHeight()/2);
            fab.setX(mapView.getWidth()/2-fab.getWidth()/2);
            fab.setY(mapView.getHeight()/2-fab.getHeight()/2);
            state = false;
            fabOnStartAction();

            //accelerometerSensor.stop();
            //locationSensor.stop();
        }
    }

    private void fabOnStartAction() {
        Intent service = new Intent (getActivity(), ForegroundService.class);
        Log.i("LOG_TAG", "Received Start Foreground Intent ");

        if (!ForegroundService.IS_SERVICE_RUNNING) {
            service.setAction(ForegroundConstants.ACTION.STARTFOREGROUND_ACTION);
            getActivity().startService(service);
//                showLastKnownLocation(gmap);
//                //gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,13f));

        } else {
            service.setAction(ForegroundConstants.ACTION.STOPFOREGROUND_ACTION);
            getActivity().startService(service);
//          AppDatabase db = AppDatabase.getInstance(getContext());
//          List<RoadPoint> locationList = db.roadPointDao().getAll();
//          if(!locationList.isEmpty()){
//            for(RoadPoint point : locationList){
//                 LatLng loc = new LatLng(point.getLatitude() , point.getLongitude());
//                gmap.addMarker(new MarkerOptions().position(loc).title("Hole: "+Integer.toString(gpsRecordCount)));
//
//            }
          }

          //db.close();
        //}


        //    accelerometerSensor = new AccelerometerSensor(this.getContext()) {
//        @Override
//        public void onUpdate(Vector3D a, Vector3D g) {
//            if (accelerometerSensor.significantMotionDetected()) {
//                //database.addAccelerometerEntry(a, g);
//                if(accelerometerSensor.significantMotionDetected()){
//                    accelRecordCount++;
//
//
//                }
//
//            }
//        }
//
//    };
//
//    locationSensor = new LocationSensor(this.getContext()) {
//        @Override
//        public void onUpdate(Location location) {
//            //database.addLocationEntry(location);
//            if(accelerometerSensor.significantMotionDetected()){
//                gpsRecordCount++;
//
//                LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());
//                gmap.addMarker(new MarkerOptions().position(loc).title("Hole: "+Integer.toString(gpsRecordCount)));
//                TextView text = root.findViewById( R.id.holes);
//                text.setText("Holes: "+Integer.toString(gpsRecordCount));
//                showLastKnownLocation(gmap);
//                //gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,13f));
//            }
//
//
//        }
//    };
//    accelerometerSensor.start();
//
//    locationSensor.start();

    }


    private void initGoogleMap(Bundle savedInstanceState){
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);

        mapView.getMapAsync(this);

    }
    @SuppressLint("WrongConstant")
    @Override
    public void onMapReady(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        }

        gmap=map;
        //gmap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,13f));
        gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        gmap.getUiSettings().setZoomControlsEnabled(false);
        gmap.getUiSettings().setCompassEnabled(true);
        gmap.getUiSettings().setZoomGesturesEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.getUiSettings().setMapToolbarEnabled(true);
        gmap.setMyLocationEnabled(true);
        showLastKnownLocation(gmap);




    }
    private void showLastKnownLocation(GoogleMap googleMap) {
        LocationManager locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);

        LatLng latLng = null;
        if (ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }
        if (latLng == null) {
            latLng = new LatLng(42.3314, -83.0458);
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
