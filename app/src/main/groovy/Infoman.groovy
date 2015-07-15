package es.euribates.estetoscopio

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.util.Log

import java.nio.ByteOrder

/**
 * Created by juan on 13/7/15.
 */
class Infoman {

    Context context = null
    WifiManager wifiMgr = null

    Infoman(Context context) {
        this.context = context
    }

    String getIpAddress() {
        wifiMgr = context.getSystemService(context.WIFI_SERVICE)
        int ipAddress = wifiMgr.getConnectionInfo().getIpAddress()
        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }
        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray()
        String ipAddressString
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress()
        } catch (UnknownHostException ex) {
            Log.e("INFOMAN", "Unable to get host address")
            ipAddressString = "Unable to get host address"
        }
        return ipAddressString
    }
}
