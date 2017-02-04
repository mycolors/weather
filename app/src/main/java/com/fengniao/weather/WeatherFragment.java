package com.fengniao.weather;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengniao.weather.db.SavedWeather;
import com.fengniao.weather.gson.Forecast;
import com.fengniao.weather.gson.Weather;
import com.fengniao.weather.service.AutoUpdateService;
import com.fengniao.weather.util.HttpUtil;
import com.fengniao.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



/**
 * A simple {@link Fragment} subclass.
 */
public class WeatherFragment extends Fragment {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdatetTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private Button navBtn;
    private FloatingActionButton addBtn;
    public SwipeRefreshLayout swipeRefresh;

    public static WeatherFragment getNewInstance(String weatherId) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putString("weatherId", weatherId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public WeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化各种控件
        initView();
        String weatherId = getArguments().getString("weatherId");
        String weatherString = DataSupport.where("weatherId = ?", weatherId).find(SavedWeather.class).get(0).getWeatherData();
        if (weatherString != null) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //无缓存时去服务器查询天气
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        final String finalWeatherId = weatherId;
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(finalWeatherId);
            }
        });
        navBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((WeatherActivity) getActivity()).drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().startActivityForResult(new Intent(getContext(), AddCountyActivity.class), 1);
            }
        });
    }

    public void initView() {
        weatherLayout = (ScrollView) getView().findViewById(R.id.weather_layout);
        titleCity = (TextView) getView().findViewById(R.id.title_city);
        titleUpdatetTime = (TextView) getView().findViewById(R.id.title_update_time);
        degreeText = (TextView) getView().findViewById(R.id.degree_text);
        weatherInfoText = (TextView) getView().findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) getView().findViewById(R.id.forecast_layout);
        aqiText = (TextView) getView().findViewById(R.id.aqi_text);
        pm25Text = (TextView) getView().findViewById(R.id.pm25_text);
        comfortText = (TextView) getView().findViewById(R.id.comfort_text);
        carWashText = (TextView) getView().findViewById(R.id.car_wash_text);
        sportText = (TextView) getView().findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        navBtn = (Button) getView().findViewById(R.id.nav_button);
        addBtn = (FloatingActionButton) getView().findViewById(R.id.btn_add);
    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SavedWeather savedWeather = new SavedWeather();
                            savedWeather.setWeatherId(weatherId);
                            savedWeather.setWeatherData(responseText);
                            savedWeather.updateAll("weatherId = ?", weatherId);
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(getContext(), "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        Intent intent = new Intent(getContext(), AutoUpdateService.class);
        getActivity().startService(intent);
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdatetTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
