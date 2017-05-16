package com.tiaopi.addsselect;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by TiaoPi on 2017/5/15.
 */

public class LocAdapter extends BaseQuickAdapter<String, BaseViewHolder>{

    public LocAdapter(List<String> data) {
        super(R.layout.dialog_loc_list_adapter, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.name_text_view,item)
                .addOnClickListener(R.id.name_text_view);
    }

}
