package com.luckyliuqs.smallloveweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.luckyliuqs.smallloveweather.R;
import com.luckyliuqs.smallloveweather.WeatherActivity;
import com.luckyliuqs.smallloveweather.db.City;
import com.luckyliuqs.smallloveweather.db.County;
import com.luckyliuqs.smallloveweather.db.Province;
import com.luckyliuqs.smallloveweather.util.HttpUtil;
import com.luckyliuqs.smallloveweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    //选中的市
    private City selectedCity;
    //当前选中的级别
    private int currenLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.i("Activity", "创建fragment视图");
        //初始化View
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //初始化控件
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        //初始化Adapter
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currenLevel == LEVEL_PROVINCE) {
                    Log.i("ChooseAreaFragment", "点击省："+provinceList.get(position).getProvinceName());
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currenLevel == LEVEL_CITY){
                    Log.i("ChooseAreaFragment", "点击市："+cityList.get(position).getCityName());
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currenLevel == LEVEL_COUNTY){
                    //获取到城市名称
                    String countyName = countyList.get(position).getCountyName();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("countyName",countyName);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currenLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currenLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到就去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currenLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }
    /**
     * 查询选中省内的所有市，优先从数据库查询，如果没有查询到就去服务器上查询
     */
    private void queryCities() {
        Log.i("ChooseAreaFragment", "进入查询这个省的所有市方法");
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            Log.i("Activity", "转变前currenLevel:"+currenLevel);
            currenLevel = LEVEL_CITY;
            Log.i("Activity", "转变后currenLevel:"+currenLevel);
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            Log.i("ChooseAreaFragment", "本地没有查询到："+selectedProvince.getProvinceName()+"省的所有市，准备进行服务器获取！");
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
    /**
     * 查询选中市内的所有县，优先从数据库查询，如果没有查询到就去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County County : countyList) {
                dataList.add(County.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currenLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            Log.i("ChooseAreaFragment", "本地没有查询到："+selectedCity.getCityName()+" 市的所有县，准备进行服务器获取！");
            String address = "http://guolin.tech/api/china/" + provinceCode+"/"+cityCode;
            queryFromServer(address, "county");
        }
    }
    /**
     * 根据输入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address,final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    //储存省数据
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    Log.i("ChooseAreaFragment", "进入服务器查询省内所有市名称方法！");
                    //储存市数据
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    Log.i("ChooseAreaFragment", "进入服务器查询市内所有市名称方法！");
                    //储存县数据
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }
                //成功从服务器上获取并保存了数据，调用方法展示数据
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭等待进度条对话框
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                Log.i("ChooseAreaFragment", "成功从服务器获取到市名称并储存到本地");
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //关闭等待进度条对话框
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("请等待");
            progressDialog.setMessage("正在加载省市县数据...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
