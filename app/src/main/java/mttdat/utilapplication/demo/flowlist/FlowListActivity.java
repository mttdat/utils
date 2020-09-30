package mttdat.utilapplication.demo.flowlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.flowlist.FlowList;
import com.flowlist.FlowListLayoutManager;

import java.util.ArrayList;

import mttdat.drapdroputils.FrameLayoutPlus;
import mttdat.utilapplication.R;
import mttdat.utils.ViewUtils;

public class FlowListActivity extends AppCompatActivity {

    FrameLayoutPlus flpDragDropZone;
    FlowList flowList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_list);

        flowList = findViewById(R.id.fl);
        flpDragDropZone = findViewById(R.id.flp_drag_drop_zone);

        /* *********************** */

        FlowListLayoutManager flowListLayoutManager =
                new FlowListLayoutManager(60, ViewUtils.getScreenWidth(this));
        flowListLayoutManager.setSpacePaddingHorizontal(90);

//        flowList.setOnDragDropListener(new FlowList.OnDragDropListener() {
//            @Override
//            public void onDragStart() {
//
//            }
//
//            @Override
//            public void onDragEnter(View view, int row, int left, int right) {
//                view.setBackgroundResource(com.flowlist.R.color.colorAccent);
//            }
//
//            @Override
//            public void onDragExit(View view, int row, int left, int right) {
//                view.setBackgroundResource(com.flowlist.R.color.transparent);
//            }
//
//            @Override
//            public void onSwap() {
//
//            }
//
//            @Override
//            public void onDragEnd(View view, int row, int left, int right) {
//                view.setBackgroundResource(com.flowlist.R.color.transparent);
//            }
//        });

        flowList.setLayoutManager(flowListLayoutManager);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("He");
        strings.add("will");
        strings.add("go");
        strings.add("to");
        strings.add("school");
        strings.add("with");
        strings.add("his");
        strings.add("friends");
        strings.add("tomorrow");

        TestFlowListAdapter testFlowListAdapter = new TestFlowListAdapter(R.layout.item_flow_list, strings);

        flowList.setAdapter(testFlowListAdapter);


    }
}
