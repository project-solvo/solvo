@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("org.solvo.web-build")
}

kotlin {
    sourceSets {
        val jsMain by getting {
            dependencies {
//                implementation(npm("stackedit-js", "1.0.7"))
            }
        }
    }
}

registerCopyWebStaticResourceTask("editormd.min.js", resourcesGeneratedDir)
registerCopyWebStaticResourceTask("zepto.min.js", resourcesGeneratedDir)
registerCopyWebStaticResourceTask("jquery.min.js", resourcesGeneratedDir)
registerCopyWebStaticResourceTask("editormd.css", resourcesGeneratedDir)
registerCopyWebStaticResourceTask("lib", resourcesGeneratedDir.resolve("lib"))
registerCopyWebStaticResourceTask("images", resourcesGeneratedDir.resolve("images"))
registerCopyWebStaticResourceTask("css", resourcesGeneratedDir.resolve("css"))
registerCopyWebStaticResourceTask("fonts", resourcesGeneratedDir.resolve("fonts"))

