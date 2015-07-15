package es.euribates.estetoscopio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.ActionBarActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

class MainActivity extends ActionBarActivity {
    static final String LOGTAG = 'KITTY'

    private TextView lbl_caption = null
    private Button pb_test_db = null
    private Button pb_clear = null
    private BroadcastReceiver receiver

    LogAdapter adapter = null
    Mondrian4View m4v = null

    protected void onStart() {
        Log.i(LOGTAG, 'MainActivity.onStarts starts')
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            (receiver),
            new IntentFilter(LogService.NEW_RESULT)
        )
        Log.i(LOGTAG, 'MainActivity.onStarts ends')
    }

    protected void onStop() {
        Log.i(LOGTAG, 'MainActivity.onStop starts')
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
        Log.i(LOGTAG, 'MainActivity.onStop ends')
    }

    void prepare_logservice() {
        Log.i(LOGTAG, 'MainActivity.prepare_logservice starts')
        try {
            receiver = new BroadcastReceiver() {
                void onReceive(Context context, Intent intent) {
                    dataChanged()
                }
            }
            Intent intent = new Intent(this, LogService.class)
            startService(intent)
        } catch (Exception err) {
            Log.e(LOGTAG, "Error: $err")
        } finally {
            Log.i(LOGTAG, 'MainActivity.prepare_logservice ends')
        }
    }

    protected void onDestroy() {
        Log.i(LOGTAG, 'MainActivity.onDestroy starts')
        try {
            Intent intent = new Intent(this, LogService.class)
            stopService(intent)
        } catch (Exception err) {
            Log.e(LOGTAG, "Error: $err")
        } finally {
            super.onDestroy()
            Log.i(LOGTAG, 'MainActivity.onDestroy ends')
        }
    }



    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOGTAG, 'MainActivity.onCreate starts')
        try {
            super.onCreate savedInstanceState
            setContentView R.layout.activity_main

            lbl_caption = findViewById(R.id.lbl_caption)
            String ip = new Infoman(this).ipAddress
            int port = DatagramReceiver.UDP_DEFAULT_PORT
            lbl_caption.text = "Listening on ${ip}:${port}"

            pb_clear = findViewById(R.id.pb_clear)
            pb_clear.onClickListener = { view -> clearDatabase() }

            prepare_logservice()

            pb_test_db = findViewById(R.id.pb_test_db) as Button
            pb_test_db.onClickListener = { view -> testDatabase() }

            ListView lv = (ListView) findViewById(R.id.lv_items)
            Log.i(LOGTAG, "Construimos el adapatador")
            adapter = new LogAdapter<LogEntry>(this)
            Log.i(LOGTAG, 'Justo antes de definir el adaptador')
            lv.setAdapter adapter
            Log.i(LOGTAG, 'Definido el adaptador')
            m4v = findViewById(R.id.mondrian_view)
            m4v.setValues(
                adapter.count_info,
                adapter.count_warning,
                adapter.count_error,
                adapter.count_panic
            )
        } catch (Exception err) {
            Log.e(LOGTAG, "Error en MainActivity.onCreate: $err")
        }
    }

    void dataChanged() {
        adapter.notifyDataSetChanged()
        m4v.setValues(
            adapter.count_info,
            adapter.count_warning,
            adapter.count_error,
            adapter.count_panic
        )
        m4v.invalidate()
    }

    void clearDatabase() {
        Log.i(LOGTAG, 'MainActivity.clearDatabase starts')
        try {
            // do somethig
            ORM orm = new ORM(this, 'w')
            orm.deleteAll()
            dataChanged()
        } catch (Exception err) {
            Log.e(LOGTAG, "error:$err")
        } finally {
            Log.i(LOGTAG, 'MainActivity.clearDatabase ends')
        }
    }



    void testDatabase() {
        Log.i(LOGTAG, 'MainActivity.testDatebase starts')
        try {
            String preposicion = 'xxx'
            ORM orm = new ORM(this, 'w')
            LogEntry.Level.each({
                if (it == LogEntry.Level.ERROR || it == LogEntry.Level.INFO) {
                    preposicion = 'an'
                } else {
                    preposicion = 'a'
                }
                LogEntry l = new LogEntry(
                    '192.168.23.12',
                    it,
                    new Date(),
                    'TEST',
                    "This is ${->preposicion} ${it} message"
                )
                Log.d(LOGTAG, 'Created logentry ${l}')
                Log.d(LOGTAG, 'Trying to save it')
                int key = orm.insert_logentry(l)
                Log.d(LOGTAG, "Ok, saved with key ${key}")
            })
            dataChanged()
        } catch (Exception err) {
            Log.d(LOGTAG, "MainActivity.testDatebase Error: ${err}")
        } finally {
            Log.i(LOGTAG, 'MainActivity.testDatebase ends')
        }
    }

    boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}

