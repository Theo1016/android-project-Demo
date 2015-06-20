package com.theo.demo.ui.activity;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.theo.demo.R;
import com.theo.demo.ui.activity.fragment.OneFragment;
import com.theo.sdk.ui.activity.SDKBaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Theo on 15/6/18.
 */
public class HomeActivity extends SDKBaseActivity implements RadioGroup.OnCheckedChangeListener{

    private RadioButton rb_one, rb_two, rb_three, rb_four, rb_five;
    private RadioGroup rg_bottom;
    List<RadioButton> radioButtons = new ArrayList<RadioButton>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);
        initViews();
        initParams();
        initListeners();
    }

    @Override
    protected void initParams() {
        this.pushFragmentToBackStack(OneFragment.class,null);
    }

    @Override
    protected void initViews() {
        rg_bottom = (RadioGroup) this.findViewById(R.id.rg_bottom);
        rb_one= (RadioButton) this.findViewById(R.id.rb_one);
        rb_two= (RadioButton) this.findViewById(R.id.rb_two);
        rb_three= (RadioButton) this.findViewById(R.id.rb_three);
        rb_four= (RadioButton) this.findViewById(R.id.rb_four);
        rb_five = (RadioButton) this.findViewById(R.id.rb_five);
        radioButtons.add(rb_one);
        radioButtons.add(rb_two);
        radioButtons.add(rb_three);
        radioButtons.add(rb_four);
        radioButtons.add(rb_five);
        changeTextColor(rb_one);
    }

    @Override
    protected void initListeners() {
        rg_bottom.setOnCheckedChangeListener(this);
    }


    @Override
    protected String getCloseWarning() {
        return null;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fl_main_group;
    }

    /**
     * 改变tab字体颜色
     *
     * @param radioButton
     */
    private void changeTextColor(RadioButton radioButton) {
        for (RadioButton element : radioButtons) {
            if (radioButton == element) {
                element.setTextColor(getResources().getColor(R.color.red));
            } else {
                element.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
         switch(checkedId){
             case R.id.rb_one:
                 if(rb_one.isChecked()) {
                     changeTextColor(rb_one);
                     this.pushFragmentToBackStack(OneFragment.class,null);
                 }
             break;
             case R.id.rb_two:
                 if(rb_two.isChecked()) {
                     changeTextColor(rb_two);
                 }
                 break;
             case R.id.rb_three:
                 if(rb_three.isChecked()) {
                     changeTextColor(rb_three);
                 }
                 break;
             case R.id.rb_four:
                 if(rb_four.isChecked()) {
                     changeTextColor(rb_four);
                 }
                 break;
             case R.id.rb_five:
                 if(rb_five.isChecked()) {
                     changeTextColor(rb_five);
                 }
                 break;
         }
    }
}
