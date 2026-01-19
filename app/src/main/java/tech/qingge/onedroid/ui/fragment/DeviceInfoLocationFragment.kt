package tech.qingge.onedroid.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.qingge.onedroid.R
import tech.qingge.onedroid.data.entity.CardData
import tech.qingge.onedroid.util.LogUtil
import javax.inject.Inject


@AndroidEntryPoint
class DeviceInfoLocationFragment @Inject constructor() : DeviceInfoBaseFragment() {

    override fun getNeedPermissions(): List<String> {
        return mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }


    override suspend fun initCardDataList(): List<CardData> {
        return mutableListOf(
            CardData(getString(R.string.satellite_info), getSatelliteInfo()),
            CardData(getString(R.string.location_info), getLocationInfo()),
        )
    }

    @SuppressLint("NewApi")
    private fun updateSatelliteInfo(status: GnssStatus) {
        LogUtil.d("updateSatelliteInfo:$status")
        cardDataList[0].pairs = mutableListOf<Pair<String, String>>(
            Pair(getString(R.string.satellite_count), status.satelliteCount.toString()),
            Pair(getString(R.string.satellite_detail), getSatelliteDetail(status))
        )
        binding.rv.adapter!!.notifyItemChanged(0)
    }

    @SuppressLint("NewApi")
    private fun getSatelliteDetail(status: GnssStatus): String {
        val sb = StringBuilder()
        for (i in 0..(status.satelliteCount - 1)) {
            sb.append(getString(R.string.satellite_index)).append(i).append("\n")
            sb.append(getString(R.string.satellite_cn0_dbhz)).append(status.getCn0DbHz(i)).append("\n")
            sb.append(getString(R.string.satellite_svid)).append(status.getSvid(i)).append("\n")
            sb.append(getString(R.string.satellite_azimuth_degrees)).append(status.getAzimuthDegrees(i)).append("\n")
            sb.append(getString(R.string.satellite_elevation_degrees)).append(status.getElevationDegrees(i)).append("\n")
            sb.append(getString(R.string.satellite_baseband_cn0_dbhz)).append(status.getBasebandCn0DbHz(i)).append("\n")
            sb.append(getString(R.string.satellite_carrier_frequency_hz)).append(status.getCarrierFrequencyHz(i)).append("\n")
            sb.append(getString(R.string.satellite_constellation_type)).append(status.getConstellationType(i)).append("\n")
            sb.append("\n")
        }

        return sb.toString()

    }

    @SuppressLint("MissingPermission")
    private fun getSatelliteInfo(): MutableList<Pair<String, String>> {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val gnssStatusListener = object : GnssStatus.Callback() {
                    override fun onSatelliteStatusChanged(status: GnssStatus) {
                        updateSatelliteInfo(status)
//                        var satelliteCount = 0
//                        for (i in 0 until status.satelliteCount) {
//                            if (status.getCn0DbHz(i) > 0) { // CN0 是信号强度
//                                satelliteCount++
//                            }
//                        }
                    }

                }
                locationManager.registerGnssStatusCallback(
                    gnssStatusListener,
                    Handler(Looper.getMainLooper())
                )
            } else {
                val gpsStatusListener = object : GpsStatus.Listener {
                    override fun onGpsStatusChanged(event: Int) {
                        if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
                            val satellites = locationManager.getGpsStatus(null)
                            //TODO

//                            var satelliteCount = 0
//                            satellites?.let {
//                                val iterator = it.satellites.iterator()
//                                while (iterator.hasNext()) {
//                                    val satellite = iterator.next()
//                                    satelliteCount++
//                                }
//                            }
                        }
                    }
                }

                locationManager.addGpsStatusListener(gpsStatusListener)
            }
        }

        return mutableListOf(
            Pair(getString(R.string.satellite_count), getString(R.string.unknown))
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocationInfo(): MutableList<Pair<String, String>> {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        lifecycleScope.launch {

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L,
                10f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        updateLocation(location)
                    }

                })
        }
        return mutableListOf(
            Pair(getString(R.string.location_latitude), getString(R.string.unknown)),
            Pair(getString(R.string.location_longitude), getString(R.string.unknown)),
            Pair(getString(R.string.location_altitude), getString(R.string.unknown)),
            Pair(getString(R.string.location_time), getString(R.string.unknown)),
            Pair(getString(R.string.location_extras), getString(R.string.unknown)),
            Pair(getString(R.string.location_accuracy), getString(R.string.unknown)),
            Pair(getString(R.string.location_bearing), getString(R.string.unknown)),
            Pair(getString(R.string.location_speed), getString(R.string.unknown)),
        )

    }

    private fun updateLocation(location: Location) {
        LogUtil.d("onNewLocation:$location")
        cardDataList[1].pairs = mutableListOf<Pair<String, String>>(
            Pair(getString(R.string.location_latitude), location.latitude.toString()),
            Pair(getString(R.string.location_longitude), location.longitude.toString()),
            Pair(getString(R.string.location_altitude), location.altitude.toString()),
            Pair(getString(R.string.location_time), location.time.toString()),
            Pair(getString(R.string.location_extras), location.extras.toString()),
            Pair(getString(R.string.location_accuracy), location.accuracy.toString()),
            Pair(getString(R.string.location_bearing), location.bearing.toString()),
            Pair(getString(R.string.location_speed), location.speed.toString()),
        )
        binding.rv.adapter!!.notifyItemChanged(1)
    }

}