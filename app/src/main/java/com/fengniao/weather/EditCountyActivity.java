package com.fengniao.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.fengniao.weather.db.SavedWeather;

import org.litepal.crud.DataSupport;

import java.util.List;

public class EditCountyActivity extends AppCompatActivity {

    boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_county);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("编辑城市");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<SavedWeather> list = DataSupport.findAll(SavedWeather.class);
        Log.i("mycolors", list.get(0).getWeatherData());
        MyAdapter adapter = new MyAdapter(list);
        recyclerView.setAdapter(adapter);
    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        MyAdapter(List<SavedWeather> list) {
            this.list = list;
        }

        List<SavedWeather> list;

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EditCountyActivity.this).inflate(R.layout.item_list_edit_county, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.county.setText(list.get(position).getCountyName());
            //长按删除城市
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DataSupport.deleteAll(SavedWeather.class, "weatherId = ?", list.get(position).getWeatherId());
                    list.remove(list.get(position));
                    if (list.isEmpty()) {
                        setResult(RESULT_OK);
                        finish();
                    }
                    isChange = true;
                    notifyDataSetChanged();
                    return true;
                }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView county;

            MyViewHolder(View itemView) {
                super(itemView);
                county = (TextView) itemView.findViewById(R.id.text_county);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isChange) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }
}
