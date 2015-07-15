package es.euribates.estetoscopio

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Created by juan on 8/7/15.
 */

public class LogAdapter extends BaseAdapter {
    String LOGTAG = 'KITTY'

    ORM source = null
    LayoutInflater layoutInflater

    int count_info = 0
    int back_info_color = 0
    int count_warning = 0
    int back_warning_color = 0
    int count_error = 0
    int back_error_color = 0
    int count_panic = 0
    int back_panic_color = 0

    private List<LogEntry> buff = null

    void calculate_stat() {
        count_info = 0
        count_warning = 0
        count_error = 0
        count_panic = 0
        buff.each {
            switch (it.level) {
                case LogEntry.Level.INFO: count_info++; break
                case LogEntry.Level.WARNING: count_warning++; break
                case LogEntry.Level.ERROR: count_error++; break
                case LogEntry.Level.PANIC: count_panic++; break
            }
        }
    }

    public LogAdapter(Context context) {
        Log.i(LOGTAG, "Creation on LogAdapter")
        try {
            Resources res = context.getResources()
            back_info_color = res?.getColor(R.color.back_info)
            back_warning_color = res?.getColor(R.color.back_warning)
            back_error_color = res?.getColor(R.color.back_error)
            back_panic_color = res?.getColor(R.color.back_panic)

            this.layoutInflater = LayoutInflater.from(context)
            this.source = new ORM(context, 'r')
            buff = source.find()
            calculate_stat()
        } catch (Exception err) {
            Log.e(LOGTAG, "error:$err")
        } finally {
            Log.i(LOGTAG, "LogAdapter created")
        }
    }

    @Override
    int getCount() {
        Log.i(LOGTAG, "getCount starts")
        int result = 0
        try {
            result = source.size()
            if (result != buff.size()) {
                buff = source.find()
                calculate_stat()
            }
            return result
        } catch (Exception err) {
            Log.e(LOGTAG, "Error ${err}")
        } finally {
            Log.i(LOGTAG, 'getCount ends')
        }
    }

    @Override
    Object getItem(int i) {
        // Log.d(LOGTAG, "getItem(${i}) starts")
        return buff[i]
        }

    @Override
    long getItemId(int i) {
        // Log.d(LOGTAG, "getItemId(${i}) starts")
        return i
        }

    @Override
    public View getView(int i, View log_item_view, ViewGroup group_view) {
        def contenedor = [:]
        View result
        // Log.d(LOGTAG, "LogAdapter.getView(${i}, ${log_item_view}, ${group_view}) starts")
        if (log_item_view == null) {
            result = layoutInflater.inflate(R.layout.log_item_view, null)
            contenedor['img']= result.findViewById(R.id.img_level) as ImageView
            contenedor['txt']= result.findViewById(R.id.txt_message) as TextView
            contenedor['tag'] = result.findViewById(R.id.lbl_tag) as TextView
            contenedor['ll'] = result.findViewById(R.id.ll_logentry) as LinearLayout
            result.tag = contenedor
            }
        else {
            result = log_item_view
            contenedor.putAll(log_item_view.tag)
            }
        LogEntry l = getItem(i) as LogEntry
        LinearLayout ll = (contenedor['ll'] as LinearLayout)
        switch (l.level) {
            case LogEntry.Level.INFO:
                contenedor['img'].setImageResource(R.drawable.info)
                ll.setBackgroundColor(back_info_color)
                break
            case LogEntry.Level.WARNING:
                contenedor['img'].setImageResource(R.drawable.warning)
                ll.setBackgroundColor(back_warning_color)
                break
            case LogEntry.Level.ERROR:
                contenedor['img'].setImageResource(R.drawable.error)
                ll.setBackgroundColor(back_error_color)
                break
            default:
                contenedor['img'].setImageResource(R.drawable.panic)
                ll.setBackgroundColor(back_panic_color)
            }
        contenedor['txt'].setText(l.message)
        contenedor['tag'].setText(l.tag)
        return result
        }
    }
