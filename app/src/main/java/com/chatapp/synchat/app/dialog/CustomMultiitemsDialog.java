package com.chatapp.synchat.app.dialog;

/**
 * created by  Adhash Team on 3/21/2017.
 */

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.CustomMultiItemsBgAdapter;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;

import java.util.List;

public class CustomMultiitemsDialog extends DialogFragment implements View.OnClickListener {

    private RecyclerView rvListItems;
    private AvnNextLTProDemiTextView tvTitle;
    private AvnNextLTProDemiButton btnNegative;

    private List<MultiTextDialogPojo> labelsList;
    private DialogItemClickListener listener;
    private String titleText, negativeBtnText;

    public interface DialogItemClickListener {
        void onDialogItemClick(int position);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.dialog_custom_black_background, container, false);

        tvTitle = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvTitle);
        btnNegative = (AvnNextLTProDemiButton) view.findViewById(R.id.btnNegative);

        rvListItems = (RecyclerView) view.findViewById(R.id.rvListItems);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvListItems.setLayoutManager(manager);

        rvListItems.addOnItemTouchListener(new RItemAdapter(getActivity(), rvListItems, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(listener != null) {
                    getDialog().dismiss();
                    listener.onDialogItemClick(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        if(titleText != null && !titleText.equalsIgnoreCase("")) {
            tvTitle.setText(titleText);
            tvTitle.setVisibility(View.VISIBLE);
        }

        if(negativeBtnText != null && !negativeBtnText.equalsIgnoreCase("")) {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setOnClickListener(CustomMultiitemsDialog.this);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(labelsList != null) {
            CustomMultiItemsBgAdapter adapter = new CustomMultiItemsBgAdapter(getActivity(), labelsList);
            rvListItems.setAdapter(adapter);
        }
    }

    public void setDialogItemClickListener(DialogItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnNegative) {
            getDialog().dismiss();
        }
    }

    public void setLabelsList(List<MultiTextDialogPojo> labelsList) {
        this.labelsList = labelsList;
    }

    public void setTitleText(String text) {
        this.titleText = text;
    }

    public void setNegativeButtonText(String text) {
        this.negativeBtnText = text;
    }
}

