package com.jahnavimareddi.totrick

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val date: TextView = findViewById(R.id.date)
        date.text = SimpleDateFormat("dd/M/yyyy", Locale.getDefault()).format(Date())

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }

        val recyclerview: RecyclerView = findViewById(R.id.recyclerview)
        recyclerview.layoutManager= LinearLayoutManager(this)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "totrickdb"
        ).build()
        val taskDao = db.taskDao()
        thread {
            val data = taskDao.getAll()
            runOnUiThread {
                recyclerview.adapter = RecyclerViewAdapter(data,taskDao)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "totrickdb"
        ).build()
        val taskDao = db.taskDao()
        val recyclerview:RecyclerView = findViewById(R.id.recyclerview)
        recyclerview.layoutManager= LinearLayoutManager(this)
        thread {
            val data = taskDao.getAll()
            runOnUiThread {
                recyclerview.adapter = RecyclerViewAdapter(data,taskDao)
            }
        }
    }
}

class RecyclerViewAdapter(private var data: List<Task>, private val taskDao: TaskDao) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkbox: CheckBox = view.findViewById(R.id.taskCheckbox)
        var title: TextView = view.findViewById(R.id.taskTitle)
        val context: Context = view.context
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.each_list_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.title.text = data[position].title
        viewHolder.checkbox.isChecked = data[position].isChecked
        viewHolder.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            data[position].isChecked = b
            thread {
                taskDao.updateTask(data[position])
            }
        }
        viewHolder.title.setOnClickListener {
            val intent = Intent(viewHolder.context, AddActivity::class.java)
            val bundle = Bundle()
            bundle.putLong("id", data[position].id)
            bundle.putString("title", data[position].title)
            bundle.putBoolean("isChecked", data[position].isChecked)
            bundle.putLong("timeCreated", data[position].timeCreated)
            intent.putExtra("taskBundle", bundle)
            viewHolder.context.startActivity(intent)
        }
    }
    override fun getItemCount() = data.size
}

