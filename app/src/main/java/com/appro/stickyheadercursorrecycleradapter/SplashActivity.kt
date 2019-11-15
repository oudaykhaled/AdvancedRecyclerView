package com.appro.stickyheadercursorrecycleradapter

import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.loader.app.LoaderManager
import com.appro.advancedrecyclerview.OnItemClickListener
import com.appro.advancedrecyclerview.RecyclerView
import com.appro.advancedrecyclerview.SwipeListener
import com.appro.stickyheadercursorrecycleradapter.adapter.ChatModel
import com.appro.stickyheadercursorrecycleradapter.adapter.TestAdapter
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity() {
    internal lateinit var chatsRecyclerview: RecyclerView<TestAdapter>

    internal lateinit var chatModels: ArrayList<ChatModel>

    //    @BindView(R.id.recyclerView)
    //    RecyclerView<TestAdapter> chatsRecyclerview;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chatsRecyclerview = findViewById(R.id.recyclerView)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = intent.getStringExtra("Ouday")

        initDisplay()
        addDataForTesting()

        chatsRecyclerview.adapter.data = chatModels

    }


    internal fun initDisplay() {
        val adapter: TestAdapter

        adapter = TestAdapter(this, chatsRecyclerview)
        chatsRecyclerview.adapter = adapter

        chatsRecyclerview.isHorizontalScrollBarEnabled = true

        chatsRecyclerview.isNestedScrollingEnabled = true
        adapter.setOnItemClickedListener(this, R.color.colorAccent, object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Toast.makeText(this@SplashActivity, "Details Page$position", Toast.LENGTH_SHORT).show()
            }

            override fun onItemLongClick(view: View, position: Int) {

            }
        })
        chatsRecyclerview.setSwipeListener(object : SwipeListener {
            override fun onSwipe() {
                Toast.makeText(this@SplashActivity, "onSwipe", Toast.LENGTH_SHORT).show()
            }

            override fun onSwipeConnectionError() {
                Toast.makeText(this@SplashActivity, "onSwipeConnectionError", Toast.LENGTH_SHORT).show()
            }

            override fun loadMore(current_page: Int) {
                Toast.makeText(this@SplashActivity, "loadMore", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //        if (getIntent().hasExtra("showBlock")) {
        menuInflater.inflate(R.menu.menu_chat1, menu)

        //        }
        return super.onCreateOptionsMenu(menu)
    }

    internal fun addDataForTesting() {
        chatModels = ArrayList()

        for (i in 0..99) {
            for (j in 0..9) {
                chatModels.add(ChatModel.createChatModel(i.toString() + "_" + j, i.toString() + "", j.toString() + ""))
            }
        }

    }

    companion object {


        val LOADER_TYPE_CHAT = 1


        fun getTimeString(then: Long): String {
            val date = Date(then)

            val dateStr = StringBuffer()

            val calendar = Calendar.getInstance()
            calendar.time = date
            val now = Calendar.getInstance()

            val days = daysBetween(calendar.time, now.time)
            val minutes = hoursBetween(calendar.time, now.time)
            val hours = minutes / 60
            return getDate(then, "h:mm aa")
        }

        fun daysBetween(d1: Date, d2: Date): Int {
            return ((d2.time - d1.time) / DateUtils.DAY_IN_MILLIS).toInt()
        }

        fun minuteBetween(d1: Date, d2: Date): Int {
            return ((d2.time - d1.time) / DateUtils.SECOND_IN_MILLIS).toInt()
        }

        fun hoursBetween(d1: Date, d2: Date): Int {
            return ((d2.time - d1.time) / DateUtils.MINUTE_IN_MILLIS).toInt()
        }

        fun getDate(milliSeconds: Long, dateFormat: String): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }


        fun <T> initLoader(loaderId: Int, args: Bundle, callbacks: LoaderManager.LoaderCallbacks<T>,
                           loaderManager: LoaderManager) {
            val loader = loaderManager.getLoader<T>(loaderId)
            if (loader != null && loader.isReset) {
                loaderManager.restartLoader(loaderId, args, callbacks)
            } else {
                loaderManager.initLoader(loaderId, args, callbacks)
            }

        }
    }

}
