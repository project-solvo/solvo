package org.solvo.model.api

import org.solvo.model.annotations.Stable
import org.solvo.model.foundation.Uuid

interface HasCoid {
    @Stable
    val coid: Uuid
}