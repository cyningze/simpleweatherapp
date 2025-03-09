import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.elvishew.xlog.XLog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

class LocationRepository(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var _location: Location? = null
    private var _addedListener: Boolean = false
    private var _callback: ((Location?) -> Unit)? = null

    fun setCallback(callback: (location: Location?) -> Unit) {
        _callback = callback
    }

    @SuppressLint("MissingPermission")
    fun startLastLocation() {
        XXPermissions.with(context).permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.ACCESS_BACKGROUND_LOCATION).request { _, allGranted ->
                if (!allGranted) {
                    _location = null
                    _addedListener = false
                } else {
                    if (!_addedListener) {
                        _addedListener = true
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->
                                _location = location
                                XLog.i("addOnSuccessListener ...")
                                if (location != null) {
                                    XLog.i("addOnSuccessListener ${location.latitude} ${location.longitude}")
                                }
                                _callback?.invoke(_location)
                            }
                            .addOnFailureListener { e ->
                                XLog.e("getLastLocation", e)
                            }
                    }

                }
            }
    }


    fun getLocation(): Location? {
        return _location
    }
}