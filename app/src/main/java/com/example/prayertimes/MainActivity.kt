package com.example.prayertimes

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.azan.*
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.azan.astrologicalCalc.Utils
import java.lang.System.currentTimeMillis
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
    var latitude_value = 53.39861110
    var longitude_value = -6.40055560

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //    prayer time calculation
        val today = SimpleDate(GregorianCalendar())

        //the method call for location based prayer times
        val location = Location(
            degreeLat = latitude_value,
            degreeLong = longitude_value,
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
            offset = true,
            12.0,
            0.0,
            0.0,
            0.0,
            0.0,
            -13.0
        )

        val azan = Azan(location, ICC_ireland)
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

        if(System_time == fajr || System_time == zuhar || System_time == asar || System_time == maghrib || System_time == ishaa){
            startAlert()
        }

    }


    fun startAlert() {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this.applicationContext,
            234324243,
            intent,
            0)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[RTC_WAKEUP, currentTimeMillis()] = pendingIntent
        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show()
    }

}


