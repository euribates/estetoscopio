package es.euribates.estetoscopio

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

class LogService extends Service {
    static final String LOGTAG = 'KITTY'
    private int counter = 10
    LocalBroadcastManager broadcaster = null

    static final public String NEW_RESULT = "es.euribates.estetoscopio.service.NEW_RESULT"

    void sendResult(String ip_address, String message) {
        Log.i(LOGTAG, 'LogService.sendResult starts')
        try {
            ORM orm = new ORM(this, 'w')
            LogEntry l = new LogEntry(ip_address, message)
            Log.i(LOGTAG, "logentry l vale $l")
            orm.insert_logentry(l)
            Intent intent = new Intent(NEW_RESULT)
            broadcaster.sendBroadcast(intent)
        } catch (Exception err) {
            Log.e(LOGTAG, "Error: $err")
        } finally {
            Log.i(LOGTAG, 'LogService.sendResult ends')
        }
    }

    void onCreate() {
        Log.i(LOGTAG, 'LogService.onCreate starts')
        try {
            broadcaster = LocalBroadcastManager.getInstance(this)
            DatagramReceiver server = new DatagramReceiver()
            server.reaction = { ip, msg -> sendResult(ip, msg) }
            server.start()
        } catch (Exception err) {
            Log.e(LOGTAG, "Error $err")
        } finally {
            Log.i(LOGTAG, "LogService.onCreate ends")
        }
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null
    }
}
