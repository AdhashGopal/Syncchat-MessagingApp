package com.chatapp.android.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.model.AudioFilePojo;

import java.util.ArrayList;
import java.util.List;


public class AudioFilesListAdapter extends RecyclerView.Adapter<AudioFilesListAdapter.AudioFilesViewHolder>
        implements Filterable {

    public List<AudioFilePojo> mDisplayedValues;
    private Context mContext;
    private List<AudioFilePojo> mOriginalValues;

    private AudioFileListItemClickListener listener;

    /**
     * Create constructor
     *
     * @param mContext The activity object inherits the Context object
     * @param dataList list of value
     */
    public AudioFilesListAdapter(Context mContext, List<AudioFilePojo> dataList) {
        this.mContext = mContext;
        this.mDisplayedValues = dataList;
        this.mOriginalValues = dataList;
    }

    /**
     * handling the FileItemClickListener
     * @param listener
     */
    public void setFileItemClickListener(AudioFileListItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public AudioFilesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_audio_files_list, parent, false);

        AudioFilesViewHolder holder = new AudioFilesViewHolder(view);
        return holder;
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(AudioFilesViewHolder holder, int position) {
        final AudioFilePojo audioItem = mDisplayedValues.get(position);

       /* long duration = Long.parseLong(audioItem.getDuration()) / 1000;
        long durationQuen = duration / 60;
        long durationRem = duration % 60;
        String strDuration;
        if(durationRem < 10) {
            strDuration = durationQuen + ":0" + durationRem;
        } else {
            strDuration = durationQuen + ":" + durationRem;
        }*/
        holder.tvDuration.setText(audioItem.getDuration());
        holder.tvFileName.setText(audioItem.getFileName());
//        holder.tvDuration.setText(strDuration);

        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onFileItemClick(audioItem);
                }
            });
        }
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }

    /**
     * filter the array value
     * @return filter value
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    mDisplayedValues = (ArrayList<AudioFilePojo>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                try {


                    ArrayList<AudioFilePojo> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {


                            String fileName = mOriginalValues.get(i).getFileName();
                            if (fileName.toLowerCase().contains(constraint)) {

                                AudioFilePojo audioItem = new AudioFilePojo();
                                audioItem.setFileName(mOriginalValues.get(i).getFileName());
                                audioItem.setFilePath(mOriginalValues.get(i).getFilePath());
                                audioItem.setDuration(mOriginalValues.get(i).getDuration());

                                FilteredArrList.add(audioItem);
                            }


                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
        return filter;
    }

    /**
     *  FileItemClick interface
     */
    public interface AudioFileListItemClickListener {
        void onFileItemClick(AudioFilePojo item);
    }

    /**
     * widgets view holder
     */
    public class AudioFilesViewHolder extends RecyclerView.ViewHolder {

        public AvnNextLTProRegTextView tvFileName, tvDuration;

        public AudioFilesViewHolder(View itemView) {
            super(itemView);

            tvFileName = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvFileName);
            tvDuration = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvDuration);
        }
    }

}
