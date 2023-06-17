package org.solvo.web.document.parameters

import org.solvo.model.annotations.Stable

@Stable
interface PathVariable {
    val pathName: String
}