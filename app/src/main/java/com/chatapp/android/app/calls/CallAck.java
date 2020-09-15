package com.chatapp.android.app.calls;

import android.content.Context;

import com.chatapp.android.core.message.BaseMessage;
import com.chatapp.android.core.message.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 *
 */
public class CallAck extends BaseMessage implements Message {
    public CallAck(Context context) {
        super(context);
    }

    /***
     * get Message Object for call state
     * @param to to user
     * @param doc_id get docid
     * @param isSecretchat check secret chat or not
     * @return value
     */
    @Override
    public Object getMessageObject(String to, String doc_id, Boolean isSecretchat) {
        this.to = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("msgIds", new JSONArray(Arrays.asList(new String[]{id})));
            object.put("doc_id", doc_id);
            object.put("status", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;

    }


    @Override
    public Object getGroupMessageObject(String to, String payload, String groupName) {
        return null;
    }

    /**
     * get Message Object for call state
     *
     * @param to     to user
     * @param doc_id get docid
     * @param status put status
     * @param _id    message id
     * @return value
     */
    public Object getMessageObject(String to, String doc_id, String status, String _id) {
        this.to = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("msgIds", new JSONArray(Arrays.asList(new String[]{_id})));
            object.put("doc_id", doc_id);
            object.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;

    }

}
