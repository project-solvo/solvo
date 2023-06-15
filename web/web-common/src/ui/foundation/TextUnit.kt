package org.solvo.web.ui.foundation

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


operator fun TextUnit.plus(sp: TextUnit): TextUnit {
    return (this.value + sp.value).sp
}

operator fun TextUnit.minus(sp: TextUnit): TextUnit {
    require(this.isSp && sp.isSp)
    return (this.value - sp.value).sp
}
