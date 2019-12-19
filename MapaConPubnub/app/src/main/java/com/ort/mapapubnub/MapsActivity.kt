package com.ort.mapapubnub

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import java.util.*
import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions




class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap


    val tripleOHouse = LatLng(-34.610118, -58.385400)
    val TripleO = MarkerOptions().position(tripleOHouse).title("TripleOPlace")
    val BobePlace = LatLng(-34.6625764,-58.3761079)
    val Bobe = MarkerOptions().position(BobePlace).title("La Bobe debería esta aquí")
    val circleOptions = CircleOptions().center(BobePlace).radius(20.0).fillColor(0x30ff0000).strokeWidth(2f)
    lateinit var MarcadorBobe : Marker





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity)

        // obtener el SupportMapFragment y que avise cuando esta listo para usarse
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //configuracion PubNub
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = "sub-c-5251cb74-f12b-11e9-ad72-8e6732c0d56b"
        pnConfiguration.publishKey = "pub-c-f623deb2-63eb-4608-acf4-af48ed3e31cc"
        pnConfiguration.secretKey = "true"
        val pubNub = PubNub(pnConfiguration)



        val subscribeText = findViewById<TextView>(R.id.coordenadas) //textview que muestra coordenadas

        var subscribeCallback: SubscribeCallback = object : SubscribeCallback()  {
            override fun status(pubnub: PubNub, status: PNStatus) {

            }
            override fun message(pubnub: PubNub, message: PNMessageResult) {
                runOnUiThread {
                    //recibe mensaje del canal
                    subscribeText.text = message.message.toString()
                    //dibuja las coordenadas que acaba de recibir
                    dibujarPunto(message.message.toString())
                }
            }
            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
            }
        }
        pubNub.run {
            addListener(subscribeCallback)
            subscribe()
                .channels(Arrays.asList("whiteboard")) // subscribe to channels
                .execute()
        }
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
        mMap = googleMap
        // Add a marker in triple o and move the camera
        MarcadorBobe = mMap.addMarker(Bobe)//Añade
        mMap.addMarker(TripleO)
        mMap.addCircle(circleOptions)//Rango donde deberia estar la bobe, solamente visual
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BobePlace,20f))

    }

    fun dibujarPunto(coordenadas: String){



        val coordssinsplit = coordenadas.replace('"',' ')
        val coords = coordssinsplit.split(",")

        Log.d("dibujarPunto", "latitud: " + coords[0] + " - longitud: " + coords[1])
        val LaBobeActual = LatLng(coords[0].toDouble(), coords[1].toDouble())

        val BobeAct = MarkerOptions().position(LaBobeActual).title("La Bobe Esta Aquí").icon(BitmapDescriptorFactory.fromResource(R.drawable.abuela))
        MarcadorBobe.remove()
        MarcadorBobe = mMap.addMarker(BobeAct)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LaBobeActual,20f))

        val location = Location("localizacion 1")
        location.setLatitude(BobePlace.latitude)  //latitud
        location.setLongitude(BobePlace.longitude) //longitud
        val location2 = Location("localizacion 2")
        location2.setLatitude(LaBobeActual.latitude)  //latitud
        location2.setLongitude(LaBobeActual.longitude) //longitud
        val distance = location.distanceTo(location2)

        val polylineOptions  = PolylineOptions()
            .add(BobePlace).add(LaBobeActual).color(Color.parseColor("#f44336"))

        mMap.addPolyline(polylineOptions)
        Toast.makeText(this, "La bobe esta a "+distance+" metros de su kasa", Toast.LENGTH_LONG).show()

    }
}
