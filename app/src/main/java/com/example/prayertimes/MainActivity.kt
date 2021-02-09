package com.example.prayertimes

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.azan.*
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.azan.astrologicalCalc.Utils
import com.google.android.gms.location.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate = LocalDate.now()

    //variables for longitude and latitude
        var latitudeValue = 0.00
        var longitudeValue = 0.00


    //location permission variables
    var PERMISSION_ID = 11
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //call the location finder function
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //    prayer time calculation
        val today = SimpleDate(GregorianCalendar())

        getLastLocation()
        //the method call for location based prayer times
        val location1 = Location(
            degreeLat = latitudeValue,
            degreeLong = longitudeValue,
            gmtDiff = 0.0,
            dst = 0
        )

        //the method call for prayer times calculation
        val ICC_ireland = Method(
            18.0,
            17.0,
            Utils.DEF_IMSAAK_ANGLE,
            0,
            0,
            0,
            Rounding.SPECIAL,
            Madhhab.SHAAFI,
            Utils.DEF_NEAREST_LATITUDE,
            ExtremeLatitude.GOOD_INVALID,
            offset = false,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0
        )

        val azan = Azan(location1, ICC_ireland)
        val prayerTimes = azan.getPrayerTimes(today)

        //display variables
        val fajr_time: TextView = findViewById(R.id.Fajr_time)
        val zuhar_time: TextView = findViewById(R.id.Zuhar_time)
        val asar_time: TextView = findViewById(R.id.Asar_time)
        val maghrib_time: TextView = findViewById(R.id.Maghrib_time)
        val ishaa_time: TextView = findViewById(R.id.Ishaa_time)


        //prayer times
        fajr_time.text = prayerTimes.fajr().toString()

        zuhar_time.text = prayerTimes.thuhr().toString()

        asar_time.text = prayerTimes.assr().toString()

        maghrib_time.text = prayerTimes.maghrib().toString()

        ishaa_time.text = prayerTimes.ishaa().toString()

        //date
        val date: TextView = findViewById(R.id.editTextDate)

        date.text = currentDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)).toString()

        //hijri date
        val G_date = Date() // Gregorian date


        val cl = Calendar.getInstance()
        cl.time = G_date

        val Hijri_date: TextView = findViewById(R.id.editTextHijriDate)
        val islamyDate: HijrahDate =
            HijrahChronology.INSTANCE.date(LocalDate.of(cl[Calendar.YEAR], cl[Calendar.MONTH] + 1,
                cl[Calendar.DATE]))

        Hijri_date.text = islamyDate.toString()
        //current time
        val currentTime = LocalTime.now()
        var System_time = currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM))

        var fajr = prayerTimes.fajr().toString()
        var zuhar = prayerTimes.thuhr().toString()
        var asar = prayerTimes.assr().toString()
        var maghrib = prayerTimes.maghrib().toString()
        var ishaa = prayerTimes.ishaa().toString()

//        if(System_time == fajr || System_time == zuhar || System_time == asar || System_time == maghrib || System_time == ishaa){
//            startAlert()
//        }




    }


//    fun startAlert() {
//        val intent = Intent(this, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this.applicationContext,
//            234324243,
//            intent,
//            0)
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        alarmManager[RTC_WAKEUP, currentTimeMillis()] = pendingIntent
//        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show()
//    }



    //--------------------------------------------------------------------------------------------
    //location system implementation

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: android.location.Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
//-----------------------------------------------------------------------------------------------
    //error in value assignment of negative values.
                        latitudeValue=location.latitude
                        longitudeValue=location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: android.location.Location? = locationResult.lastLocation
            if (mLastLocation != null) {
                latitudeValue = mLastLocation.latitude
            }
            if (mLastLocation != null) {
                longitudeValue = mLastLocation.longitude
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

}


