@file:Suppress("MemberVisibilityCanBePrivate")

package org.solvo.server.utils


import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.kotlin.asLog4jSuppliers
import org.apache.logging.log4j.message.EntryMessage
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.SimpleMessage
import org.apache.logging.log4j.spi.ExtendedLogger
import java.util.*
import kotlin.reflect.full.companionObject

object LogManagerKt {
//    fun getLogger(kClass: KClass<*>): Logger = LogManager.getLogger(kClass.java)
//    inline fun <reified T : Any> getLogger(): Logger = LogManager.getLogger(T::class.java)
//    fun getLogger(name: String): Logger = LogManager.getLogger(name)
//    fun getLogger(): Logger = LogManager.getLogger()


    /**
     * Logger instantiation by function. Use: `val log = logger()`. The logger will be named according to the
     * receiver of the function, which can be a class or object. An alternative for explicitly named loggers is
     * the [logger(String)] function.
     */
    inline fun <reified T : Any> logger() = loggerOf(T::class.java)

    /**
     * Named logger instantiation by function. Use: `val log = logger('MyLoggerName')`. Generally one should
     * prefer the `logger` function to create automatically named loggers, but this is useful outside of objects,
     * such as in top-level functions.
     */
    fun logger(name: String): KotlinLogger =
        KotlinLogger(LogManager.getContext(false).getLogger(name))

    /**
     * @see [logger]
     */
    @Deprecated("Replaced with logger(name)", replaceWith = ReplaceWith("logger"), level = DeprecationLevel.WARNING)
    fun namedLogger(name: String): KotlinLogger =
        KotlinLogger(LogManager.getContext(false).getLogger(name))

    private fun loggerDelegateOf(ofClass: Class<*>): ExtendedLogger {
        return LogManager.getContext(ofClass.classLoader, false).getLogger(unwrapCompanionClass(ofClass).name)
    }

    fun loggerOf(ofClass: Class<*>): KotlinLogger {
        return KotlinLogger(loggerDelegateOf(ofClass))
    }

    fun cachedLoggerOf(ofClass: Class<*>): KotlinLogger {
        return loggerCache.getOrPut(ofClass) { loggerOf(ofClass) }
    }

    // unwrap companion class to enclosing class given a Java Class
    private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
        return if (ofClass.enclosingClass?.kotlin?.companionObject?.java == ofClass) {
            ofClass.enclosingClass
        } else {
            ofClass
        }
    }

    private val loggerCache = Collections.synchronizedMap(SimpleLoggerLruCache(100))

    /**
     * A very simple cache for loggers, to be used with [cachedLoggerOf].
     */
    private class SimpleLoggerLruCache(private val maxEntries: Int) :
        LinkedHashMap<Class<*>, KotlinLogger>(maxEntries, 1f) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Class<*>, KotlinLogger>): Boolean {
            return size > maxEntries
        }
    }

}

/**
 * An adapter supporting cleaner syntax when calling a logger via Kotlin. This does not implement
 * the Log4j2 [Logger] interface, but instead limits logging methods to those that would be natural
 * to use from Kotlin. For example, the various logging-parameter methods necessary for Java are
 * eschewed in favor of Kotlin lambdas and String interpolation.
 *
 * If you do need access to the underlying [Logger] or [ExtendedLogger], it may be accessed via the
 * `delegate` property.
 *
 * One can use Kotlin's String interpolation for logging without the performance impact of
 * evaluating the parameters if the level is not enabled e.g.:
 *
 * ```
 * log.debug { "Value a = $a" }
 * ```
 *
 * In addition, the overloads provide methods in which the lambda is the *last* parameter rather than
 * the first as in the regular Log4j2 API. This means one can use Kotlin's last parameter lambda
 * outside of parentheses syntax e.g.:
 *
 * ```
 * log.error(exc) { "Unexpected exception evaluating $whatever." }
 * ```
 *
 * The adapter also provides a `runInTrace` utility that avoids having to call traceEnter and traceExit
 * and catch manually. Rather, simply call the `trace` method, passing in an [EntryMessage] and the block to
 * execute within trace enter/exit/catch calls. Location-awareness is currently broken for trace logging with this
 * method as the ExtendedLogger does not expose the enter/exit/catch calls with the FQCN parameter.
 *
 * We also use Kotlin's nullability features to specify unambiguously which parameters must be non-null
 * when passed.
 *
 * Lastly, the ExtendedLogger delegate is available if the underlying Log4j Logger is needed for some reason.
 * Access it via the `delegate` property.
 *
 * Therefore, until the Log4j2 API is updated and then this code is updated to match, location awareness will not
 * work for these calls.
 */
@Suppress("UNUSED", "MemberVisibilityCanBePrivate")
class KotlinLogger(val delegate: ExtendedLogger) {
    companion object {
        val FQCN: String = KotlinLogger::class.java.name
    }

    fun log(level: Level, marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, level, marker, msg, null)
    }

    fun log(level: Level, marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, level, marker, msg, t)
    }

    fun log(level: Level, marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, level, marker, msg, null)
    }

    fun log(level: Level, marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, level, marker, msg, t)
    }

    fun log(level: Level, marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, level, marker, msg, null)
    }

    fun log(level: Level, marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, level, marker, msg, t)
    }

    fun log(level: Level, msg: Message) {
        delegate.logIfEnabled(FQCN, level, null, msg, null)
    }

    fun log(level: Level, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, level, null, msg, t)
    }

    fun log(level: Level, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, level, null, msg, null)
    }

    fun log(level: Level, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, level, null, msg, t)
    }

    fun log(level: Level, msg: Any) {
        delegate.logIfEnabled(FQCN, level, null, msg, null)
    }

    fun log(level: Level, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, level, null, msg, t)
    }

    inline fun log(level: Level, supplier: () -> Any?) {
        if (delegate.isEnabled(level, null))
            delegate.logIfEnabled(FQCN, level, null, supplier(), null)
    }

    inline fun log(level: Level, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(level, null))
            delegate.logIfEnabled(FQCN, level, null, supplier(), t)
    }

    inline fun log(level: Level, marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(level, marker))
            delegate.logIfEnabled(FQCN, level, marker, supplier(), null)
    }

    inline fun log(level: Level, marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(level, marker))
            delegate.logIfEnabled(FQCN, level, marker, supplier(), t)
    }

    fun trace(marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, Level.TRACE, marker, msg, null)
    }

    fun trace(marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.TRACE, marker, msg, t)
    }

    fun trace(marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.TRACE, marker, msg, null)
    }

    fun trace(marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.TRACE, marker, msg, t)
    }

    fun trace(marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, Level.TRACE, marker, msg, null)
    }

    fun trace(marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.TRACE, marker, msg, t)
    }

    fun trace(msg: Message) {
        delegate.logIfEnabled(FQCN, Level.TRACE, null, msg, null)
    }

    fun trace(msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.TRACE, null, msg, t)
    }

    fun trace(msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.TRACE, null, msg, null)
    }

    fun trace(msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.TRACE, null, msg, t)
    }

    fun trace(msg: Any) {
        delegate.logIfEnabled(FQCN, Level.TRACE, null, msg, null)
    }

    fun trace(msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.TRACE, null, msg, t)
    }

    inline fun trace(supplier: () -> Any?) {
        if (delegate.isEnabled(Level.TRACE, null))
            delegate.logIfEnabled(FQCN, Level.TRACE, null, supplier(), null)
    }

    inline fun trace(t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.TRACE, null))
            delegate.logIfEnabled(FQCN, Level.TRACE, null, supplier(), t)
    }

    inline fun trace(marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.TRACE, marker))
            delegate.logIfEnabled(FQCN, Level.TRACE, marker, supplier(), null)
    }

    inline fun trace(marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.TRACE, marker))
            delegate.logIfEnabled(FQCN, Level.TRACE, marker, supplier(), t)
    }

    fun traceEntry(msg: CharSequence): EntryMessage {
        return delegate.traceEntry(SimpleMessage(msg))
    }

    inline fun traceEntry(supplier: () -> CharSequence): EntryMessage? {
        return if (delegate.isTraceEnabled) delegate.traceEntry(SimpleMessage(supplier())) else null
    }

    fun traceEntry(vararg paramSuppliers: () -> Any?): EntryMessage {
        return delegate.traceEntry(*paramSuppliers.asLog4jSuppliers())
    }

    fun traceEntry(vararg params: Any?): EntryMessage {
        return delegate.traceEntry(null, params)
    }

    fun traceEntry(message: Message): EntryMessage {
        return delegate.traceEntry(message)
    }

    inline fun <R : Any?> runInTrace(block: () -> R): R {
        return runInTrace(delegate.traceEntry(), block)
    }

    inline fun <R : Any?> runInTrace(entryMessage: EntryMessage, block: () -> R): R {
        delegate.traceEntry(entryMessage)
        return try {
            val result = block()
            when (result) {
                Unit -> delegate.traceExit(entryMessage)
                else -> delegate.traceExit(entryMessage, result)
            }
            result
        } catch (e: Throwable) {
            delegate.catching(e)
            throw e
        }
    }

    fun debug(marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, marker, msg, null)
    }

    fun debug(marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, marker, msg, t)
    }

    fun debug(marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, marker, msg, null)
    }

    fun debug(marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, marker, msg, t)
    }

    fun debug(marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, marker, msg, null)
    }

    fun debug(marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, marker, msg, t)
    }

    fun debug(msg: Message) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, null, msg, null)
    }

    fun debug(msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, null, msg, t)
    }

    fun debug(msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, null, msg, null)
    }

    fun debug(msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, null, msg, t)
    }

    fun debug(msg: Any) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, null, msg, null)
    }

    fun debug(msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.DEBUG, null, msg, t)
    }

    inline fun debug(supplier: () -> Any?) {
        if (delegate.isEnabled(Level.DEBUG))
            delegate.logIfEnabled(FQCN, Level.DEBUG, null, supplier(), null)
    }

    inline fun debug(t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.DEBUG))
            delegate.logIfEnabled(FQCN, Level.DEBUG, null, supplier(), t)
    }

    inline fun debug(marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.DEBUG, marker))
            delegate.logIfEnabled(FQCN, Level.DEBUG, marker, supplier(), null)
    }

    inline fun debug(marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.DEBUG, marker))
            delegate.logIfEnabled(FQCN, Level.DEBUG, marker, supplier(), t)
    }

    fun info(marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, Level.INFO, marker, msg, null)
    }

    fun info(marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.INFO, marker, msg, t)
    }

    fun info(marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.INFO, marker, msg, null)
    }

    fun info(marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.INFO, marker, msg, t)
    }

    fun info(marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, Level.INFO, marker, msg, null)
    }

    fun info(marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.INFO, marker, msg, t)
    }

    fun info(msg: Message) {
        delegate.logIfEnabled(FQCN, Level.INFO, null, msg, null)
    }

    fun info(msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.INFO, null, msg, t)
    }

    fun info(msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.INFO, null, msg, null)
    }

    fun info(msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.INFO, null, msg, t)
    }

    fun info(msg: Any) {
        delegate.logIfEnabled(FQCN, Level.INFO, null, msg, null)
    }

    fun info(msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.INFO, null, msg, t)
    }

    inline fun info(supplier: () -> Any?) {
        if (delegate.isEnabled(Level.INFO))
            delegate.logIfEnabled(FQCN, Level.INFO, null, supplier(), null)
    }

    inline fun info(t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.INFO))
            delegate.logIfEnabled(FQCN, Level.INFO, null, supplier(), t)
    }

    inline fun info(marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.INFO, marker))
            delegate.logIfEnabled(FQCN, Level.INFO, marker, supplier(), null)
    }

    inline fun info(marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.INFO, marker))
            delegate.logIfEnabled(FQCN, Level.INFO, marker, supplier(), t)
    }

    fun warn(marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, Level.WARN, marker, msg, null)
    }

    fun warn(marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.WARN, marker, msg, t)
    }

    fun warn(marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.WARN, marker, msg, null)
    }

    fun warn(marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.WARN, marker, msg, t)
    }

    fun warn(marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, Level.WARN, marker, msg, null)
    }

    fun warn(marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.WARN, marker, msg, t)
    }

    fun warn(msg: Message) {
        delegate.logIfEnabled(FQCN, Level.WARN, null, msg, null)
    }

    fun warn(msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.WARN, null, msg, t)
    }

    fun warn(msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.WARN, null, msg, null)
    }

    fun warn(msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.WARN, null, msg, t)
    }

    fun warn(msg: Any) {
        delegate.logIfEnabled(FQCN, Level.WARN, null, msg, null)
    }

    fun warn(msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.WARN, null, msg, t)
    }

    inline fun warn(supplier: () -> Any?) {
        if (delegate.isEnabled(Level.WARN))
            delegate.logIfEnabled(FQCN, Level.WARN, null, supplier(), null)
    }

    inline fun warn(t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.WARN))
            delegate.logIfEnabled(FQCN, Level.WARN, null, supplier(), t)
    }

    inline fun warn(marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.WARN, marker))
            delegate.logIfEnabled(FQCN, Level.WARN, marker, supplier(), null)
    }

    inline fun warn(marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.WARN, marker))
            delegate.logIfEnabled(FQCN, Level.WARN, marker, supplier(), t)
    }

    fun error(marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, Level.ERROR, marker, msg, null)
    }

    fun error(marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.ERROR, marker, msg, t)
    }

    fun error(marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.ERROR, marker, msg, null)
    }

    fun error(marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.ERROR, marker, msg, t)
    }

    fun error(marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, Level.ERROR, marker, msg, null)
    }

    fun error(marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.ERROR, marker, msg, t)
    }

    fun error(msg: Message) {
        delegate.logIfEnabled(FQCN, Level.ERROR, null, msg, null)
    }

    fun error(msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.ERROR, null, msg, t)
    }

    fun error(msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.ERROR, null, msg, null)
    }

    fun error(msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.ERROR, null, msg, t)
    }

    fun error(msg: Any) {
        delegate.logIfEnabled(FQCN, Level.ERROR, null, msg, null)
    }

    fun error(msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.ERROR, null, msg, t)
    }

    inline fun error(supplier: () -> Any?) {
        if (delegate.isEnabled(Level.ERROR))
            delegate.logIfEnabled(FQCN, Level.ERROR, null, supplier(), null)
    }

    inline fun error(t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.ERROR))
            delegate.logIfEnabled(FQCN, Level.ERROR, null, supplier(), t)
    }

    inline fun error(marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.ERROR, marker))
            delegate.logIfEnabled(FQCN, Level.ERROR, marker, supplier(), null)
    }

    inline fun error(marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.ERROR, marker))
            delegate.logIfEnabled(FQCN, Level.ERROR, marker, supplier(), t)
    }

    fun fatal(marker: Marker, msg: Message) {
        delegate.logIfEnabled(FQCN, Level.FATAL, marker, msg, null)
    }

    fun fatal(marker: Marker, msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.FATAL, marker, msg, t)
    }

    fun fatal(marker: Marker, msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.FATAL, marker, msg, null)
    }

    fun fatal(marker: Marker, msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.FATAL, marker, msg, t)
    }

    fun fatal(marker: Marker, msg: Any) {
        delegate.logIfEnabled(FQCN, Level.FATAL, marker, msg, null)
    }

    fun fatal(marker: Marker, msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.FATAL, marker, msg, t)
    }

    fun fatal(msg: Message) {
        delegate.logIfEnabled(FQCN, Level.FATAL, null, msg, null)
    }

    fun fatal(msg: Message, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.FATAL, null, msg, t)
    }

    fun fatal(msg: CharSequence) {
        delegate.logIfEnabled(FQCN, Level.FATAL, null, msg, null)
    }

    fun fatal(msg: CharSequence, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.FATAL, null, msg, t)
    }

    fun fatal(msg: Any) {
        delegate.logIfEnabled(FQCN, Level.FATAL, null, msg, null)
    }

    fun fatal(msg: Any, t: Throwable?) {
        delegate.logIfEnabled(FQCN, Level.FATAL, null, msg, t)
    }

    inline fun fatal(supplier: () -> Any?) {
        if (delegate.isEnabled(Level.FATAL))
            delegate.logIfEnabled(FQCN, Level.FATAL, null, supplier(), null)
    }

    inline fun fatal(t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.FATAL))
            delegate.logIfEnabled(FQCN, Level.FATAL, null, supplier(), t)
    }

    inline fun fatal(marker: Marker, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.FATAL, marker))
            delegate.logIfEnabled(FQCN, Level.FATAL, marker, supplier(), null)
    }

    inline fun fatal(marker: Marker, t: Throwable?, supplier: () -> Any?) {
        if (delegate.isEnabled(Level.FATAL, marker))
            delegate.logIfEnabled(FQCN, Level.FATAL, marker, supplier(), t)
    }

}
