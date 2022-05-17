package com.germainkevin.collapsingtopbar

import androidx.compose.ui.graphics.Color

/**
 * Default colors used in the [CollapsingTopBar]
 * */
interface CollapsingTopBarColors {

    /**
     * The background color of the [CollapsingTopBar]
     * */
    var backgroundColor: Color

    /**
     * The default color for the content inside [CollapsingTopBar]
     * */
    var contentColor: Color
}

class CollapsingTopBarColorsImpl(
    override var backgroundColor: Color,
    override var contentColor: Color
) : CollapsingTopBarColors