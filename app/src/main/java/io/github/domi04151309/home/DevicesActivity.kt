package io.github.domi04151309.home

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.*
import org.json.JSONException
import java.util.*
import androidx.appcompat.app.AlertDialog
import android.view.View

class DevicesActivity : AppCompatActivity() {

    private var devices: Devices? = null
    private var listView: ListView? = null
    private var reset = true

    override fun onCreate(savedInstanceState: Bundle?) {
        Theme.set(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        devices = Devices(PreferenceManager.getDefaultSharedPreferences(this))
        listView = findViewById<View>(R.id.listView) as ListView

        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, view, _, _ ->
            val action =  view.findViewById<TextView>(R.id.hidden).text
            val titleTxt = view.findViewById<TextView>(R.id.title).text.toString()

            if (action == "edit") {
                reset = true
                startActivity(Intent(this, EditDeviceActivity::class.java).putExtra("Device", titleTxt))
            } else if (action == "add") {
                reset = true
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.pref_add_method)
                builder.setItems(resources.getStringArray(R.array.pref_add_method_array)) { _, which ->
                    if (which == 0) {
                        startActivity(Intent(this, EditDeviceActivity::class.java))
                    } else if (which == 1) {
                        startActivity(Intent(this, SearchDevicesActivity::class.java))
                    }
                }
                builder.show()
            }
        }
    }

    private fun loadDevices(){
        var titles: Array<String?>
        var summaries: Array<String?>
        var actions: Array<String?>
        var drawables: IntArray
        var i = 0
        try {
            if (devices!!.length() == 0) {
                titles = arrayOfNulls(2)
                summaries = arrayOfNulls(2)
                actions = arrayOfNulls(2)
                drawables = IntArray(2)
                titles[i] = resources.getString(R.string.main_no_devices)
                summaries[i] = ""
                actions[i] = "none"
                i++
            } else {
                val count = devices!!.length()
                titles = arrayOfNulls(count + 1)
                summaries = arrayOfNulls(count + 1)
                actions = arrayOfNulls(count + 1)
                drawables = IntArray(count + 1)
                while (i < count) {
                    try {
                        val name = devices!!.getName(i)
                        titles[i] = name
                        summaries[i] = devices!!.getAddress(name)
                        actions[i] = "edit"
                        drawables[i] = devices!!.getIconId(name)
                    } catch (e: JSONException) {
                        Log.e(Global.LOG_TAG, e.toString())
                    }
                    i++
                }
            }
            titles[i] = resources.getString(R.string.pref_add)
            summaries[i] = resources.getString(R.string.pref_add_summary)
            actions[i] = "add"
            drawables[i] = R.drawable.ic_add
        } catch (e: Exception) {
            titles = arrayOfNulls(1)
            summaries = arrayOfNulls(1)
            actions = arrayOfNulls(1)
            drawables = IntArray(1)
            titles[i] = resources.getString(R.string.err_wrong_format)
            summaries[i] = resources.getString(R.string.err_wrong_format_summary)
            actions[i] = "none"
            drawables[i] = R.drawable.ic_warning
            Log.e(Global.LOG_TAG, e.toString())
        }
        Log.d(Global.LOG_TAG, Arrays.toString(titles) + Arrays.toString(summaries))

        val adapter = ListAdapter(this, titles, summaries, actions, drawables)
        listView!!.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (reset){
            loadDevices()
            reset = false
        }
    }
}
