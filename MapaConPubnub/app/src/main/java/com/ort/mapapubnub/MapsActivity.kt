package com.ort.mapapubnub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //configuracion PubNub
        val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = "sub-c-5251cb74-f12b-11e9-ad72-8e6732c0d56b"
        pnConfiguration.publishKey = "pub-c-f623deb2-63eb-4608-acf4-af48ed3e31cc"
        pnConfiguration.secretKey = "true"
        val pubNub = PubNub(pnConfiguration)


        val subscribeText = findViewById<TextView>(R.id.coordenadas)
        //onCreate
        var subscribeCallback: SubscribeCallback = object : SubscribeCallback()  {
            override fun status(pubnub: PubNub, status: PNStatus) {

            }
            override fun message(pubnub: PubNub, message: PNMessageResult) {
                runOnUiThread {
                    subscribeText.text = message.message.toString()
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
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.610118, -58.385400)
        mMap.addMarker(MarkerOptions().position(sydney).title("triple o"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun dibujarPunto(coordenadas: String){
        val coords = coordenadas.split(",")
        

    }
}
