package com.jahnavimareddi.totrick

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date
import kotlin.concurrent.thread

class AddActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "totrickdb"
        ).build()
        val taskDao = db.taskDao()

        val editText : EditText = findViewById(R.id.editText)
        val addButton : Button = findViewById(R.id.addButton)
        val deleteButton: FloatingActionButton = findViewById(R.id.deleteButton)
        val heading: TextView = findViewById(R.id.heading)

        val bundle = intent.getBundleExtra("taskBundle")
        if (bundle != null) {
            val task = Task(bundle.getLong("id"), bundle.getString("title").toString(), bundle.getBoolean("isChecked"), bundle.getLong("timeCreated"))
            editText.setText(task.title)
            heading.text = "Edit Task"
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                thread {
                    taskDao.deleteTask(task)
                    runOnUiThread {
                        Toast.makeText(this,"This task has been deleted", Toast.LENGTH_SHORT).show()
                        this.finish()
                    }
                }
            }
            addButton.text = "Save"
            addButton.setOnClickListener {
                task.title = editText.text.toString()
                thread {
                    taskDao.updateTask(task)
                    runOnUiThread {
                        Toast.makeText(this,"This task has been edited", Toast.LENGTH_SHORT).show()
                        this.finish()
                    }
                }
            }

        } else {
            addButton.setOnClickListener {
                val title = editText.text.toString()
                val newTask = Task(0, title,false, Date().time)
                thread {
                    taskDao.insertTask(newTask)
                    runOnUiThread{
                        editText.setText("")
                        Toast.makeText(this,"This task has been added",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}