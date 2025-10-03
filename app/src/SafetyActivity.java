package com.nexkraft.ifleetUser.ui;

import android.content.Intent;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nexkraft.brac_fleet.R;
import com.nexkraft.ifleetUser.adapter.SafetyAdapter;
import com.nexkraft.ifleetUser.data.SafetyMessage;
import com.nexkraft.brac_fleet.databinding.ActivitySafetyBinding;
import com.nexkraft.ifleetUser.ui.base.BaseActivity;
import com.nexkraft.ifleetUser.ui.home.HomeActivity;
import com.nexkraft.ifleetUser.ui.trip.TripActivity;
import com.nexkraft.ifleetUser.utils.DialogsUtils;

import java.util.ArrayList;
import java.util.List;

public class SafetyActivity extends BaseActivity {

    private ActivitySafetyBinding safetyBinding;
    private List<SafetyMessage> messageList;
    private MaterialDialog materialDialog;
    private String source;
    private String type;
    private int position;

    @Override
    public void onConnection(boolean isConnected, String message) {
        runOnUiThread(() -> {
            safetyBinding.tvConnectionStatus.setText(message);
            safetyBinding.tvConnectionStatus.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void initBinding(ViewDataBinding viewDataBinding) {
        safetyBinding = (ActivitySafetyBinding) viewDataBinding;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_safety;
    }

    @Override
    public int getBottomNavigationMenuItemId() {
        return R.id.safety;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setToolbar(true);
        setToolbarWhite(true);
        setBottomNav(true);
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        TextView pageTitleView = findViewById(R.id.appbar).findViewById(R.id.user_name);
        pageTitleView.setText("Safety");

        messageList = new ArrayList<>(2);
        String[] messages = getResources().getStringArray(R.array.list_of_safetyMessage);
        int[] safetyIcons = new int[]{
                R.drawable.ic_wear_seatbelt,
                R.drawable.ic_focused,
                R.drawable.ic_safe_driving,
                R.drawable.ic_follow_rules,
                R.drawable.ic_car_body,
                R.drawable.ic_crosswalk,
                R.drawable.ic_safe_overtaking,
                R.drawable.ic_attentive};

        for (int count = 0; count < messages.length; count++){
            SafetyMessage safetyMessage = new SafetyMessage(messages[count], safetyIcons[count]);
            messageList.add(safetyMessage);
        }

        RecyclerView recyclerSafety = safetyBinding.idRecyclerSafety;
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getApplicationContext(), resId);
        recyclerSafety.setLayoutAnimation(animation);
        recyclerSafety.setNestedScrollingEnabled(false);

        // Use LinearLayoutManager instead of GridLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerSafety.setLayoutManager(layoutManager);
        SafetyAdapter safetyAdapter = new SafetyAdapter(messageList, this);
//        safetyAdapter.setOnItemClickListener(safetyItemClickListener);
        recyclerSafety.setAdapter(safetyAdapter);

        source = getIntent().getStringExtra("source");
        type = getIntent().getStringExtra("type");
        position = getIntent().getIntExtra("position", 0);
    }

    private SafetyAdapter.OnItemClickListener safetyItemClickListener = new SafetyAdapter.OnItemClickListener() {
        @Override
        public void onClick(View v, int position) {
            showDetailDialog(messageList.get(position).getSafetyMessage());
        }
    };

    private void showDetailDialog(String message) {
        DialogsUtils mDialogUtils = DialogsUtils.getInstance();
        mDialogUtils.initDialogs(this);
        mDialogUtils.setLayoutRes(R.layout.dialog_safety_detail);

        MaterialDialog mMaterialDialog = mDialogUtils.getDialog();
        TextView detailText = (TextView) mMaterialDialog.findViewById(R.id.txt_detail);
        detailText.setText(message);
        TextView btnAgree = (TextView) mMaterialDialog.findViewById(R.id.btn_back);
        btnAgree.setOnClickListener(v -> {
            mMaterialDialog.dismiss();
        });
        mMaterialDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (source.equals("home")) {
            onNavigation(this, HomeActivity.class);
        } else {
            onNavigation(new Intent(SafetyActivity.this, TripActivity.class).putExtra("type", type).putExtra("position", position));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
