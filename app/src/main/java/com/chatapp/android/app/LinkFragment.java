package com.chatapp.android.app;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.ConstantMethods;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.MessageItemChat;

import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 3/10/2017.
 */
public class LinkFragment extends Fragment {
    MessageDbController db;
    private ArrayList<MessageItemChat> mChatData;
    private String docid;
    private ArrayList<MessageItemChat> linklist;
    private RecyclerView rvMedia;
    private LinkAdapter linkadapter;
    private Getcontactname getcontactname;
    private static final String TAG = LinkFragment.class.getSimpleName();
    public LinkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linkfragment_layout, container, false);
        Session session = new Session(getActivity());
        db = CoreController.getDBInstance(getActivity());
        rvMedia = (RecyclerView) view.findViewById(R.id.rvMedia);
        LinearLayoutManager mediaManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvMedia.setLayoutManager(mediaManager);
        docid = session.getMediaDocid();
        mChatData = new ArrayList<>();
        linklist = new ArrayList<>();
        getcontactname = new Getcontactname(getActivity());
        loadFromDB();
        return view;
    }

    private void loadFromDB() {
        ArrayList<MessageItemChat> items;
        items = db.selectAllChatMessages(docid, ConstantMethods.getChatType(docid));
        mChatData.clear();
        mChatData.addAll(items);
        mediafile();
    }

    protected void mediafile() {
        for (int i = 0; i < mChatData.size(); i++) {
            String type = mChatData.get(i).getMessageType();
            int mtype = Integer.parseInt(type);
            MessageItemChat msgItem = mChatData.get(i);
            String txtMsg= msgItem.getTextMessage();
            Log.d(TAG, "mediafile: "+txtMsg);

           if (MessageFactory.web_link == mtype) {

                if (msgItem.getWebLink() != null) {
                    linklist.add(msgItem);
                }

            }
            else if(txtMsg!=null &&  Patterns.WEB_URL.matcher(txtMsg).matches()) {
                linklist.add(msgItem);
            }
        }
        linkadapter = new LinkAdapter(linklist);
        rvMedia.setAdapter(linkadapter);
    }

    public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.MyViewHolder> {

        private List<MessageItemChat> linklist;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public AvnNextLTProRegTextView linkname, tvViewMsg;

            public MyViewHolder(View view) {
                super(view);
                linkname = (AvnNextLTProRegTextView) view.findViewById(R.id.linkname);
                tvViewMsg = (AvnNextLTProRegTextView) view.findViewById(R.id.tvViewMsg);
            }
        }

        public LinkAdapter(List<MessageItemChat> linklist) {
            this.linklist = linklist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.linklist_single_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.linkname.setText(linklist.get(position).getWebLink());
            holder.tvViewMsg.setText(linklist.get(position).getTextMessage());

            stripUnderlines(holder.linkname);

            holder.tvViewMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessageItemChat msgItem = linklist.get(position);
                    getcontactname.navigateToChatViewpagewithmessageitems(msgItem, "webLink");
                }
            });
        }

        @Override
        public int getItemCount() {
            return linklist.size();
        }
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
