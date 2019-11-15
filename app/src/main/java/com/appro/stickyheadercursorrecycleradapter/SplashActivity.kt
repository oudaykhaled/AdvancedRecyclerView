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
        menuInflater.inflate(R.menu.menu_chat1, menu)
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

}
