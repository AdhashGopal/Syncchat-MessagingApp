package com.chatapp.android.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.Session;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * created by  Adhash Team on 4/18/2017.
 */
public class Networkusage extends CoreActivity {
    RecyclerView listnetworkusage;
    ImageView ibBack;
    private Networkusageadapter adapter;
    ArrayList<String> networkstate;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.networkusagelayout);
        getSupportActionBar().hide();
        networkstate = new ArrayList<>();
        session = new Session(Networkusage.this);
        listnetworkusage = (RecyclerView) findViewById(R.id.listnetworkusage);
        listnetworkusage.setHasFixedSize(true);
        LinearLayoutManager mediaManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listnetworkusage.setLayoutManager(mediaManager);
        ibBack = (ImageView) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String sentmessage_byte = size(session.getsentmessagelength());
        String receviedmessage_byte = size(session.getreceviedmessagelength());
        String sentmedia_byte = size(session.getsentmedialength());
        String receviedmedia_byte = size(session.getreceviedmedialength());
        String totalsentbyte = size(session.getsentmessagelength() + session.getsentmedialength());
        String totalreceviedtbyte = size(session.getreceviedmessagelength() + session.getreceviedmedialength());
        networkstate.add("Messages Sent" + "-" + String.valueOf(session.getsentmessagecount()));
        networkstate.add("Messages Recevied" + "-" + String.valueOf(session.getreceviedmessagecount()));
        networkstate.add("Media Bytes sent" + "-" + sentmedia_byte);
        networkstate.add("Media bytes Recevied" + "-" + receviedmedia_byte);
        networkstate.add("Message bytes sent" + "-" + sentmessage_byte);
        networkstate.add("Message bytes Recevied" + "-" + receviedmessage_byte);
        networkstate.add("Total Bytes Sent" + "-" + totalsentbyte);
        networkstate.add("Total Bytes Recevied" + "-" + totalreceviedtbyte);
        adapter = new Networkusageadapter(networkstate);
        listnetworkusage.setAdapter(adapter);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AvnNextLTProRegTextView Count_bytes;
        public AvnNextLTProDemiTextView Heading;
        public MyViewHolder(View view) {
            super(view);
            Heading = (AvnNextLTProDemiTextView) view.findViewById(R.id.Hedding_datausage);
            Count_bytes = (AvnNextLTProRegTextView) view.findViewById(R.id.count_bytes);
        }
    }

    private class Networkusageadapter extends RecyclerView.Adapter<Networkusage.MyViewHolder> {

        private ArrayList<String> madaptervalue;

        public Networkusageadapter(ArrayList<String> madaptervalue) {
            this.madaptervalue = madaptervalue;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.networkusage_adapter_layout, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String[] value = madaptervalue.get(position).split("-");
            holder.Heading.setText(value[0]);
            holder.Count_bytes.setText(value[1]);


        }

        @Override
        public int getItemCount() {
            return madaptervalue.size();
        }
    }

    public String size(long size) {
        String hrSize = "";
        double k = size / 1024.0;
        double m = size / 1048576.0;
        double g = size / 1073741824.0;
        double t = size / (1073741824.0 * 1024.0);
        DecimalFormat form = new DecimalFormat("0.00");
        if (t > 1) {
            t = round(t, 2);
            hrSize = (t + "").concat(" TB");
        } else if (g > 1) {
            g = round(g, 2);
            hrSize = (g + "").concat(" GB");
        } else if (m > 1) {
            m = round(m, 2);
            hrSize = (m + "").concat(" MB");
        } else if (k > 1) {
            k = round(k, 2);
            hrSize = (k + "").concat(" KB");
        } else {
            hrSize = (size + "").concat(" Bytes");
        }

        return hrSize;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(100, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


}
