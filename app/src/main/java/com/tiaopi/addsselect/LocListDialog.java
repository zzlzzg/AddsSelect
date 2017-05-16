package com.tiaopi.addsselect;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by TiaoPi on 2017/5/16.
 */

public class LocListDialog extends DialogFragment implements View.OnClickListener,BaseQuickAdapter.OnItemChildClickListener {

    private final String chooseString = "请选择";

    private String[] tabStrings = new String[]{chooseString,chooseString,chooseString};

    private TabLayout tabLayout;
    private RecyclerView locRecyclerView;
    private ProgressBar loadingView;

    private LocAdapter locAdapter;
    private List<String> locList = new ArrayList<>();

    private int statePosition = 0;
    private int cityPosition = 0;
    private int regionPosition = 0;

    public static LocListDialog newInstance(){
        return new LocListDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View customView = inflater.inflate(R.layout.dialog_loc_list_layout,null);

        TextView agreeBtn = (TextView) customView.findViewById(R.id.agree_button);
        agreeBtn.setOnClickListener(this);

        tabLayout  = (TabLayout) customView.findViewById(R.id.loc_tab_layout);
        locRecyclerView = (RecyclerView) customView.findViewById(R.id.loc_recycler_view);
        loadingView = (ProgressBar) customView.findViewById(R.id.loading_view);

        showTabLayout();

        locRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        locAdapter = new LocAdapter(locList);
        locAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        locRecyclerView.setAdapter(locAdapter);
        locAdapter.setOnItemChildClickListener(this);

        showLoadingView();
        initData();

        return customView;
    }

    @Override
    public boolean onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (view.getId() == R.id.name_text_view) {
            switch (tabLayout.getSelectedTabPosition()) {
                case 0: //省份
                    statePosition = position;

                    if (tabStrings[0].equals(chooseString)) {

                        tabStrings[0] = getModel().get(position).getAreaName();
                        tabLayout.getTabAt(0).setText(tabStrings[0]);

                        if (tabLayout.getTabCount() < 2) {
                            tabLayout.addTab(tabLayout.newTab().setText(tabStrings[1]));
                            tabLayout.getTabAt(1).select();
                        }

                    }else if (!tabStrings[0].equals(getModel().get(position).getAreaName())) {

                        if (tabLayout.getTabCount() == 3) {
                            tabLayout.removeTabAt(2);
                            tabStrings[2] = chooseString;
                        }

                        tabLayout.getTabAt(1).select();
                        tabStrings[1] = chooseString;
                        tabLayout.getTabAt(1).setText(tabStrings[1]);

                        tabStrings[0] = getModel().get(position).getAreaName();
                        tabLayout.getTabAt(0).setText(tabStrings[0]);

                    }else if (tabStrings[0].equals(getModel().get(position).getAreaName()))  {
                        tabLayout.getTabAt(1).select();
                    }


                    break;
                case 1: //城市

                    cityPosition = position;

                    String cityName = getModel().get(statePosition).getCities().get(position).getAreaName();

                    if (tabStrings[1].equals(chooseString)) {

                        tabStrings[1] = cityName;
                        tabLayout.getTabAt(1).setText(tabStrings[1]);

                        if (tabLayout.getTabCount() < 3) {
                            tabLayout.addTab(tabLayout.newTab().setText(tabStrings[2]));
                            tabLayout.getTabAt(2).select();
                        }

                    }else if (!tabStrings[1].equals(cityName)) {

                        tabLayout.getTabAt(2).select();
                        tabStrings[2] = chooseString;
                        tabLayout.getTabAt(2).setText(tabStrings[2]);

                        tabStrings[1] = cityName;
                        tabLayout.getTabAt(1).setText(tabStrings[1]);

                    }else if (tabStrings[1].equals(cityName))  {
                        tabLayout.getTabAt(2).select();
                    }

                    break;
                case 2: //县区
                    regionPosition = position;
                    tabStrings[2] = getModel().get(statePosition).getCities()
                            .get(cityPosition).getCounties().get(regionPosition).getAreaName();
                    tabLayout.getTabAt(2).setText(tabStrings[2]);
                    break;
            }

        }
        return true;
    }

    private void initData(){
        /**
         * 显示省份
         */
        showStateData();
    }

    public void showTabLayout(){
        tabLayout.addTab(tabLayout.newTab().setText(tabStrings[0]));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showStateData();
                        break;
                    case 1:
                        showCityData();
                        break;
                    case 2:
                        showRegionData();
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showStateData(){
        /**
         * 显示省份
         */
        locList.clear();
        Observable.from(getModel())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LocListModel.LOCBean>() {
                    @Override
                    public void call(LocListModel.LOCBean stateBean) {
                        locList.add(stateBean.getAreaName());
                        if (getModel().size() == locList.size()) {
                            showRecyclerData(locList);
                        }
                    }
                });
    }
    private void showCityData(){

        /**
         * 显示省份
         */
        locList.clear();
        Observable.from(getModel().get(statePosition).getCities())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LocListModel.LOCBean.CitiesBean>() {
                    @Override
                    public void call(LocListModel.LOCBean.CitiesBean cityBean) {
                        locList.add(cityBean.getAreaName());
                        if (getModel().get(statePosition).getCities().size() == locList.size()) {
                            showRecyclerData(locList);
                        }
                    }
                });
    }

    private void showRegionData(){
        /**
         * 显示省份
         */
        locList.clear();
        Observable.from(getModel().get(statePosition).getCities().get(cityPosition).getCounties())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LocListModel.LOCBean.CitiesBean.CountiesBean>() {
                    @Override
                    public void call(LocListModel.LOCBean.CitiesBean.CountiesBean regionBean) {

                        locList.add(regionBean.getAreaName());
                        if (getModel().get(statePosition).getCities().get(cityPosition).getCounties().size() == locList.size()) {
                            showRecyclerData(locList);
                        }
                    }
                });
    }

    private void showRecyclerData(List<String> locList){
        closeLoadingView();
        if (locList != null && locList.size() != 0) {
            locAdapter.setNewData(locList);
        }else {
            Toast.makeText(getActivity(),"没数据",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.agree_button: //选取
                final TextView selectAdds = (TextView) getActivity().findViewById(R.id.select_adds_text_view);
                selectAdds.setText(tabStrings[0] + tabStrings[1] + tabStrings[2] );
                dismiss();
                break;
        }
    }

    private void showLoadingView(){
        loadingView.setVisibility(View.VISIBLE);
    }

    private void closeLoadingView(){
        loadingView.setVisibility(View.GONE);
    }

    List<LocListModel.LOCBean> LOC;
    private List<LocListModel.LOCBean> getModel(){
        if (LOC == null) {
            Gson gson = new Gson();
            String json = Utils.getStrFromRaw(getActivity(),R.raw.loclist);
            LocListModel locModel = gson.fromJson(json,LocListModel.class);
            LOC = locModel.getLOC();
        }
        return LOC;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window mWindow = getDialog().getWindow();
        WindowManager.LayoutParams mLayoutParams = mWindow.getAttributes();
        mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.BOTTOM;
        mWindow.setAttributes(mLayoutParams);
    }

}
