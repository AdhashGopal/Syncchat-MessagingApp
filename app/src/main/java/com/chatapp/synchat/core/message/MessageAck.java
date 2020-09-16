package com.chatapp.synchat.core.message;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 *
 */
public class MessageAck extends BaseMessage implements Message {
    public MessageAck(Context context) {
        super(context);
    }

    private static final String TAG = MessageAck.class.getSimpleName();

    /**
     * set Message and secretchat
     *
     * @param to           input value(to)
     * @param doc_id       input value(doc_id)
     * @param isSecretchat input value(isSecretchat)
     * @return response value
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
     * set Message and secretchat
     *
     * @param to           input value(to)
     * @param doc_id       input value(doc_id)
     * @param status       input value(status)
     * @param _id          input value(_id)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    public Object getMessageObject(String to, String doc_id, String status, String _id, boolean isSecretChat) {
        this.to = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("msgIds", new JSONArray(Arrays.asList(new String[]{_id})));
            object.put("doc_id", doc_id);
            object.put("status", status);
            if (isSecretChat) {
                object.put("secret_type", "yes");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;

    }


    public Object getStatusMessageObject(String to, String docId, String status, String _id) {
        this.to = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("msgIds", new JSONArray(Arrays.asList(new String[]{_id})));
            object.put("doc_id", docId);
            object.put("status", status);
            object.put("type", "status");
        } catch (Exception e) {
            Log.e(TAG, "getStatusMessageObject: ", e);
        }
        return object;
    }

}
