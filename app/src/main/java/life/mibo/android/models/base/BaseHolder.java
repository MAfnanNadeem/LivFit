/*
 *  Created by Sumeet Kumar on 4/16/20 12:03 PM
 *  Copyright (c) 2020 . MI.BO All rights reserved.
 *  Last modified 4/16/20 12:03 PM
 *  Mibo Hexa - app
 */

package life.mibo.android.models.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import life.mibo.android.ui.base.ItemClickListener;

public abstract class BaseHolder<A> extends RecyclerView.ViewHolder implements BaseModel {

    public BaseHolder(@NonNull View itemView) {
        super(itemView);
    }

    abstract void bind(@Nullable A item, @Nullable ItemClickListener<A> listener);
}
