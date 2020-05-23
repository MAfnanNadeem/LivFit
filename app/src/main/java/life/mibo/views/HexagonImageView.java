/*
 *  Created by Sumeet Kumar on 5/17/20 3:46 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 5/17/20 3:46 PM
 *  Mibo Hexa - app
 */

package life.mibo.views;

import android.content.Context;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.ShaderImageView;
import com.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.github.siyamed.shapeimageview.shader.SvgShader;

public class HexagonImageView extends ShaderImageView {

    public HexagonImageView(Context context) {
        super(context);
    }

    public HexagonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HexagonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(com.github.siyamed.shapeimageview.R.raw.imgview_hexagon);
    }
}
