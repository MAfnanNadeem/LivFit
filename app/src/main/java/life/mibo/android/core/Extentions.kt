package life.mibo.android.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import life.mibo.android.R
import life.mibo.android.utils.Toasty
import java.io.Serializable

@Throws(JsonIOException::class)
fun Serializable.toJson(): String {
    return Gson().toJson(this, javaClass)
}

@Throws(JsonSyntaxException::class)
fun <T> String.to(type: Class<T>): T where T : Serializable? {
    return Gson().fromJson(this, type)
}

fun String.toIntOrZero(): Int {
    return try {
        if (this.isNullOrEmpty()) 0 else this.replace(Regex("\\D+"), "").toInt()
    } catch (e: Exception) {
        0
    }
}

fun Color.PRIMARY(): Int {
    return Color.parseColor("#09B189")
}

fun Context.showToast(msg: String) {
    Toasty.info(this, msg).show()
}

class Extentions {
}

internal fun AttributeSet?.handleArguments(
    context: Context, attrs: IntArray, defStyleAttr: Int, defStyleRes: Int,
    block: TypedArray.() -> Unit
) = this?.let {
    val arr = context.obtainStyledAttributes(it, attrs, defStyleAttr, defStyleRes)
    block(arr)
    arr.recycle()
}

internal typealias Style = R.styleable

internal val Context.hasCameraPermission
    get() = isPermissionGranted(Manifest.permission.CAMERA)

internal fun Fragment.requestCameraPermission(requestCode: Int) =
    requestPermission(Manifest.permission.CAMERA, requestCode)

internal val IntArray.isPermissionGranted
    get() = size > 0 && get(0) == PackageManager.PERMISSION_GRANTED

private fun Context.isPermissionGranted(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

private fun Fragment.requestPermission(permission: String, requestCode: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    if (context?.isPermissionGranted(permission) == true) return
    requestPermissions(arrayOf(permission), requestCode)
}

internal class Debouncer(
    val debounceTime: Int
) {
    private var lastShot = 0L

    operator fun <T> invoke(block: () -> T) =
        if (System.currentTimeMillis() - lastShot > debounceTime) {
            block.invoke().also {
                lastShot = System.currentTimeMillis()
            }
        } else {
            null
        }
}

fun NavController.navigateSafe(
    @IdRes actionId: Int, @IdRes fragmentId: Int, args: Bundle? = null,
    navOptions: NavOptions? = null, navExtras: Navigator.Extras? = null
) {
    if (actionId != 0) {
        val action = currentDestination?.getAction(actionId) ?: graph.getAction(actionId)
        if (action != null && currentDestination?.id != action.destinationId) {
            navigate(actionId, args, navOptions, navExtras)
            return
        }
    } else if (fragmentId != 0 && fragmentId != currentDestination?.id)
        navigate(fragmentId, args, navOptions, navExtras)
}

fun <T : CoordinatorLayout.Behavior<*>> View.findBehavior(): T = layoutParams.run {
    if (this !is CoordinatorLayout.LayoutParams) throw IllegalArgumentException("View's layout params should be CoordinatorLayout.LayoutParams")

    (layoutParams as CoordinatorLayout.LayoutParams).behavior as? T
        ?: throw IllegalArgumentException("Layout's behavior is not current behavior")
}


//fun AppCompatActivity.startActivityForResult(
//    intent: Intent,
//    listener: (Int, Intent?) -> Unit?
//) {
//    ////androidx.activity
//    registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
//        listener.invoke(result.resultCode, result.data)
//    }.launch(intent)
//}
//
//fun Fragment.startActivityForResult(intent: Intent, listener: (Int, Intent?) -> Unit?) {
//    registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
//        listener.invoke(result.resultCode, result.data)
//    }.launch(intent)
//}

//
//fun Fragment.startForResult(intent: Intent, block: (Result) -> Unit): KotlinActivityResult {
//    return KotlinActivityResult(activity, intent, block)
//}
//
//fun FragmentActivity.startForResult(intent: Intent, block: (Result) -> Unit): KotlinActivityResult {
//    return KotlinActivityResult(this, intent, block)
//}
//
//fun Fragment.startForResult(request: Request, block: (Result) -> Unit): KotlinActivityResult {
//    return KotlinActivityResult(activity, request, block)
//}
//
//fun FragmentActivity.startForResult(request: Request, block: (Result) -> Unit): KotlinActivityResult {
//    return KotlinActivityResult(this, request, block)
//}
//
//class KotlinActivityResult {
//
//    constructor(activity: FragmentActivity?, intentToStart: Intent, successBlock: (Result) -> Unit) {
//        inlineActivityResult = InlineActivityResult(activity)
//            .onSuccess(successBlock)
//            .startForResult(intentToStart)
//    }
//
//    constructor(activity: FragmentActivity?, request: Request, successBlock: (Result) -> Unit) {
//        inlineActivityResult = InlineActivityResult(activity)
//            .onSuccess(successBlock)
//            .startForResult(request)
//    }
//
//    val inlineActivityResult: InlineActivityResult
//
//    fun onFailed(failBlock: ((Result) -> Unit)): KotlinActivityResult {
//        inlineActivityResult.onFail(failBlock)
//        return this
//    }
//}