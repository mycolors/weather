package com.fengniao.weather;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fengniao.weather.db.SavedWeather;
import com.fengniao.weather.gson.Weather;
import com.fengniao.weather.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class WeatherActivity extends AppCompatActivity {
    private ImageView bingPicImg;

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    MyViewPagerAdapter adapter;
    List<SavedWeather> list;
    ViewPager weatherViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        setContentView(R.layout.activity_weather);
        weatherViewPager = (ViewPager) findViewById(R.id.view_pager);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        list = DataSupport.findAll(SavedWeather.class);
        weatherViewPager.setOffscreenPageLimit(list.size());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        navigationView.setCheckedItem(R.id.control_county);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.control_county:
                        startActivity(new Intent(WeatherActivity.this, EditCountyActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        adapter = new MyViewPagerAdapter(getSupportFragmentManager());
        weatherViewPager.setAdapter(adapter);
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this,"1",Toast.LENGTH_SHORT).show();
        if (resultCode == RESULT_OK) {
            adapter.notifyDataSetChanged();
            list = DataSupport.findAll(SavedWeather.class);
            weatherViewPager.setOffscreenPageLimit(list.size());

        }
    }

    class MyViewPagerAdapter extends FragmentStatePagerAdapter {
        List<SavedWeather> list;

        MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
            list = DataSupport.findAll(SavedWeather.class);
        }

        @Override
        public void notifyDataSetChanged() {
            list = DataSupport.findAll(SavedWeather.class);
            super.notifyDataSetChanged();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return WeatherFragment.getNewInstance(list.get(position).getWeatherId());
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

    /**
     * 加载bing每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

}
