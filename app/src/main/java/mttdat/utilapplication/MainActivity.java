package mttdat.utilapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mttdat.utilapplication.demo.audioprogress.AudioProgressActivity;
import mttdat.utilapplication.demo.centergrid.CenterGridActivity;
import mttdat.utilapplication.demo.flowlist.FlowListActivity;
import mttdat.utilapplication.demo.textwrapwidth.TextWrapWidthActivity;
import mttdat.utils.RecyclerAdapter;
import mttdat.utils.RecyclerViewHolder;

public class MainActivity extends AppCompatActivity {

    private final String DEMO_FLOW_LIST = "Flow List";
    private final String DEMO_CENTER_GRID = "Center Grid";
    private final String DEMO_TEXT_WRAP_WIDTH = "Text Wrap Width";
    private final String DEMO_AUDIO_PROGRESS = "Audio Progress";

    RecyclerView rvDemoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvDemoList = findViewById(R.id.rv_demo);

        ArrayList<String> listDemos = new ArrayList<>();
        listDemos.add(DEMO_FLOW_LIST);
        listDemos.add(DEMO_CENTER_GRID);
        listDemos.add(DEMO_TEXT_WRAP_WIDTH);
        listDemos.add(DEMO_AUDIO_PROGRESS);

        DemoAdapter demoAdapter = new DemoAdapter(R.layout.item_demo_activity, listDemos);

        demoAdapter.setOnItemClickListenerWithItem(new RecyclerAdapter.OnItemClickWithItemListener<String>() {
            @Override
            public void onItemClick(View view, int position, String itemDemo) {

                Intent intent = null;

                switch (itemDemo){
                    case DEMO_FLOW_LIST:
                        intent = new Intent(MainActivity.this, FlowListActivity.class);
                        break;

                    case DEMO_CENTER_GRID:
                        intent = new Intent(MainActivity.this, CenterGridActivity.class);
                        break;

                    case DEMO_TEXT_WRAP_WIDTH:
                        intent = new Intent(MainActivity.this, TextWrapWidthActivity.class);
                        break;

                    case DEMO_AUDIO_PROGRESS:
                        intent = new Intent(MainActivity.this, AudioProgressActivity.class);
                        break;
                }

                if(intent == null){
                    return;
                }

                startActivity(intent);
                overridePendingTransition(R.anim.fade_left_in, R.anim.fade_left_out);
            }
        });

        rvDemoList.setAdapter(demoAdapter);
    }

    private static class DemoAdapter extends RecyclerAdapter<String> {

        public DemoAdapter(int resource, List<String> listItem) {
            super(resource, listItem);
        }

        @Override
        public RecyclerViewHolder createHolder(View v, int viewType) {
            return new DemoHolder(v);
        }
    }

    private static class DemoHolder extends RecyclerViewHolder<String> {

        TextView tvActivityDemo;

        public DemoHolder(View itemView) {
            super(itemView);

            tvActivityDemo = itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void onBind(String textDemo) {
            tvActivityDemo.setText(textDemo);
        }
    }
}