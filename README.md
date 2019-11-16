# AdvancedRecyclerView
A recyclerview that provides the most important features such as pull down to refresh, load more, sticky header and banner ads

Usage:

1- Add the following code to you xml file

<com.appro.advancedrecyclerview.RecyclerView
        android:id="@+id/advancedRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:isRefresh = "true"
        app:isLoadMore = "true" />

2- Create your own RecyclerViewAdapter that extends RecyclerViewAdapter, below is a sample code


class TestAdapter(context: Activity, recyclerView: RecyclerView<*>) : RecyclerViewAdapter<ViewHolder, ChatModel>(context, recyclerView) {

    override val headerLayout: Int
        get() = R.layout.header_text_date

    override val footerLayout: Int
        get() = R.layout.test_footer

    override val isStickyHeader: Boolean
        get() = true

    override val orientation: Int
        get() = RecyclerViewAdapter.Companion.VERTICAL

    override val isSection: Boolean
        get() = true

    override fun attachAlwaysLastHeader(): Boolean {
        return false
    }


    override fun onBindViewHolders(viewHolder: ViewHolder, position: Int) {
        when (getItemType(position)) {
            TEXTTYPE -> {
                val viewHolderText = viewHolder as ViewHolderText
                viewHolderText.text.text = data!![position].message
                viewHolderText.date.text = data!![position].dateOfCreation
            }
        }
    }

    override fun onCreateViewHolders(parent: ViewGroup, viewType: Int): ViewHolder? {
        val layoutInflater = LayoutInflater.from(parent.context)
        var v: View? = null
        when (viewType) {
            TEXTTYPE -> {
                v = layoutInflater.inflate(R.layout.bubble_text_left, parent, false)
                return ViewHolderText(v)
            }

            else -> return null
        }
    }

    override fun getItemType(position: Int): Int {
        return TEXTTYPE

    }

    override fun getSectionCondition(position: Int): String {
        return data!![position].chatID
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return 0
    }

    companion object {


        internal val TEXTTYPE = 1
    }


}


