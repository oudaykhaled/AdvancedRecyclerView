package com.appro.advancedrecyclerview.swipe

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.appro.advancedrecyclerview.R
import java.util.*

/**
 * Created by Ouday Khaled on 5/23/2018.
 */

class SwipeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private val mTouchSlop: Int

    private var mCurrentDragEdge: DragEdge? = DefaultDragEdge
    private val mDragHelper: ViewDragHelper

    private var mDragDistance = 0
    private val mDragEdges = LinkedHashMap<DragEdge, View?>()
    private var mShowMode: ShowMode? = null

    private val mEdgeSwipesOffset = FloatArray(4)

    private val mSwipeListeners = ArrayList<SwipeLayout.SwipeListener>()
    private val mSwipeDeniers = ArrayList<SwipeLayout.SwipeDenier>()
    private val mRevealListeners = HashMap<View, ArrayList<SwipeLayout.OnRevealListener>>()
    private val mShowEntirely = HashMap<View, Boolean>()

    private var mDoubleClickListener: SwipeLayout.DoubleClickListener? = null

    var isSwipeEnabled = true
    private val mSwipesEnabled = booleanArrayOf(true, true, true, true)
    var isClickToClose = false

    private val mDragHelperCallback = object : ViewDragHelper.Callback() {

        internal var isCloseBeforeDrag = true

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            if (child === surfaceView) {
                when (mCurrentDragEdge) {
                    DragEdge.Top, DragEdge.Bottom -> return paddingLeft
                    DragEdge.Left -> {
                        if (left < paddingLeft) return paddingLeft
                        if (left > paddingLeft + mDragDistance)
                            return paddingLeft + mDragDistance
                    }
                    DragEdge.Right -> {
                        if (left > paddingLeft) return paddingLeft
                        if (left < paddingLeft - mDragDistance)
                            return paddingLeft - mDragDistance
                    }
                }
            } else if (currentBottomView === child) {

                when (mCurrentDragEdge) {
                    DragEdge.Top, DragEdge.Bottom -> return paddingLeft
                    DragEdge.Left -> if (mShowMode == ShowMode.PullOut) {
                        if (left > paddingLeft) return paddingLeft
                    }
                    DragEdge.Right -> if (mShowMode == ShowMode.PullOut) {
                        if (left < measuredWidth - mDragDistance) {
                            return measuredWidth - mDragDistance
                        }
                    }
                }
            }
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if (child === surfaceView) {
                when (mCurrentDragEdge) {
                    DragEdge.Left, DragEdge.Right -> return paddingTop
                    DragEdge.Top -> {
                        if (top < paddingTop) return paddingTop
                        if (top > paddingTop + mDragDistance)
                            return paddingTop + mDragDistance
                    }
                    DragEdge.Bottom -> {
                        if (top < paddingTop - mDragDistance) {
                            return paddingTop - mDragDistance
                        }
                        if (top > paddingTop) {
                            return paddingTop
                        }
                    }
                }
            } else {
                val surfaceView = surfaceView
                val surfaceViewTop = surfaceView?.top ?: 0
                when (mCurrentDragEdge) {
                    DragEdge.Left, DragEdge.Right -> return paddingTop
                    DragEdge.Top -> if (mShowMode == ShowMode.PullOut) {
                        if (top > paddingTop) return paddingTop
                    } else {
                        if (surfaceViewTop + dy < paddingTop)
                            return paddingTop
                        if (surfaceViewTop + dy > paddingTop + mDragDistance)
                            return paddingTop + mDragDistance
                    }
                    DragEdge.Bottom -> if (mShowMode == ShowMode.PullOut) {
                        if (top < measuredHeight - mDragDistance)
                            return measuredHeight - mDragDistance
                    } else {
                        if (surfaceViewTop + dy >= paddingTop)
                            return paddingTop
                        if (surfaceViewTop + dy <= paddingTop - mDragDistance)
                            return paddingTop - mDragDistance
                    }
                }
            }
            return top
        }

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            val result = child === surfaceView || bottomViews.contains(child)
            if (result) {
                isCloseBeforeDrag = openStatus == SwipeLayout.Status.Close
            }
            return result
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return mDragDistance
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return mDragDistance
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            for (l in mSwipeListeners) {
                l.onHandRelease(this@SwipeLayout, xvel, yvel)
            }
            processHandRelease(xvel, yvel, isCloseBeforeDrag)

            invalidate()
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            val surfaceView = surfaceView ?: return
            val currentBottomView = currentBottomView
            val evLeft = surfaceView.left
            val evRight = surfaceView.right
            val evTop = surfaceView.top
            val evBottom = surfaceView.bottom
            if (changedView === surfaceView) {

                if (mShowMode == ShowMode.PullOut && currentBottomView != null) {
                    if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                        currentBottomView.offsetLeftAndRight(dx)
                    } else {
                        currentBottomView.offsetTopAndBottom(dy)
                    }
                }

            } else if (bottomViews.contains(changedView)) {

                if (mShowMode == ShowMode.PullOut) {
                    surfaceView.offsetLeftAndRight(dx)
                    surfaceView.offsetTopAndBottom(dy)
                } else {
                    val rect = computeBottomLayDown(mCurrentDragEdge)
                    currentBottomView?.layout(rect.left, rect.top, rect.right, rect.bottom)

                    var newLeft = surfaceView.left + dx
                    var newTop = surfaceView.top + dy

                    if (mCurrentDragEdge == DragEdge.Left && newLeft < paddingLeft)
                        newLeft = paddingLeft
                    else if (mCurrentDragEdge == DragEdge.Right && newLeft > paddingLeft)
                        newLeft = paddingLeft
                    else if (mCurrentDragEdge == DragEdge.Top && newTop < paddingTop)
                        newTop = paddingTop
                    else if (mCurrentDragEdge == DragEdge.Bottom && newTop > paddingTop)
                        newTop = paddingTop

                    surfaceView.layout(newLeft, newTop, newLeft + measuredWidth, newTop + measuredHeight)
                }
            }

            dispatchRevealEvent(evLeft, evTop, evRight, evBottom)

            dispatchSwipeEvent(evLeft, evTop, dx, dy)

            invalidate()
        }
    }

    private var mEventCounter = 0

    private var mOnLayoutListeners: MutableList<SwipeLayout.OnLayout>? = null

    private var mIsBeingDragged: Boolean = false

    private var sX = -1f
    private var sY = -1f

    var isLeftSwipeEnabled: Boolean
        get() {
            val bottomView = mDragEdges[DragEdge.Left]
            return (bottomView != null && bottomView.parent === this
                    && bottomView !== surfaceView && mSwipesEnabled[DragEdge.Left.ordinal])
        }
        set(leftSwipeEnabled) {
            this.mSwipesEnabled[DragEdge.Left.ordinal] = leftSwipeEnabled
        }

    var isRightSwipeEnabled: Boolean
        get() {
            val bottomView = mDragEdges[DragEdge.Right]
            return (bottomView != null && bottomView.parent === this
                    && bottomView !== surfaceView && mSwipesEnabled[DragEdge.Right.ordinal])
        }
        set(rightSwipeEnabled) {
            this.mSwipesEnabled[DragEdge.Right.ordinal] = rightSwipeEnabled
        }

    var isTopSwipeEnabled: Boolean
        get() {
            val bottomView = mDragEdges[DragEdge.Top]
            return (bottomView != null && bottomView.parent === this
                    && bottomView !== surfaceView && mSwipesEnabled[DragEdge.Top.ordinal])
        }
        set(topSwipeEnabled) {
            this.mSwipesEnabled[DragEdge.Top.ordinal] = topSwipeEnabled
        }

    var isBottomSwipeEnabled: Boolean
        get() {
            val bottomView = mDragEdges[DragEdge.Bottom]
            return (bottomView != null && bottomView.parent === this
                    && bottomView !== surfaceView && mSwipesEnabled[DragEdge.Bottom.ordinal])
        }
        set(bottomSwipeEnabled) {
            this.mSwipesEnabled[DragEdge.Bottom.ordinal] = bottomSwipeEnabled
        }

    private val adapterView: AdapterView<*>?
        get() {
            val t = parent
            return if (t is AdapterView<*>) {
                t
            } else null
        }
    internal var clickListener: View.OnClickListener? = null
    internal var longClickListener: View.OnLongClickListener? = null

    private var hitSurfaceRect: Rect? = null
    private val gestureDetector = GestureDetector(getContext(), SwipeDetector())

    /**Deprecated, use [.addDrag]  */
    var dragEdge: DragEdge?
        get() = mCurrentDragEdge
        @Deprecated("")
        set(dragEdge) {
            if (childCount >= 2) {
                dragEdge?.let {
                    mDragEdges[it] = getChildAt(childCount - 2)
                }
            }
            dragEdge?.let { setCurrentDragEdge(it) }
        }

    /**
     * set the drag distance, it will force set the bottom view's width or
     * height via this value.
     *
     * @param max max distance in dp unit
     */
    var dragDistance: Int
        get() = mDragDistance
        set(max) {
            var max = max
            if (max < 0) max = 0
            mDragDistance = dp2px(max.toFloat())
            requestLayout()
        }

    /**
     * There are 2 diffirent show mode.
     * [SwipeLayout.ShowMode].PullOut and
     * [SwipeLayout.ShowMode].LayDown.
     *
     * @param mode
     */
    var showMode: ShowMode?
        get() = mShowMode
        set(mode) {
            mShowMode = mode
            requestLayout()
        }

    /**return null if there is no surface view(no children)  */
    val surfaceView: View?
        get() = if (childCount == 0) null else getChildAt(childCount - 1)

    /**return null if there is no bottom view  */
    val currentBottomView: View?
        get() {
            val bottoms = bottomViews
            return if (mCurrentDragEdge!!.ordinal < bottoms.size) {
                bottoms[mCurrentDragEdge!!.ordinal]
            } else null
        }
    /**
     * @return all bottomViews: left, top, right, bottom (may null if the edge is not set)
     */
    val bottomViews: List<View>
        get() {
            val bottoms = ArrayList<View>()
            for (dragEdge in DragEdge.values()) {
                mDragEdges[dragEdge]?.let { bottoms.add(it) }
            }
            return bottoms
        }

    /**
     * get the open status.
     *
     * @return [SwipeLayout.Status] Open , Close or
     * Middle.
     */
    val openStatus: SwipeLayout.Status
        get() {
            val surfaceView = surfaceView ?: return SwipeLayout.Status.Close
            val surfaceLeft = surfaceView.left
            val surfaceTop = surfaceView.top
            if (surfaceLeft == paddingLeft && surfaceTop == paddingTop) return SwipeLayout.Status.Close

            return if (surfaceLeft == paddingLeft - mDragDistance || surfaceLeft == paddingLeft + mDragDistance
                    || surfaceTop == paddingTop - mDragDistance || surfaceTop == paddingTop + mDragDistance) SwipeLayout.Status.Open else SwipeLayout.Status.Middle

        }
    val dragEdgeMap: Map<DragEdge, View?>
        get() = mDragEdges

    /**Deprecated, use [.getDragEdgeMap]  */
    /**Deprecated, use [.addDrag]  */
    var dragEdges: List<DragEdge>
        @Deprecated("")
        get() = ArrayList(mDragEdges.keys)
        @Deprecated("")
        set(dragEdges) {
            var i = 0
            val size = Math.min(dragEdges.size, childCount - 1)
            while (i < size) {
                val dragEdge = dragEdges[i]
                mDragEdges[dragEdge] = getChildAt(i)
                i++
            }
            if (dragEdges.size == 0 || dragEdges.contains(DefaultDragEdge)) {
                setCurrentDragEdge(DefaultDragEdge)
            } else {
                setCurrentDragEdge(dragEdges[0])
            }
        }

    private val currentOffset: Float
        get() = if (mCurrentDragEdge == null) 0f else mEdgeSwipesOffset[mCurrentDragEdge!!.ordinal]

    enum class DragEdge {
        Left,
        Top,
        Right,
        Bottom
    }

    enum class ShowMode {
        LayDown,
        PullOut
    }

    init {
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

        val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout)
        val dragEdgeChoices = a.getInt(R.styleable.SwipeLayout_drag_edge, DRAG_RIGHT)
        mEdgeSwipesOffset[DragEdge.Left.ordinal] = a.getDimension(R.styleable.SwipeLayout_leftEdgeSwipeOffset, 0f)
        mEdgeSwipesOffset[DragEdge.Right.ordinal] = a.getDimension(R.styleable.SwipeLayout_rightEdgeSwipeOffset, 0f)
        mEdgeSwipesOffset[DragEdge.Top.ordinal] = a.getDimension(R.styleable.SwipeLayout_topEdgeSwipeOffset, 0f)
        mEdgeSwipesOffset[DragEdge.Bottom.ordinal] = a.getDimension(R.styleable.SwipeLayout_bottomEdgeSwipeOffset, 0f)
        isClickToClose = a.getBoolean(R.styleable.SwipeLayout_clickToClose, isClickToClose)

        if (dragEdgeChoices and DRAG_LEFT == DRAG_LEFT) {
            mDragEdges[DragEdge.Left] = null
        }
        if (dragEdgeChoices and DRAG_TOP == DRAG_TOP) {
            mDragEdges[DragEdge.Top] = null
        }
        if (dragEdgeChoices and DRAG_RIGHT == DRAG_RIGHT) {
            mDragEdges[DragEdge.Right] = null
        }
        if (dragEdgeChoices and DRAG_BOTTOM == DRAG_BOTTOM) {
            mDragEdges[DragEdge.Bottom] = null
        }
        val ordinal = a.getInt(R.styleable.SwipeLayout_show_mode, ShowMode.PullOut.ordinal)
        mShowMode = ShowMode.values()[ordinal]
        a.recycle()

    }

    interface SwipeListener {
        fun onStartOpen(layout: SwipeLayout)

        fun onOpen(layout: SwipeLayout)

        fun onStartClose(layout: SwipeLayout)

        fun onClose(layout: SwipeLayout)

        fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int)

        fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float)
    }

    fun addSwipeListener(l: SwipeLayout.SwipeListener) {
        mSwipeListeners.add(l)
    }

    fun removeSwipeListener(l: SwipeLayout.SwipeListener) {
        mSwipeListeners.remove(l)
    }

    interface SwipeDenier {
        /*
         * Called in onInterceptTouchEvent Determines if this swipe event should
         * be denied Implement this interface if you are using views with swipe
         * gestures As a child of SwipeLayout
         *
         * @return true deny false allow
         */
        fun shouldDenySwipe(ev: MotionEvent): Boolean
    }

    fun addSwipeDenier(denier: SwipeLayout.SwipeDenier) {
        mSwipeDeniers.add(denier)
    }

    fun removeSwipeDenier(denier: SwipeLayout.SwipeDenier) {
        mSwipeDeniers.remove(denier)
    }

    fun removeAllSwipeDeniers() {
        mSwipeDeniers.clear()
    }

    interface OnRevealListener {
        fun onReveal(child: View, edge: DragEdge?, fraction: Float, distance: Int)
    }

    /**
     * bind a view with a specific
     * [SwipeLayout.OnRevealListener]
     *
     * @param childId the view id.
     * @param l       the target
     * [SwipeLayout.OnRevealListener]
     */
    fun addRevealListener(childId: Int, l: SwipeLayout.OnRevealListener) {
        val child = findViewById<View>(childId)
                ?: throw IllegalArgumentException("Child does not belong to SwipeListener.")

        if (!mShowEntirely.containsKey(child)) {
            mShowEntirely[child] = false
        }
        if (mRevealListeners[child] == null)
            mRevealListeners[child] = ArrayList()

        mRevealListeners[child]!!.add(l)
    }

    /**
     * bind multiple views with an
     * [SwipeLayout.OnRevealListener].
     *
     * @param childIds the view id.
     * @param l        the [SwipeLayout.OnRevealListener]
     */
    fun addRevealListener(childIds: IntArray, l: SwipeLayout.OnRevealListener) {
        for (i in childIds)
            addRevealListener(i, l)
    }

    fun removeRevealListener(childId: Int, l: SwipeLayout.OnRevealListener) {
        val child = findViewById<View>(childId) ?: return

        mShowEntirely.remove(child)
        if (mRevealListeners.containsKey(child)) mRevealListeners[child]!!.remove(l)
    }

    fun removeAllRevealListeners(childId: Int) {
        val child = findViewById<View>(childId)
        if (child != null) {
            mRevealListeners.remove(child)
            mShowEntirely.remove(child)
        }
    }

    /**
     * the dispatchRevealEvent method may not always get accurate position, it
     * makes the view may not always get the event when the view is totally
     * show( fraction = 1), so , we need to calculate every time.
     */
    protected fun isViewTotallyFirstShowed(child: View, relativePosition: Rect, edge: DragEdge?, surfaceLeft: Int,
                                           surfaceTop: Int, surfaceRight: Int, surfaceBottom: Int): Boolean {
        if (mShowEntirely[child]!!) return false
        val childLeft = relativePosition.left
        val childRight = relativePosition.right
        val childTop = relativePosition.top
        val childBottom = relativePosition.bottom
        var r = false
        if (showMode == ShowMode.LayDown) {
            if (edge == DragEdge.Right && surfaceRight <= childLeft
                    || edge == DragEdge.Left && surfaceLeft >= childRight
                    || edge == DragEdge.Top && surfaceTop >= childBottom
                    || edge == DragEdge.Bottom && surfaceBottom <= childTop)
                r = true
        } else if (showMode == ShowMode.PullOut) {
            if (edge == DragEdge.Right && childRight <= width
                    || edge == DragEdge.Left && childLeft >= paddingLeft
                    || edge == DragEdge.Top && childTop >= paddingTop
                    || edge == DragEdge.Bottom && childBottom <= height)
                r = true
        }
        return r
    }

    protected fun isViewShowing(child: View, relativePosition: Rect, availableEdge: DragEdge?, surfaceLeft: Int,
                                surfaceTop: Int, surfaceRight: Int, surfaceBottom: Int): Boolean {
        val childLeft = relativePosition.left
        val childRight = relativePosition.right
        val childTop = relativePosition.top
        val childBottom = relativePosition.bottom
        if (showMode == ShowMode.LayDown) {
            when (availableEdge) {
                DragEdge.Right -> if (surfaceRight > childLeft && surfaceRight <= childRight) {
                    return true
                }
                DragEdge.Left -> if (surfaceLeft < childRight && surfaceLeft >= childLeft) {
                    return true
                }
                DragEdge.Top -> if (surfaceTop >= childTop && surfaceTop < childBottom) {
                    return true
                }
                DragEdge.Bottom -> if (surfaceBottom > childTop && surfaceBottom <= childBottom) {
                    return true
                }
            }
        } else if (showMode == ShowMode.PullOut) {
            when (availableEdge) {
                DragEdge.Right -> if (childLeft <= width && childRight > width) return true
                DragEdge.Left -> if (childRight >= paddingLeft && childLeft < paddingLeft) return true
                DragEdge.Top -> if (childTop < paddingTop && childBottom >= paddingTop) return true
                DragEdge.Bottom -> if (childTop < height && childTop >= paddingTop) return true
            }
        }
        return false
    }

    protected fun getRelativePosition(child: View): Rect {
        var t = child
        val r = Rect(t.left, t.top, 0, 0)
        while (t.parent != null && t !== rootView) {
            t = t.parent as View
            if (t === this) break
            r.left += t.left
            r.top += t.top
        }
        r.right = r.left + child.measuredWidth
        r.bottom = r.top + child.measuredHeight
        return r
    }

    protected fun dispatchSwipeEvent(surfaceLeft: Int, surfaceTop: Int, dx: Int, dy: Int) {
        val edge = dragEdge
        var open = true
        if (edge == DragEdge.Left) {
            if (dx < 0) open = false
        } else if (edge == DragEdge.Right) {
            if (dx > 0) open = false
        } else if (edge == DragEdge.Top) {
            if (dy < 0) open = false
        } else if (edge == DragEdge.Bottom) {
            if (dy > 0) open = false
        }

        dispatchSwipeEvent(surfaceLeft, surfaceTop, open)
    }

    protected fun dispatchSwipeEvent(surfaceLeft: Int, surfaceTop: Int, open: Boolean) {
        safeBottomView()
        val status = openStatus

        if (!mSwipeListeners.isEmpty()) {
            mEventCounter++
            for (l in mSwipeListeners) {
                if (mEventCounter == 1) {
                    if (open) {
                        l.onStartOpen(this)
                    } else {
                        l.onStartClose(this)
                    }
                }
                l.onUpdate(this@SwipeLayout, surfaceLeft - paddingLeft, surfaceTop - paddingTop)
            }

            if (status == SwipeLayout.Status.Close) {
                for (l in mSwipeListeners) {
                    l.onClose(this@SwipeLayout)
                }
                mEventCounter = 0
            }

            if (status == SwipeLayout.Status.Open) {
                val currentBottomView = currentBottomView
                if (currentBottomView != null) {
                    currentBottomView.isEnabled = true
                }
                for (l in mSwipeListeners) {
                    l.onOpen(this@SwipeLayout)
                }
                mEventCounter = 0
            }
        }
    }

    /**
     * prevent bottom view get any touch event. Especially in LayDown mode.
     */
    private fun safeBottomView() {
        val status = openStatus
        val bottoms = bottomViews

        if (status == SwipeLayout.Status.Close) {
            for (bottom in bottoms) {
                if (bottom != null && bottom.visibility != View.INVISIBLE) {
                    bottom.visibility = View.INVISIBLE
                }
            }
        } else {
            val currentBottomView = currentBottomView
            if (currentBottomView != null && currentBottomView.visibility != View.VISIBLE) {
                currentBottomView.visibility = View.VISIBLE
            }
        }
    }

    protected fun dispatchRevealEvent(surfaceLeft: Int, surfaceTop: Int, surfaceRight: Int,
                                      surfaceBottom: Int) {
        if (mRevealListeners.isEmpty()) return
        for ((child, value) in mRevealListeners) {
            val rect = getRelativePosition(child)
            if (isViewShowing(child, rect, mCurrentDragEdge, surfaceLeft, surfaceTop,
                            surfaceRight, surfaceBottom)) {
                mShowEntirely[child] = false
                var distance = 0
                var fraction = 0f
                if (showMode == ShowMode.LayDown) {
                    when (mCurrentDragEdge) {
                        DragEdge.Left -> {
                            distance = rect.left - surfaceLeft
                            fraction = distance / child.width.toFloat()
                        }
                        DragEdge.Right -> {
                            distance = rect.right - surfaceRight
                            fraction = distance / child.width.toFloat()
                        }
                        DragEdge.Top -> {
                            distance = rect.top - surfaceTop
                            fraction = distance / child.height.toFloat()
                        }
                        DragEdge.Bottom -> {
                            distance = rect.bottom - surfaceBottom
                            fraction = distance / child.height.toFloat()
                        }
                    }
                } else if (showMode == ShowMode.PullOut) {
                    when (mCurrentDragEdge) {
                        DragEdge.Left -> {
                            distance = rect.right - paddingLeft
                            fraction = distance / child.width.toFloat()
                        }
                        DragEdge.Right -> {
                            distance = rect.left - width
                            fraction = distance / child.width.toFloat()
                        }
                        DragEdge.Top -> {
                            distance = rect.bottom - paddingTop
                            fraction = distance / child.height.toFloat()
                        }
                        DragEdge.Bottom -> {
                            distance = rect.top - height
                            fraction = distance / child.height.toFloat()
                        }
                    }
                }

                for (l in value) {
                    l.onReveal(child, mCurrentDragEdge, Math.abs(fraction), distance)
                    if (Math.abs(fraction) == 1f) {
                        mShowEntirely[child] = true
                    }
                }
            }

            if (isViewTotallyFirstShowed(child, rect, mCurrentDragEdge, surfaceLeft, surfaceTop,
                            surfaceRight, surfaceBottom)) {
                mShowEntirely[child] = true
                for (l in value) {
                    if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right)
                        l.onReveal(child, mCurrentDragEdge, 1f, child.width)
                    else
                        l.onReveal(child, mCurrentDragEdge, 1f, child.height)
                }
            }

        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    /**
     * [android.view.View.OnLayoutChangeListener] added in API 11. I need
     * to support it from API 8.
     */
    interface OnLayout {
        fun onLayout(v: SwipeLayout)
    }

    fun addOnLayoutListener(l: SwipeLayout.OnLayout) {
        if (mOnLayoutListeners == null) mOnLayoutListeners = ArrayList()
        mOnLayoutListeners!!.add(l)
    }

    fun removeOnLayoutListener(l: SwipeLayout.OnLayout) {
        if (mOnLayoutListeners != null) mOnLayoutListeners!!.remove(l)
    }

    @JvmOverloads
    fun addDrag(dragEdge: DragEdge, child: View, params: ViewGroup.LayoutParams? = null) {
        var params = params
        if (params == null) {
            params = generateDefaultLayoutParams()
        }
        if (!checkLayoutParams(params)) {
            params = generateLayoutParams(params)
        }
        var gravity = -1
        when (dragEdge) {
            DragEdge.Left -> gravity = Gravity.LEFT
            DragEdge.Right -> gravity = Gravity.RIGHT
            DragEdge.Top -> gravity = Gravity.TOP
            DragEdge.Bottom -> gravity = Gravity.BOTTOM
        }
        if (params is FrameLayout.LayoutParams) {
            params.gravity = gravity
        }
        params?.let { addView(child, 0, it) }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams) {
        var gravity = Gravity.NO_GRAVITY
        try {
            gravity = params.javaClass.getField("gravity").get(params) as Int
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (gravity > 0) {
            gravity = GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this))

            if (gravity and Gravity.LEFT == Gravity.LEFT) {
                child?.let { mDragEdges.put(DragEdge.Left, it) }
            }
            if (gravity and Gravity.RIGHT == Gravity.RIGHT) {
                child?.let { mDragEdges.put(DragEdge.Right, it) }
            }
            if (gravity and Gravity.TOP == Gravity.TOP) {
                child?.let { mDragEdges.put(DragEdge.Top, it) }
            }
            if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
                child?.let { mDragEdges.put(DragEdge.Bottom, it) }
            }
        } else {
            for ((key, value) in mDragEdges) {
                if (value == null) {
                    //means used the drag_edge attr, the no gravity child should be use set
                    child?.let { mDragEdges.put(key, it) }
                    break
                }
            }
        }
        if (child == null || child.parent === this) {
            return
        }
        super.addView(child, index, params)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        updateBottomViews()

        if (mOnLayoutListeners != null)
            for (i in mOnLayoutListeners!!.indices) {
                mOnLayoutListeners!![i].onLayout(this)
            }
    }

    internal fun layoutPullOut() {
        var rect = computeSurfaceLayoutArea(false)
        val surfaceView = surfaceView
        if (surfaceView != null) {
            surfaceView.layout(rect.left, rect.top, rect.right, rect.bottom)
            bringChildToFront(surfaceView)
        }
        rect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, rect)
        val currentBottomView = currentBottomView
        currentBottomView?.layout(rect.left, rect.top, rect.right, rect.bottom)
    }

    internal fun layoutLayDown() {
        var rect = computeSurfaceLayoutArea(false)
        val surfaceView = surfaceView
        if (surfaceView != null) {
            surfaceView.layout(rect.left, rect.top, rect.right, rect.bottom)
            bringChildToFront(surfaceView)
        }
        rect = computeBottomLayoutAreaViaSurface(ShowMode.LayDown, rect)
        val currentBottomView = currentBottomView
        currentBottomView?.layout(rect.left, rect.top, rect.right, rect.bottom)
    }

    private fun checkCanDrag(ev: MotionEvent) {
        if (mIsBeingDragged) return
        if (openStatus == SwipeLayout.Status.Middle) {
            mIsBeingDragged = true
            return
        }
        val status = openStatus
        val distanceX = ev.rawX - sX
        val distanceY = ev.rawY - sY
        var angle = Math.abs(distanceY / distanceX)
        angle = Math.toDegrees(Math.atan(angle.toDouble())).toFloat()
        if (openStatus == SwipeLayout.Status.Close) {
            val dragEdge: DragEdge
            if (angle < 45) {
                if (distanceX > 0 && isLeftSwipeEnabled) {
                    dragEdge = DragEdge.Left
                } else if (distanceX < 0 && isRightSwipeEnabled) {
                    dragEdge = DragEdge.Right
                } else
                    return

            } else {
                if (distanceY > 0 && isTopSwipeEnabled) {
                    dragEdge = DragEdge.Top
                } else if (distanceY < 0 && isBottomSwipeEnabled) {
                    dragEdge = DragEdge.Bottom
                } else
                    return
            }
            setCurrentDragEdge(dragEdge)
        }

        var doNothing = false
        if (mCurrentDragEdge == DragEdge.Right) {
            var suitable = status == SwipeLayout.Status.Open && distanceX > mTouchSlop || status == SwipeLayout.Status.Close && distanceX < -mTouchSlop
            suitable = suitable || status == SwipeLayout.Status.Middle

            if (angle > 30 || !suitable) {
                doNothing = true
            }
        }

        if (mCurrentDragEdge == DragEdge.Left) {
            var suitable = status == SwipeLayout.Status.Open && distanceX < -mTouchSlop || status == SwipeLayout.Status.Close && distanceX > mTouchSlop
            suitable = suitable || status == SwipeLayout.Status.Middle

            if (angle > 30 || !suitable) {
                doNothing = true
            }
        }

        if (mCurrentDragEdge == DragEdge.Top) {
            var suitable = status == SwipeLayout.Status.Open && distanceY < -mTouchSlop || status == SwipeLayout.Status.Close && distanceY > mTouchSlop
            suitable = suitable || status == SwipeLayout.Status.Middle

            if (angle < 60 || !suitable) {
                doNothing = true
            }
        }

        if (mCurrentDragEdge == DragEdge.Bottom) {
            var suitable = status == SwipeLayout.Status.Open && distanceY > mTouchSlop || status == SwipeLayout.Status.Close && distanceY < -mTouchSlop
            suitable = suitable || status == SwipeLayout.Status.Middle

            if (angle < 60 || !suitable) {
                doNothing = true
            }
        }
        mIsBeingDragged = !doNothing
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeEnabled) {
            return false
        }
        if (isClickToClose && openStatus == SwipeLayout.Status.Open && isTouchOnSurface(ev)) {
            return true
        }
        for (denier in mSwipeDeniers) {
            if (denier != null && denier.shouldDenySwipe(ev)) {
                return false
            }
        }

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mDragHelper.processTouchEvent(ev)
                mIsBeingDragged = false
                sX = ev.rawX
                sY = ev.rawY
                //if the swipe is in middle state(scrolling), should intercept the touch
                if (openStatus == SwipeLayout.Status.Middle) {
                    mIsBeingDragged = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val beforeCheck = mIsBeingDragged
                checkCanDrag(ev)
                if (mIsBeingDragged) {
                    val parent = parent
                    parent?.requestDisallowInterceptTouchEvent(true)
                }
                if (!beforeCheck && mIsBeingDragged) {
                    //let children has one chance to catch the touch, and request the swipe not intercept
                    //useful when swipeLayout wrap a swipeLayout or other gestural layout
                    return false
                }
            }

            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                mIsBeingDragged = false
                mDragHelper.processTouchEvent(ev)
            }
            else//handle other action, such as ACTION_POINTER_DOWN/UP
            -> mDragHelper.processTouchEvent(ev)
        }
        return mIsBeingDragged
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isSwipeEnabled) return super.onTouchEvent(event)

        val action = event.actionMasked
        gestureDetector.onTouchEvent(event)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mDragHelper.processTouchEvent(event)
                sX = event.rawX
                sY = event.rawY
                run {
                    //the drag state and the direction are already judged at onInterceptTouchEvent
                    checkCanDrag(event)
                    if (mIsBeingDragged) {
                        parent.requestDisallowInterceptTouchEvent(true)
                        mDragHelper.processTouchEvent(event)
                    }
                }
            }


            MotionEvent.ACTION_MOVE -> {
                checkCanDrag(event)
                if (mIsBeingDragged) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    mDragHelper.processTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mDragHelper.processTouchEvent(event)
            }

            else//handle other action, such as ACTION_POINTER_DOWN/UP
            -> mDragHelper.processTouchEvent(event)
        }

        return super.onTouchEvent(event) || mIsBeingDragged || action == MotionEvent.ACTION_DOWN
    }

    private fun insideAdapterView(): Boolean {
        return adapterView != null
    }

    private fun performAdapterViewItemClick() {
        if (openStatus != SwipeLayout.Status.Close) return
        val t = parent
        if (t is AdapterView<*>) {
            val p = t.getPositionForView(this@SwipeLayout)
            if (p != AdapterView.INVALID_POSITION) {
                t.performItemClick(t.getChildAt(p - t.firstVisiblePosition), p, t
                        .adapter.getItemId(p))
            }
        }
    }

    private fun performAdapterViewItemLongClick(): Boolean {
        if (openStatus != SwipeLayout.Status.Close) return false
        val t = parent
        if (t is AdapterView<*>) {
            val p = t.getPositionForView(this@SwipeLayout)
            if (p == AdapterView.INVALID_POSITION) return false
            val vId = t.getItemIdAtPosition(p)
            var handled = false
            try {
                val m = AbsListView::class.java!!.getDeclaredMethod("performLongPress", View::class.java, Int::class.javaPrimitiveType, Long::class.javaPrimitiveType)
                m.setAccessible(true)
                handled = m.invoke(t, this@SwipeLayout, p, vId) as Boolean

            } catch (e: Exception) {
                e.printStackTrace()

                if (t.onItemLongClickListener != null) {
                    handled = t.onItemLongClickListener.onItemLongClick(t, this@SwipeLayout, p, vId)
                }
                if (handled) {
                    t.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }

            return handled
        }
        return false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (insideAdapterView()) {
            if (clickListener == null) {
                setOnClickListener { performAdapterViewItemClick() }
            }
            if (longClickListener == null) {
                setOnLongClickListener {
                    performAdapterViewItemLongClick()
                    true
                }
            }
        }
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        super.setOnClickListener(l)
        clickListener = l
    }

    override fun setOnLongClickListener(l: View.OnLongClickListener?) {
        super.setOnLongClickListener(l)
        longClickListener = l
    }

    private fun isTouchOnSurface(ev: MotionEvent): Boolean {
        val surfaceView = surfaceView ?: return false
        if (hitSurfaceRect == null) {
            hitSurfaceRect = Rect()
        }
        surfaceView.getHitRect(hitSurfaceRect)
        return hitSurfaceRect!!.contains(ev.x.toInt(), ev.y.toInt())
    }

    internal inner class SwipeDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (isClickToClose && isTouchOnSurface(e)) {
                close()
            }
            return super.onSingleTapUp(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (mDoubleClickListener != null) {
                val target: View?
                val bottom = currentBottomView
                val surface = surfaceView
                if (bottom != null && e.x > bottom.left && e.x < bottom.right
                        && e.y > bottom.top && e.y < bottom.bottom) {
                    target = bottom
                } else {
                    target = surface
                }
                mDoubleClickListener!!.onDoubleClick(this@SwipeLayout, target === surface)
            }
            return true
        }
    }

    enum class Status {
        Middle,
        Open,
        Close
    }


    /**
     * Process the surface release event.
     *
     * @param xvel xVelocity
     * @param yvel yVelocity
     * @param isCloseBeforeDragged the open state before drag
     */
    protected fun processHandRelease(xvel: Float, yvel: Float, isCloseBeforeDragged: Boolean) {
        val minVelocity = mDragHelper.minVelocity
        val surfaceView = surfaceView
        val currentDragEdge = mCurrentDragEdge
        if (currentDragEdge == null || surfaceView == null) {
            return
        }
        val willOpenPercent = if (isCloseBeforeDragged) .25f else .75f
        if (currentDragEdge == DragEdge.Left) {
            if (xvel > minVelocity)
                open()
            else if (xvel < -minVelocity)
                close()
            else {
                val openPercent = 1f * surfaceView.left / mDragDistance
                if (openPercent > willOpenPercent)
                    open()
                else
                    close()
            }
        } else if (currentDragEdge == DragEdge.Right) {
            if (xvel > minVelocity)
                close()
            else if (xvel < -minVelocity)
                open()
            else {
                val openPercent = 1f * -surfaceView.left / mDragDistance
                if (openPercent > willOpenPercent)
                    open()
                else
                    close()
            }
        } else if (currentDragEdge == DragEdge.Top) {
            if (yvel > minVelocity)
                open()
            else if (yvel < -minVelocity)
                close()
            else {
                val openPercent = 1f * surfaceView.top / mDragDistance
                if (openPercent > willOpenPercent)
                    open()
                else
                    close()
            }
        } else if (currentDragEdge == DragEdge.Bottom) {
            if (yvel > minVelocity)
                close()
            else if (yvel < -minVelocity)
                open()
            else {
                val openPercent = 1f * -surfaceView.top / mDragDistance
                if (openPercent > willOpenPercent)
                    open()
                else
                    close()
            }
        }
    }

    @JvmOverloads
    fun open(smooth: Boolean = true, notify: Boolean = true) {
        val surface = surfaceView
        val bottom = currentBottomView
        if (surface == null) {
            return
        }
        val dx: Int
        val dy: Int
        val rect = computeSurfaceLayoutArea(true)
        if (smooth) {
            mDragHelper.smoothSlideViewTo(surface, rect.left, rect.top)
        } else {
            dx = rect.left - surface.left
            dy = rect.top - surface.top
            surface.layout(rect.left, rect.top, rect.right, rect.bottom)
            if (showMode == ShowMode.PullOut) {
                val bRect = computeBottomLayoutAreaViaSurface(ShowMode.PullOut, rect)
                bottom?.layout(bRect.left, bRect.top, bRect.right, bRect.bottom)
            }
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom)
                dispatchSwipeEvent(rect.left, rect.top, dx, dy)
            } else {
                safeBottomView()
            }
        }
        invalidate()
    }

    fun open(edge: DragEdge) {
        setCurrentDragEdge(edge)
        open(true, true)
    }

    fun open(smooth: Boolean, edge: DragEdge) {
        setCurrentDragEdge(edge)
        open(smooth, true)
    }

    fun open(smooth: Boolean, notify: Boolean, edge: DragEdge) {
        setCurrentDragEdge(edge)
        open(smooth, notify)
    }

    /**
     * close surface
     *
     * @param smooth smoothly or not.
     * @param notify if notify all the listeners.
     */
    @JvmOverloads
    fun close(smooth: Boolean = true, notify: Boolean = true) {
        val surface = surfaceView ?: return
        val dx: Int
        val dy: Int
        if (smooth)
            mDragHelper.smoothSlideViewTo(surfaceView!!, paddingLeft, paddingTop)
        else {
            val rect = computeSurfaceLayoutArea(false)
            dx = rect.left - surface.left
            dy = rect.top - surface.top
            surface.layout(rect.left, rect.top, rect.right, rect.bottom)
            if (notify) {
                dispatchRevealEvent(rect.left, rect.top, rect.right, rect.bottom)
                dispatchSwipeEvent(rect.left, rect.top, dx, dy)
            } else {
                safeBottomView()
            }
        }
        invalidate()
    }

    @JvmOverloads
    fun toggle(smooth: Boolean = true) {
        if (openStatus == SwipeLayout.Status.Open)
            close(smooth)
        else if (openStatus == SwipeLayout.Status.Close) open(smooth)
    }


    /**
     * a helper function to compute the Rect area that surface will hold in.
     * @param open open status or close status.
     */
    private fun computeSurfaceLayoutArea(open: Boolean): Rect {
        var l = paddingLeft
        var t = paddingTop
        if (open) {
            if (mCurrentDragEdge == DragEdge.Left)
                l = paddingLeft + mDragDistance
            else if (mCurrentDragEdge == DragEdge.Right)
                l = paddingLeft - mDragDistance
            else if (mCurrentDragEdge == DragEdge.Top)
                t = paddingTop + mDragDistance
            else
                t = paddingTop - mDragDistance
        }
        return Rect(l, t, l + measuredWidth, t + measuredHeight)
    }

    private fun computeBottomLayoutAreaViaSurface(mode: ShowMode, surfaceArea: Rect): Rect {
        val bottomView = currentBottomView

        var bl = surfaceArea.left
        var bt = surfaceArea.top
        var br = surfaceArea.right
        var bb = surfaceArea.bottom
        if (mode == ShowMode.PullOut) {
            if (mCurrentDragEdge == DragEdge.Left)
                bl = surfaceArea.left - mDragDistance
            else if (mCurrentDragEdge == DragEdge.Right)
                bl = surfaceArea.right
            else if (mCurrentDragEdge == DragEdge.Top)
                bt = surfaceArea.top - mDragDistance
            else
                bt = surfaceArea.bottom

            if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                bb = surfaceArea.bottom
                br = bl + (bottomView?.measuredWidth ?: 0)
            } else {
                bb = bt + (bottomView?.measuredHeight ?: 0)
                br = surfaceArea.right
            }
        } else if (mode == ShowMode.LayDown) {
            if (mCurrentDragEdge == DragEdge.Left)
                br = bl + mDragDistance
            else if (mCurrentDragEdge == DragEdge.Right)
                bl = br - mDragDistance
            else if (mCurrentDragEdge == DragEdge.Top)
                bb = bt + mDragDistance
            else
                bt = bb - mDragDistance

        }
        return Rect(bl, bt, br, bb)

    }

    private fun computeBottomLayDown(dragEdge: DragEdge?): Rect {
        var bl = paddingLeft
        var bt = paddingTop
        val br: Int
        val bb: Int
        if (dragEdge == DragEdge.Right) {
            bl = measuredWidth - mDragDistance
        } else if (dragEdge == DragEdge.Bottom) {
            bt = measuredHeight - mDragDistance
        }
        if (dragEdge == DragEdge.Left || dragEdge == DragEdge.Right) {
            br = bl + mDragDistance
            bb = bt + measuredHeight
        } else {
            br = bl + measuredWidth
            bb = bt + mDragDistance
        }
        return Rect(bl, bt, br, bb)
    }

    fun setOnDoubleClickListener(doubleClickListener: SwipeLayout.DoubleClickListener) {
        mDoubleClickListener = doubleClickListener
    }

    interface DoubleClickListener {
        fun onDoubleClick(layout: SwipeLayout, surface: Boolean)
    }

    private fun dp2px(dp: Float): Int {
        return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    override fun onViewRemoved(child: View) {
        for ((key, value) in HashMap(mDragEdges)) {
            if (value === child) {
                mDragEdges.remove(key)
            }
        }
    }

    /**Deprecated, use [.addDrag]  */
    @Deprecated("")
    fun setDragEdges(vararg mDragEdges: DragEdge) {
        dragEdges = Arrays.asList(*mDragEdges)
    }

    /**
     * Deprecated, use [.addDrag]
     * When using multiple drag edges it's a good idea to pass the ids of the views that
     * you're using for the left, right, top bottom views (-1 if you're not using a particular view)
     */
    @Deprecated("")
    fun setBottomViewIds(leftId: Int, rightId: Int, topId: Int, bottomId: Int) {
        addDrag(DragEdge.Left, findViewById(leftId))
        addDrag(DragEdge.Right, findViewById(rightId))
        addDrag(DragEdge.Top, findViewById(topId))
        addDrag(DragEdge.Bottom, findViewById(bottomId))
    }

    private fun setCurrentDragEdge(dragEdge: DragEdge) {
        if (mCurrentDragEdge != dragEdge) {
            mCurrentDragEdge = dragEdge
            updateBottomViews()
        }
    }

    private fun updateBottomViews() {
        val currentBottomView = currentBottomView
        if (currentBottomView != null) {
            if (mCurrentDragEdge == DragEdge.Left || mCurrentDragEdge == DragEdge.Right) {
                mDragDistance = currentBottomView.measuredWidth - dp2px(currentOffset)
            } else
                mDragDistance = currentBottomView.measuredHeight - dp2px(currentOffset)
        }

        if (mShowMode == ShowMode.PullOut)
            layoutPullOut()
        else if (mShowMode == ShowMode.LayDown) layoutLayDown()

        safeBottomView()
    }

    companion object {
        @Deprecated("")
        val EMPTY_LAYOUT = -1
        private val DRAG_LEFT = 1
        private val DRAG_RIGHT = 2
        private val DRAG_TOP = 4
        private val DRAG_BOTTOM = 8
        private val DefaultDragEdge = DragEdge.Right
    }
}
/**
 * smoothly open surface.
 */
/**
 * smoothly close surface.
 */
