package com.example.aula_aplicativo_localizacao


import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.net.InetAddress
import java.security.KeyStore
import java.util.*


//Extender a classe GoogleMap.OnMarkerClickListener e implementar o método com false
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap

    //Acrecentar
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastlocation: Location

    //Variaveis para atualizar localização
    private lateinit var locationCallback:  LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        //Variaveis para atualizar localização
        private const val  REQUEST_CHECK_SETTINGS = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Acrescentar
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        //Exemplo inicial
        // Add a marker in Sydney and move the camera
        //val myPlace = LatLng(40.73, -73.99)
        //map.addMarker(MarkerOptions().position(myPlace).title("Nova York"))
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace,12.0f))

        //Responsável pelos sinais de zoom
        map.uiSettings.isZoomControlsEnabled = true
        //exibe dois icones
        map.setOnMarkerClickListener(this)
        setUpMap()
    }




    private fun setUpMap() {

        //Verifica se existe permissão para fazer a localização senão solicita
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE) }

        //Habilita para encontrar a localização
        map.isMyLocationEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastlocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                //Funcao que gerar um balão na localização
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12.0f))
            }
        }
    }



    //Criar um marcador(balão vermelho) no map
    private fun placeMarkerOnMap(location: LatLng){
        val markeroptions = MarkerOptions().position(location)
        //Pega o endereço e exibe no balão
        markeroptions.title(getAddress(location))
        map.addMarker(markeroptions)
    }

    //Exibir o endereço no marcador
    private fun getAddress (latLng: LatLng): String{
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(this, Locale.getDefault())
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude,1)
        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        return address
    }

    override fun onMarkerClick(p0: Marker?) = false

}

