package com.apps2u.stickyheadercursorrecycleradapter;

import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.apps2u.happiestrecyclerview.OnItemClickListener;
import com.apps2u.happiestrecyclerview.RecyclerView;
import com.apps2u.happiestrecyclerview.SwipeListener;
import com.apps2u.stickyheadercursorrecycleradapter.adapter.ChatModel;
import com.apps2u.stickyheadercursorrecycleradapter.adapter.TestAdapter1;
import com.apps2u.stickyheadercursorrecycleradapter.provider.ChatContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    public static final int LOADER_TYPE_CHAT = 1;
    RecyclerView<TestAdapter1> chatsRecyclerview;

//    @BindView(R.id.recyclerView)
//    RecyclerView<TestAdapter1> chatsRecyclerview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatsRecyclerview = findViewById(R.id.recyclerView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("Ouday"));

        initDisplay();
        addDataForTesting();

        initLoader(LOADER_TYPE_CHAT, null, this, getSupportLoaderManager());

        chatsRecyclerview.getAdapter().setData(chatModels);

    }


    void initDisplay() {
        TestAdapter1 adapter;

        chatsRecyclerview.setAdapter(adapter = new TestAdapter1(this, chatsRecyclerview));



//        adapter.inject(new ViewInjections(this));

//        chatsRecyclerview.setAdapter(adapter = new TestAdapter1(this, chatsRecyclerview));

//        adapter.enableMultiSelection(this, R.color.colorAccent, null, R.menu.menu_chat);

        chatsRecyclerview.setHorizontalScrollBarEnabled(true);

        chatsRecyclerview.setNestedScrollingEnabled(true);
        adapter.setOnItemClickedListener(this, R.color.colorAccent, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, "Details Page" + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        chatsRecyclerview.setSwipeListener(new SwipeListener() {
            @Override
            public void onSwipe() {
                Toast.makeText(MainActivity.this, "onSwipe", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeConnectionError() {
                Toast.makeText(MainActivity.this, "onSwipeConnectionError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void loadMore(int current_page) {
                Toast.makeText(MainActivity.this, "loadMore", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (getIntent().hasExtra("showBlock")) {
        getMenuInflater().inflate(R.menu.menu_chat1, menu);

//        }
        return super.onCreateOptionsMenu(menu);
    }

    ArrayList<ChatModel> chatModels;

    void addDataForTesting() {
        chatModels = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 10; j++) {
//                ChatContract.insertChat(ChatModel.createChatModel(i + "_" + j, i + "", j + ""), this);
                chatModels.add(ChatModel.createChatModel(i + "_" + j, i + "", j + ""));
            }
        }

    }


    public static String getTimeString(long then) {
        Date date = new Date(then);

        StringBuffer dateStr = new StringBuffer();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar now = Calendar.getInstance();

        int days = daysBetween(calendar.getTime(), now.getTime());
        int minutes = hoursBetween(calendar.getTime(), now.getTime());
        int hours = minutes / 60;
        return getDate(then, "h:mm aa").toString();
    }

    public static int daysBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.DAY_IN_MILLIS);
    }

    public static int minuteBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.SECOND_IN_MILLIS);
    }

    public static int hoursBetween(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / DateUtils.MINUTE_IN_MILLIS);
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    public static <T> void initLoader(final int loaderId, final Bundle args, final LoaderManager.LoaderCallbacks<T> callbacks,
                                      final LoaderManager loaderManager) {
        final Loader<T> loader = loaderManager.getLoader(loaderId);
        if (loader != null && loader.isReset()) {
            loaderManager.restartLoader(loaderId, args, callbacks);
        } else {
            loaderManager.initLoader(loaderId, args, callbacks);
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] PROJECTION = new String[]{"*"};
        String select = null;
        switch (id) {
            case LOADER_TYPE_CHAT:
                PROJECTION = new String[]{"*"};
                select = null;
                return new CursorLoader(this, ChatContract.Entry.CONTENT_URI, PROJECTION, select, null,
                        ChatContract.Entry.COLUMN_NAME_CHAT_CREATED + " ASC");


            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) return;
        if (loader.getId() == LOADER_TYPE_CHAT) {
            if (data.getCount() == 0) {
                // TODO: 2/22/2018 no data available now
            } else {
//                chatsRecyclerview.getAdapter().swapCursor(data);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_TYPE_CHAT) {
//           chatsRecyclerview.getAdapter().swapCursor(null);
        }
    }
}
