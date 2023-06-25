package star.sky.voyager.utils.voyager

import android.content.Context
import android.util.TypedValue
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClass
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XC_MethodHook
import star.sky.voyager.utils.api.isStatic
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

/**
 * 扩展函数 通过遍历方法数组 返回符合条件的方法数组
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun Array<Method>.findAllMethods(condition: MethodCondition): Array<Method> {
    return this.filter { it.condition() }.onEach { it.isAccessible = true }.toTypedArray()
}

/**
 * 通过条件获取方法数组
 * @param clz 类
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun findAllMethods(
    clz: Class<*>,
    findSuper: Boolean = false,
    condition: MethodCondition
): List<Method> {
    var c = clz
    val arr = ArrayList<Method>()
    arr.addAll(c.declaredMethods.findAllMethods(condition))
    if (findSuper) {
        while (c.superclass?.also { c = it } != null) {
            arr.addAll(c.declaredMethods.findAllMethods(condition))
        }
    }
    return arr
}

/**
 * 通过条件获取方法数组
 * @param clzName 类名
 * @param classLoader 类加载器
 * @param findSuper 是否查找父类
 * @param condition 条件
 * @return 符合条件的方法数组
 */
fun findAllMethods(
    clzName: String,
    classLoader: ClassLoader = EzXHelper.classLoader,
    findSuper: Boolean = false,
    condition: MethodCondition
): List<Method> {
    return findAllMethods(loadClass(clzName, classLoader), findSuper, condition)
}

fun dp2px(context: Context, dpValue: Float): Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    dpValue,
    context.resources.displayMetrics
).toInt()

/**
 * 执行数组中所有的unhook
 */
fun Array<XC_MethodHook.Unhook>.unhookAll() {
    this.forEach { it.unhook() }
}

fun Iterable<XC_MethodHook.Unhook>.unhookAll() {
    this.forEach { it.unhook() }
}