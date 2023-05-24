import org.gradle.api.Project

fun Project.webResources(config: WebResources.() -> Unit) {
    WebResources(this).run(config)
}

class WebResources(
    private val project: Project
) {
    fun richTextEditor() {
        with(project) {
            registerCopyWebStaticResourceTask("zepto.min.js", resourcesGeneratedDir)
            registerCopyWebStaticResourceTask("jquery.min.js", resourcesGeneratedDir)
            registerCopyWebStaticResourceTask("editor.md", resourcesGeneratedDir.resolve("editor.md"))
        }
    }
}