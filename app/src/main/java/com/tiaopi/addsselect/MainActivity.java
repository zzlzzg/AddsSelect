package com.tiaopi.addsselect;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView selectAdds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectAdds = (TextView) findViewById(R.id.select_adds_text_view);
        selectAdds.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        FragmentTransaction mFragTransaction = getFragmentManager().beginTransaction();
        Fragment fragment =  getFragmentManager().findFragmentByTag("dialogFragment");
        if(fragment != null){
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        LocListDialog locListDialog =LocListDialog.newInstance();
        locListDialog.show(mFragTransaction, "dialogFragment");
    }

}
