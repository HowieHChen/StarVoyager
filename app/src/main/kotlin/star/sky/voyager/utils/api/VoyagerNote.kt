package star.sky.voyager.utils.api

import android.graphics.drawable.Drawable
import android.view.View
import com.github.kyuubiran.ezxhelper.ClassUtils
import com.github.kyuubiran.ezxhelper.Log
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field
import java.lang.reflect.Method

@JvmInline
value class Args(val args: Array<out Any?>)

@JvmInline
value class ArgTypes(val argTypes: Array<out Class<*>>)

@Suppress("NOTHING_TO_INLINE")
inline fun args(vararg args: Any?) = Args(args)

@Suppress("NOTHING_TO_INLINE")
inline fun argTypes(vararg argTypes: Class<*>) = ArgTypes(argTypes)

typealias MethodCondition = Method.() -> Boolean

/**
 * 扩展函数 通过类或者对象获取单个属性
 * @param fieldName 属性名
 * @param isStatic 是否静态类型
 * @param fieldType 属性类型
 * @return 符合条件的属性
 * @throws IllegalArgumentException 属性名为空
 * @throws NoSuchFieldException 未找到属性
 */
fun Any.field(
    fieldName: String,
    isStatic: Boolean = false,
    fieldType: Class<*>? = null
): Field {
    if (fieldName.isBlank()) throw IllegalArgumentException("Field name must not be empty!")
    var c: Class<*> = if (this is Class<*>) this else this.javaClass
    do {
        c.declaredFields
            .filter { isStatic == it.isStatic }
            .firstOrNull { (fieldType == null || it.type == fieldType) && (it.name == fieldName) }
            ?.let { it.isAccessible = true;return it }
    } while (c.superclass?.also { c = it } != null)
    throw NoSuchFieldException("Name: $fieldName,Static: $isStatic, Type: ${if (fieldType == null) "ignore" else fieldType.name}")
}

/**
 * 扩展函数 调用对象中符合条件的方法
 * @param args 参数
 * @param condition 条件
 * @return 方法的返回值
 * @throws NoSuchMethodException 未找到方法
 */
fun Any.invokeMethod(vararg args: Any?, condition: MethodCondition): Any? {
    this::class.java.declaredMethods.firstOrNull { it.condition() }
        ?.let { it.isAccessible = true;return it(this, *args) }
    throw NoSuchMethodException()
}

fun isPad() =
    ClassUtils.loadClass("miui.os.Build")
        .getField("IS_TABLET")
        .getBoolean(null)

fun getValueByField(target: Any, fieldName: String, clazz: Class<*>? = null): Any? {
    var targetClass = clazz
    if (targetClass == null) {
        targetClass = target.javaClass
    }
    return try {
        val field = targetClass.getDeclaredField(fieldName)
        field.isAccessible = true
        field.get(target)
    } catch (e: Throwable) {
        if (targetClass.superclass == null) {
            null
        } else {
            getValueByField(target, fieldName, targetClass.superclass)
        }
    }
}

fun createBlurDrawable(
    view: View,
    blurRadius: Int,
    cornerRadius: Int,
    color: Int? = null
): Drawable? {
    try {
        val mViewRootImpl = XposedHelpers.callMethod(
            view,
            "getViewRootImpl"
        ) ?: return null
        val blurDrawable = XposedHelpers.callMethod(
            mViewRootImpl,
            "createBackgroundBlurDrawable"
        ) as Drawable
        XposedHelpers.callMethod(blurDrawable, "setBlurRadius", blurRadius)
        XposedHelpers.callMethod(blurDrawable, "setCornerRadius", cornerRadius)
        if (color != null) {
            XposedHelpers.callMethod(
                blurDrawable,
                "setColor",
                color
            )
        }
        return blurDrawable
    } catch (e: Throwable) {
        Log.e("Create BlurDrawable Error:$e")
        return null
    }
}

fun isBlurDrawable(drawable: Drawable?): Boolean {
    // 不够严谨，可以用
    if (drawable == null) {
        return false
    }
    val drawableClassName = drawable.javaClass.name
    return drawableClassName.contains("BackgroundBlurDrawable")
}