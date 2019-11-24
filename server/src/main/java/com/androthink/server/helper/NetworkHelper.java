package com.androthink.server.helper;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

public class NetworkHelper {

    public class InterfaceName {
        public static final String W_LAN_0 = "wlan0";
        public static final String ETH_0 = "eth0";
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    @NonNull
    public static String getMACAddress(String interfaceName) throws SocketException {
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface netInterface : interfaces) {
            if (interfaceName != null) {
                if (!netInterface.getName().equalsIgnoreCase(interfaceName)) continue;
            }
            byte[] mac = netInterface.getHardwareAddress();
            if (mac == null) return "";
            StringBuilder buf = new StringBuilder();
            for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
            if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
            return buf.toString();
        }

        return "";
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) throws SocketException{
        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface netInterface : interfaces) {
            List<InetAddress> addressList = Collections.list(netInterface.getInetAddresses());
            for (InetAddress address : addressList) {
                if (!address.isLoopbackAddress()) {
                    return getIp(address,useIPv4);
                }
            }
        }
        return "";
    }

    public static String getIp(@NonNull InetAddress address,boolean useIPv4){
        String addressString = address.getHostAddress();

        boolean isIPv4 = addressString.indexOf(':') < 0;
        if (useIPv4) {
            if (isIPv4)
                return addressString;
        } else {
            if (!isIPv4) {
                int delim = addressString.indexOf('%'); // drop ip6 zone suffix
                return delim < 0 ? addressString.toUpperCase() : addressString.substring(0, delim).toUpperCase();
            }
        }

        return "";
    }
}
