package com.chatapp.android.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.BuildConfig;
import com.chatapp.android.R;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.utils.ConstantMethods;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 3/10/2017.
 */
public class DocumentFragment extends Fragment {
    MessageDbController db;
    private ArrayList<MessageItemChat> mChatData;
    private String docid;
    private ArrayList<String> documentlist;
    private RecyclerView rvMedia;
    private HorizontalAdapter horizontalAdapter;

    /**
     * constructor
     */
    public DocumentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.documentfragement_layout, container, false);
        Session session = new Session(getActivity());
        db = CoreController.getDBInstance(getActivity());
        rvMedia = (RecyclerView) view.findViewById(R.id.rvMedia);
        LinearLayoutManager mediaManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvMedia.setLayoutManager(mediaManager);
        docid = session.getMediaDocid();
        mChatData = new ArrayList<>();
        documentlist = new ArrayList<String>();
        loadFromDB();
        return view;
    }

    /***
     * Getting database value
     */
    private void loadFromDB() {
        ArrayList<MessageItemChat> items;
        items = db.selectAllChatMessages(docid, ConstantMethods.getChatType(docid));
        mChatData.clear();
        mChatData.addAll(items);
        mediafile();
    }


    /**
     * Getting downloaded document file
     */
    protected void mediafile() {
        for (int i = 0; i < mChatData.size(); i++) {
            String type = mChatData.get(i).getMessageType();
            int mtype = Integer.parseInt(type);

            if (MessageFactory.document == mtype) {
                MessageItemChat msgItem = mChatData.get(i);
                if (msgItem.getChatFileLocalPath() != null) {
                    String path = msgItem.getChatFileLocalPath();
                    File file = new File(path);
                    if (file.exists()) {
                        Uri pathuri = Uri.fromFile(file);
                        documentlist.add(path);
                    }
                }

            }
        }
        horizontalAdapter = new HorizontalAdapter(documentlist);
        rvMedia.setAdapter(horizontalAdapter);
        rvMedia.addOnItemTouchListener(new RItemAdapter(getActivity(), rvMedia, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(documentlist.get(position));
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                PackageManager packageManager = getContext().getPackageManager();
                Intent testIntent = new Intent(Intent.ACTION_VIEW);
                testIntent.setType(mimeType);
                List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.size() > 0) {
                    File file = new File(documentlist.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file);
                    intent.setDataAndType(uri, mimeType);
                    startActivity(intent);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    /**
     * Document Horizontal Adapter view
     */
    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<String> horizontalList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public AvnNextLTProDemiTextView doc_name;
            public AvnNextLTProRegTextView doc_listname;
            public ImageView document_pic;

            public MyViewHolder(View view) {
                super(view);
                doc_name = (AvnNextLTProDemiTextView) view.findViewById(R.id.doc_name);
                doc_listname = (AvnNextLTProRegTextView) view.findViewById(R.id.doc_listname);
                document_pic = (ImageView) view.findViewById(R.id.document_pic);
            }
        }

        public HorizontalAdapter(List<String> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.document_recylarlist_view, parent, false);

            return new MyViewHolder(itemView);
        }

        /**
         * Type of document view
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            String[] array = horizontalList.get(position).split("/");
            String fileName = array[array.length - 1];
            holder.doc_name.setText(fileName);

            String extension = FileUploadDownloadManager.getFileExtnFromPath(fileName);

            if (extension.contains("txt")) {
                holder.document_pic.setImageResource(R.drawable.ic_media_txt);
                holder.doc_listname.setText("Text");
            } else if (extension.contains("doc")) {
                holder.document_pic.setImageResource(R.drawable.ic_media_doc);
                holder.doc_listname.setText("Document");
            } else if (extension.contains("ppt")) {
                holder.document_pic.setImageResource(R.drawable.ic_media_ppt);
                holder.doc_listname.setText("PPT");
            } else if (extension.contains("xls")) {
                holder.document_pic.setImageResource(R.drawable.ic_media_xls);
                holder.doc_listname.setText("XL Document");
            } else if (extension.contains("pdf")) {
                holder.document_pic.setImageResource(R.drawable.ic_media_pdf);
                holder.doc_listname.setText("PDF");
            } else {
                holder.document_pic.setImageResource(0);
                holder.doc_listname.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return horizontalList.size();
        }
    }

}
