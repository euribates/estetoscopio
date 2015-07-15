package es.euribates.estetoscopio

import android.util.Log;

/**
 * Created by juan on 9/7/15.
 */

class DatagramReceiver extends Thread {
    String LOGTAG = 'KITTY'

    static final int UDP_DEFAULT_PORT = 31416
    static final int MAX_UDP_DATAGRAM_LEN = 512

    int port = UDP_DEFAULT_PORT
    boolean running = false
    String message = ""
    Closure reaction = { ip, msg -> Log.d(LOGTAG, ip + ':' + msg) }

    void run() {
        byte[] buff = new byte[MAX_UDP_DATAGRAM_LEN];
        def packet = new DatagramPacket(buff, buff.length)
        DatagramSocket socket = null
        int count = 0
        try {
            InetAddress addr = InetAddress.getByAddress([0,0,0,0] as byte[])
            socket = new DatagramSocket(port, addr)
            socket.setSoTimeout(60000)
            Log.d(LOGTAG, "Listening on ${addr}:${port}")
            running = true
            while (running) {
                try {
                    socket.receive(packet)
                    String ip_address = packet.address.hostAddress
                    String message = new String(buff, 0, packet.getLength())
                    reaction(ip_address, message)
                    Log.d(LOGTAG, message)
                    }
                catch (InterruptedIOException err) {
                    count++
                    Log.d(LOGTAG, "${count} Alive and kicking!")
                    Log.e(LOGTAG, "error: $err")
                    }
                catch (Exception err) {
                    Log.e(LOGTAG, "error: $err")
                    }
                }
            }
        catch (Throwable e) {
            Log.d(LOGTAG, e.toString())
            }
        running = false
        socket?.close()
    }

}


