package com.merlin.view.recycler;

import android.view.View;

/**
 * Created by ncm on 16/11/11.
 */

public interface OnItemClickListener {

    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);

}
