package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import org.json.JSONArray
import org.json.JSONObject

class MyraAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "MyraAccessibility"
        var instance: MyraAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "MYRA Accessibility Service Connected successfully.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Observes accessibility events from target apps
    }

    override fun onInterrupt() {
        Log.w(TAG, "MYRA Accessibility Service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
        Log.i(TAG, "MYRA Accessibility Service destroyed.")
    }

    /**
     * Inspects the current screen window node tree and returns a JSON string
     * containing visible elements, text, viewIds, and bounds.
     */
    fun inspectCurrentScreen(): String {
        val rootNode = rootInActiveWindow ?: return "[]"
        val jsonArray = JSONArray()

        fun traverse(node: AccessibilityNodeInfo?) {
            if (node == null) return

            val text = node.text?.toString() ?: ""
            val contentDesc = node.contentDescription?.toString() ?: ""
            val viewId = node.viewIdResourceName ?: ""
            val className = node.className?.toString() ?: ""
            val bounds = Rect()
            node.getBoundsInScreen(bounds)

            if (text.isNotBlank() || contentDesc.isNotBlank() || node.isClickable || node.isEditable) {
                val nodeObj = JSONObject().apply {
                    put("text", text)
                    put("contentDescription", contentDesc)
                    put("viewId", viewId)
                    put("className", className)
                    put("isClickable", node.isClickable)
                    put("isEditable", node.isEditable)
                    put("isScrollable", node.isScrollable)
                    put("bounds", JSONObject().apply {
                        put("left", bounds.left)
                        put("top", bounds.top)
                        put("right", bounds.right)
                        put("bottom", bounds.bottom)
                        put("centerX", bounds.centerX())
                        put("centerY", bounds.centerY())
                    })
                }
                jsonArray.put(nodeObj)
            }

            for (i in 0 until node.childCount) {
                traverse(node.getChild(i))
            }
        }

        try {
            traverse(rootNode)
        } catch (e: Exception) {
            Log.e(TAG, "Error inspecting screen nodes: ${e.message}", e)
        }

        return jsonArray.toString()
    }

    /**
     * Dispatches a tap gesture at the specified screen coordinates (x, y).
     */
    fun dispatchClick(x: Float, y: Float): Boolean {
        val path = Path().apply {
            moveTo(x, y)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, 100)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        var isDispatched = false
        val success = dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d(TAG, "Gesture click completed at ($x, $y)")
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.w(TAG, "Gesture click cancelled at ($x, $y)")
            }
        }, null)

        return success
    }

    /**
     * Performs a click action directly on a node matching the specified viewId or text.
     */
    fun performClickOnNode(viewId: String?, text: String?): Boolean {
        val root = rootInActiveWindow ?: return false

        // Try by viewId
        if (!viewId.isNullOrBlank()) {
            val nodes = root.findAccessibilityNodeInfosByViewId(viewId)
            for (node in nodes) {
                if (node.isClickable && node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    return true
                }
                // Check parent if node itself not marked clickable
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable && parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        return true
                    }
                    parent = parent.parent
                }
            }
        }

        // Try by text
        if (!text.isNullOrBlank()) {
            val nodes = root.findAccessibilityNodeInfosByText(text)
            for (node in nodes) {
                if (node.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                    return true
                }
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable && parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        return true
                    }
                    parent = parent.parent
                }
            }
        }

        return false
    }

    /**
     * Types text into the currently focused or first editable node.
     */
    fun performInputText(text: String): Boolean {
        val root = rootInActiveWindow ?: return false

        // Look for focused input
        var targetNode = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)

        // If no focused input, search for any editable node
        if (targetNode == null) {
            fun findEditable(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
                if (node == null) return null
                if (node.isEditable) return node
                for (i in 0 until node.childCount) {
                    val found = findEditable(node.getChild(i))
                    if (found != null) return found
                }
                return null
            }
            targetNode = findEditable(root)
        }

        if (targetNode != null) {
            val arguments = Bundle().apply {
                putCharSequence(
                    AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    text
                )
            }
            return targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        }

        return false
    }

    /**
     * Performs a scroll action (forward/down or backward/up).
     */
    fun performScroll(direction: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val action = if (direction.equals("up", true) || direction.equals("backward", true)) {
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        } else {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        }

        fun findScrollable(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
            if (node == null) return null
            if (node.isScrollable) return node
            for (i in 0 until node.childCount) {
                val found = findScrollable(node.getChild(i))
                if (found != null) return found
            }
            return null
        }

        val scrollableNode = findScrollable(root)
        return scrollableNode?.performAction(action) ?: false
    }

    /**
     * Performs Android system global actions.
     */
    fun performGlobalActionCompat(action: String): Boolean {
        val globalActionId = when (action.lowercase()) {
            "back" -> GLOBAL_ACTION_BACK
            "home" -> GLOBAL_ACTION_HOME
            "recents" -> GLOBAL_ACTION_RECENTS
            "notifications" -> GLOBAL_ACTION_NOTIFICATIONS
            "quick_settings" -> GLOBAL_ACTION_QUICK_SETTINGS
            else -> GLOBAL_ACTION_BACK
        }
        return performGlobalAction(globalActionId)
    }
}
