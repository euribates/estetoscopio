package es.euribates.estetoscopio

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import es.euribates.estetoscopio.LogEntry

class Scripts {

    static final String drop_tables = '''
Drop Table LogEntry;
'''

    static final String create_database = '''

Create Table LogEntry (
    id_logentry integer primary key autoincrement,
    ip_address char(15),
    level char(7) default 'INFO',
    timestamp char(20),
    tag Text,
    message Text
    );'''

}

class DBHelper extends SQLiteOpenHelper {
    static final String LOGTAG = 'KITTY'
    final static String Filename = 'estetoscopio.db'
    final static int Version = 2

    DBHelper(Context context) {
        super(context, Filename, null, Version)
    }

    void onUpgrade(SQLiteDatabase db, int from_version, int to_version) {
        db.execSQL(Scripts.drop_tables)
        createDatabase(db)
    }

    void onCreate(SQLiteDatabase db) {
        createDatabase(db)
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL(Scripts.create_database)
    }
}

class ORM {
    static final String LOGTAG = 'ORM'

    final static String TABLE_LOGENTRY = 'LogEntry'

    SQLiteDatabase db = null

    ORM(Context context, String mode='w') {
        DBHelper db_helper
        db_helper = new DBHelper(context)
        try {
            if (mode == 'w') {
                db = db_helper.getWritableDatabase()
            } else {
                db = db_helper.getReadableDatabase()
            }
        } catch (Exception err) {
            Log.d(LOGTAG, "ERROR: ${err}")
        }
    }

    int insert_logentry(LogEntry item) {
        ContentValues values = new ContentValues()
        values.put('level', item.level as String)
        values.put('ip_address', item.ip_address)
        String ts = item.timestamp.format("yyyy-d-M H:m:s")
        values.put('timestamp', ts)
        values.put('tag', item.tag)
        values.put('message', item.message)
        int id_logentry = db.insert(TABLE_LOGENTRY, null, values)
        return id_logentry
    }

    void deleteAll() {
        db.delete(TABLE_LOGENTRY, null, null)
    }

    int size() {
        Log.i(LOGTAG, 'ORM.size starts')
        try {
            int count = 0
            Cursor cur = db.rawQuery("select count(*) from $TABLE_LOGENTRY")
            cur.moveToFirst()
            count = cur.getInt(0)
            cur.close()
            return count
        }  catch (Exception err) {
            Log.e(LOGTAG, "ORM.size serror:$err")
        } finally {
            Log.i(LOGTAG, 'ORM.size ends')
        }
    }

    List<LogEntry> find(where=null, order_by='id_logentry desc') {
        Cursor cursor = null
        boolean pending = false
        List<LogEntry> result = []
        try {
            cursor = db.query(TABLE_LOGENTRY, null, where, null, null, null, order_by, null)
            Log.d(LOGTAG, "cursor value is ${cursor}")
            int size = cursor?.getCount()
            Log.d(LOGTAG, "Continene ${size} resultados")
            pending = cursor.moveToFirst()
            while (pending) {
                String ip_address = cursor.getString(0)
                LogEntry.Level level = LogEntry.parse_level(cursor.getString(2))
                String timestamp = cursor.getString(3)
                String tag = cursor.getString(4)
                String message = cursor.getString(5)
                Log.d(LOGTAG, "${level} ${timestamp} ${tag} ${message}")
                result.add( new LogEntry(ip_address, level, new Date(), tag, message) )
                pending = cursor.moveToNext()
            }
        } catch (Exception err) {
            Log.d(LOGTAG, "Error ${err}")
        }
        return result
    }

}

