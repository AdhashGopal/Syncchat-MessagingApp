package com.chatapp.synchat.app.dialog;

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
import com.chatapp.synchat.app.adapter.CustomMultiItemsAdapter;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;

import java.util.List;

/**
 * created by  Adhash Team on 2/16/2017.
 */
public class CustomMultiTextItemsDialog extends DialogFragment implements View.OnClickListener {

    private RecyclerView rvListItems;
    private AvnNextLTProDemiTextView tvTitle;
    private AvnNextLTProDemiButton btnNegative;

    private List<MultiTextDialogPojo> labelsList;
    private DialogItemClickListener listener;
    private CancelClickListener cancelListener;
    private String titleText, negativeBtnText;

    /**
     * DialogItemClickListener interface (getting position)
     */
    public interface DialogItemClickListener {
        void onDialogItemClick(int position);
    }

    /**
     * CancelClickListener interface
     */
    public interface CancelClickListener {
        void onDialogCancelClick();
    }

    /**
     * onCreateView layout binding
     *
     * @param inflater           make a view
     * @param container          parent of view
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.dialog_custom_multi_text_items, container, false);

        tvTitle = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvTitle);
        btnNegative = (AvnNextLTProDemiButton) view.findViewById(R.id.btnNegative);

        rvListItems = (RecyclerView) view.findViewById(R.id.rvListItems);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvListItems.setLayoutManager(manager);

        rvListItems.addOnItemTouchListener(new RItemAdapter(getActivity(), rvListItems, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (listener != null) {
                    getDialog().dismiss();
                    listener.onDialogItemClick(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        if (titleText != null && !titleText.equalsIgnoreCase("")) {
            tvTitle.setText(titleText);
            tvTitle.setVisibility(View.VISIBLE);
        }

        if (negativeBtnText != null && !negativeBtnText.equalsIgnoreCase("")) {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setOnClickListener(CustomMultiTextItemsDialog.this);
        }

        return view;
    }

    /**
     * set adapter view
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (labelsList != null) {
            CustomMultiItemsAdapter adapter = new CustomMultiItemsAdapter(getActivity(), labelsList);
            rvListItems.setAdapter(adapter);
        }
    }

    /**
     * Click event
     *
     * @param listener action perform
     */
    public void setDialogItemClickListener(DialogItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Click event
     *
     * @param cancelListener action perform
     */
    public void setCancelItemClickListener(CancelClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }


    /**
     * click action
     * @param view specific view
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnNegative) {
            if (listener != null) {
                getDialog().dismiss();
                //this.cancelListener.onDialogCancelClick();
            }
        }
    }

    /**
     *  set adapter data
     * @param labelsList arraylist value
     */
    public void setLabelsList(List<MultiTextDialogPojo> labelsList) {
        this.labelsList = labelsList;
    }

    /**
     * setTitleText
     * @param text value
     */
    public void setTitleText(String text) {
        this.titleText = text;
    }

    /**
     * setNegativeButtonText
     * @param text value
     */
    public void setNegativeButtonText(String text) {
        this.negativeBtnText = text;
    }
}
