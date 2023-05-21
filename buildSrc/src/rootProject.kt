import org.gradle.api.Project
import java.time.Instant
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal object RootProjectHolder {
    internal lateinit var _rootProject: Pair<Project, Instant>
        private set

    val time: Instant get() = _rootProject.second
    fun setRootProject(
        project: Project
    ) {
        _rootProject = project to Instant.now()
    }
}

val rootProject: Project get() = RootProjectHolder._rootProject.first

fun <R> projectLevelCache(calculate: () -> R): ReadOnlyProperty<Any?, R> {
    return object : ReadOnlyProperty<Any?, R> {
        @Volatile
        var currentInstance: Pair<R, Instant>? = null

        @Synchronized
        override fun getValue(thisRef: Any?, property: KProperty<*>): R {
            val current = currentInstance
            if (current == null) {
                val new = calculate()
                currentInstance = new to RootProjectHolder.time
                return new
            }

            if (current.second != RootProjectHolder.time) {
                // outdated
                val new = calculate()
                currentInstance = new to RootProjectHolder.time
                return new
            }

            return current.first
        }

    }
}
