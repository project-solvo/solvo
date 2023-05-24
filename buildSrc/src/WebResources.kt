import org.gradle.api.Project

fun Project.webResources(config: WebResources.() -> Unit) {
    WebResources(this).run(config)
}

class WebResources(
    private val project: Project
) {
    fun richTextEditor() {
        with(project) {
            registerCopyWebStaticResourceTask("editormd.min.js", resourcesGeneratedDir)
            registerCopyWebStaticResourceTask("zepto.min.js", resourcesGeneratedDir)
            registerCopyWebStaticResourceTask("jquery.min.js", resourcesGeneratedDir)
            registerCopyWebStaticResourceTask("editormd.css", resourcesGeneratedDir)
            registerCopyWebStaticResourceTask("lib", resourcesGeneratedDir.resolve("lib"))
            registerCopyWebStaticResourceTask("images", resourcesGeneratedDir.resolve("images"))
            registerCopyWebStaticResourceTask("css", resourcesGeneratedDir.resolve("css"))
            registerCopyWebStaticResourceTask("fonts", resourcesGeneratedDir.resolve("fonts"))
            registerCopyWebStaticResourceTask("plugins", resourcesGeneratedDir.resolve("plugins"))
        }
    }
}