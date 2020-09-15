
/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc;

import org.appspot.apprtc.AppRTCClient.SignalingParameters;
import org.appspot.apprtc.util.AsyncHttpURLConnection;
import org.appspot.apprtc.util.AsyncHttpURLConnection.AsyncHttpEvents;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

import static org.webrtc.ContextUtils.getApplicationContext;

/**
 * AsyncTask that converts an AppRTC room URL into the set of signaling
 * parameters to use with that room.
 */
public class RoomParametersFetcher {
    private static final String TAG = "RoomRTCClient";
    private static final int TURN_HTTP_TIMEOUT_MS = 5000;
    private final RoomParametersFetcherEvents events;
    private final String roomUrl;
    private final String roomMessage;
    private AsyncHttpURLConnection httpConnection;

    /**
     * Room parameters fetcher callbacks.
     */
    public interface RoomParametersFetcherEvents {
        /**
         * Callback fired once the room's signaling parameters
         * SignalingParameters are extracted.
         */
        void onSignalingParametersReady(final SignalingParameters params);

        /**
         * Callback for room parameters extraction error.
         */
        void onSignalingParametersError(final String description);
    }

    public RoomParametersFetcher(String roomUrl, String roomMessage, final RoomParametersFetcherEvents events) {
        this.roomUrl = roomUrl;
        this.roomMessage = roomMessage;
        this.events = events;
    }

    public void makeRequest() {
        Log.d(TAG, "Connecting to room: " + roomUrl);
        Log.d(TAG, "Connecting to roommessage: " + roomMessage);
        httpConnection = new AsyncHttpURLConnection("POST", roomUrl, roomMessage, new AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "Room connection error: " + errorMessage);
                events.onSignalingParametersError(errorMessage);

                Log.d(TAG, "Connecting to errorresponse: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                roomHttpResponseParse(response);
                Log.d(TAG, "Connecting to successresponse: " + response);
            }
        });
        httpConnection.send();
    }

    private void roomHttpResponseParse(String response) {
        Log.d(TAG, "Room response: " + response);
        try {
            LinkedList<IceCandidate> iceCandidates = null;
            SessionDescription offerSdp = null;
            JSONObject roomJson = new JSONObject(response);

            String result = roomJson.getString("result");
            if (!result.equals("SUCCESS")) {
                events.onSignalingParametersError("Room response error: " + result);
                return;
            }
            response = roomJson.getString("params");
            roomJson = new JSONObject(response);
            String roomId = roomJson.getString("room_id");
            String clientId = roomJson.getString("client_id");
            String wssUrl = roomJson.getString("wss_url");
            String wssPostUrl = roomJson.getString("wss_post_url");
            boolean initiator = (roomJson.getBoolean("is_initiator"));
            if (!initiator) {
                iceCandidates = new LinkedList<IceCandidate>();
                String messagesString = roomJson.getString("messages");
                JSONArray messages = new JSONArray(messagesString);
                for (int i = 0; i < messages.length(); ++i) {
                    String messageString = messages.getString(i);
                    JSONObject message = new JSONObject(messageString);
                    String messageType = message.getString("type");
                    Log.d(TAG, "GAE->C #" + i + " : " + messageString);
                    if (messageType.equals("offer")) {
                        offerSdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(messageType), message.getString("sdp"));
                    } else if (messageType.equals("candidate")) {
                        IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                        iceCandidates.add(candidate);

//            String timestamp = roomId;
//
//
//            SendMessageEvent sendEvent = new SendMessageEvent();
//
//            JSONObject msgObj = new JSONObject();
//
//            try {
//              msgObj.put("from", SessionManager.getInstance(getApplicationContext()).getCurrentUserID());
//              msgObj.put("to", opponentUserId);
//              msgObj.put("type", "answer");
//              msgObj.put("id", Long.parseLong(timestamp));
//              msgObj.put("roomid", timestamp);
//
//              JSONObject sdpJSON = new JSONObject();
//              sdpJSON.put("type","sdp");
//              sdpJSON.put("sdp", signalingParameters.offerSdp.description);
//              msgObj.put("sdp", sdpJSON);
//
//            } catch (JSONException e) {
//              e.printStackTrace();
//            }
//
//            sendEvent.setEventName(SocketManager.EVENT_CALL_WEBRTC_MESSAGE);
//            sendEvent.setMessageObject(msgObj);
//            EventBus.getDefault().post(sendEvent);

                    } else {
                        Log.e(TAG, "Unknown message: " + messageString);
                    }
                }
            }
            Log.d(TAG, "RoomId: " + roomId + ". ClientId: " + clientId);
            Log.d(TAG, "Initiator: " + initiator);
            Log.d(TAG, "WSS url: " + wssUrl);
            Log.d(TAG, "WSS POST url: " + wssPostUrl);


            LinkedList<PeerConnection.IceServer> iceServers =
                    iceServersFromPCConfigJSON(roomJson.getString("pc_config"));
            // Log.e("==string_URL","==string_URL"+roomJson.getString("pc_config"));
            boolean isTurnPresent = false;
            for (PeerConnection.IceServer server : iceServers) {
                Log.d(TAG, "IceServer: " + server);
                Log.e("==IceServer", "==IceServer" + iceServers);
                if (server.uri.startsWith("turn:")) {
                    isTurnPresent = true;
                    break;
                }
            }

            iceServers.clear();
            // Request TURN servers.
//      if (!isTurnPresent && !roomJson.optString("ice_server_url").isEmpty()) {
            LinkedList<PeerConnection.IceServer> turnServers =
                    requestTurnServers(roomJson.getString("ice_server_url"));
            for (PeerConnection.IceServer turnServer : turnServers) {
                Log.d(TAG, "TurnServer: " + turnServer);
                Log.e("==TurnServer", "==TurnServer" + turnServers);
                iceServers.add(turnServer);
            }
//      }

            SignalingParameters params = new SignalingParameters(iceServers, initiator, clientId, wssUrl, wssPostUrl, offerSdp, iceCandidates);
            events.onSignalingParametersReady(params);
        } catch (JSONException e) {
            events.onSignalingParametersError("Room JSON parsing error: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Requests & returns a TURN ICE Server based on a request URL.  Must be run
    // off the main thread!
    private LinkedList<PeerConnection.IceServer> requestTurnServers(String url)
            throws IOException, JSONException {
        LinkedList<PeerConnection.IceServer> turnServers = new LinkedList<PeerConnection.IceServer>();
        Log.d(TAG, "Request TURN from: " + url);
//    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
//    connection.setDoOutput(true);
//    connection.setRequestProperty("REFERER", "https://appr.tc");
//    connection.setConnectTimeout(TURN_HTTP_TIMEOUT_MS);
//    connection.setReadTimeout(TURN_HTTP_TIMEOUT_MS);
//    int responseCode = connection.getResponseCode();
//    if (responseCode != 200) {
//      throw new IOException("Non-200 response when requesting TURN server from " + url + " : "
//          + connection.getHeaderField(null));
//    }
//    InputStream responseStream = connection.getInputStream();
//    String response = drainStream(responseStream);
//    connection.disconnect();

//    Log.d(TAG, "TURN response: " + response);

        // JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:103.88.129.42:3478\"]},{\"urls\": [\"turn:103.88.129.42:3478?transport=tcp\", \"turn:103.88.129.42:3478?transport=udp\"],\"username\": \"test\", \"credential\": \"test123\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        // JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:stun.l.google.com:19302\"]},{\"urls\": [\"turn:103.88.129.42:3478?transport=tcp\", \"turn:103.88.129.42:3478?transport=udp\"],\"username\": \"test\", \"credential\": \"test123\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        //   JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"turn:bturn1.xirsys.com:3478?transport=tcp\", \"turn:bturn1.xirsys.com:3478?transport=udp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        //  JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\",\"stun:13.233.83.168:3478\"]},{\"urls\": [\"turn:bturn1.xirsys.com:3478?transport=tcp\", \"turn:bturn1.xirsys.com:3478?transport=udp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"},{\"urls\": [\"turn:13.233.83.168:3478?transport=tcp\", \"turn:13.233.83.168:3478?transport=udp\"],\"username\": \"chatuser\", \"credential\": \"802c89f51bf63330beb314bad225342d\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        // JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:13.233.83.168:3478\"]},{\"urls\": [\"turn:13.233.83.168:3478?transport=udp\"],\"username\": \"chatuser\", \"credential\": \"802c89f51bf63330beb314bad225342d\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        //   JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:104.211.100.224:3478\"]},{\"urls\": [\"turn:104.211.100.224:3478?transport=tcp\",\"turn:104.211.100.224:3478?transport=udp\"],\"username\": \"chatappuser\", \"credential\": \"17c2cbad8052a41ef522a9be3c6f05e4\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");



        //    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\"]},{\"urls\": [\"turn:bturn1.xirsys.com:80?transport=tcp\", \"turn:bturn1.xirsys.com:80?transport=udp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");


        //  JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\"]},{\"urls\": [\"turn:bturn1.xirsys.com:80?transport=udp\", \"turn:bturn1.xirsys.com:80?transport=tcp\",\"turn:bturn1.xirsys.com:3478?transport=udp\",\"turn:bturn1.xirsys.com:3478?transport=tcp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        // Adani live build
        //  JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:14.142.48.27:3478\"]},{\"urls\": [\"turn:14.142.48.27:3478?transport=udp\", \"turn:14.142.48.27:3478?transport=tcp\"],\"username\": \"chatappuser\", \"credential\": \"c9afdad4b4b7032dfe05dc6776ad5e78\"},{\"urls\": [\"turn:10.6.127.251:3478?transport=udp\", \"turn:10.6.127.251:3478?transport=tcp\"],\"username\": \"adani\", \"credential\": \"Adani@123\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        // TBT solution build
        //  JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\"]},{\"urls\": [\"turn:bturn1.xirsys.com:80?transport=udp\",\"turn:bturn1.xirsys.com:80?transport=tcp\",\"turn:bturn1.xirsys.com:3478?transport=udp\",\"turn:bturn1.xirsys.com:3478?transport=tcp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");


        //27-6-2019

        //JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:eu-turn5.xirsys.com\"]},{\"urls\": [\"turn:eu-turn5.xirsys.com:80?transport=udp\",\"turn:eu-turn5.xirsys.com:80?transport=tcp\",\"turn:eu-turn5.xirsys.com:3478?transport=udp\",\"turn:eu-turn5.xirsys.com:3478?transport=tcp\"],\"username\": \"0hOtjM5fdR61Q6doUdGSCFaqExBOJktGQ_XhyPOMKXOiLMiRAHV-T8-S9SXR4Z8GAAAAAFy1r1xhZGhhc2g=\", \"credential\": \"020b243c-6033-11e9-b9c2-8e4d62b186e1\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:101.53.141.74:3478\"]},{\"urls\": [\"turn:101.53.141.74:3478?transport=udp\",\"turn:101.53.141.74:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:stun.vodafone.ro:3478\"]},{\"urls\": [\"turn:101.53.141.74:3478?transport=udp\",\"turn:101.53.141.74:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:stun.l.google.com:19302\",\"stun:stun1.l.google.com:19302\",\"stun:stun2.l.google.com:19302\",\"stun:stun3.l.google.com:19302\",\"stun:stun4.l.google.com:19302\"]},{\"urls\": [\"turn:101.53.141.74:3478?transport=udp\",\"turn:101.53.141.74:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

//    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:syncchat.in:3478\"]},{\"urls\": [\"turn:syncchat.in:3478?transport=udp\",\"turn:syncchat.in:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

//    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:104.211.241.104:3478\"]},{\"urls\": [\"turn:104.211.241.104:3478?transport=udp\",\"turn:104.211.241.104:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

//        JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:52.172.4.166:3478\"]},{\"urls\": [\"turn:52.172.4.166:3478?transport=udp\",\"turn:52.172.4.166:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:achatstun.adani.com:3478\"]},{\"urls\": [\"turn:achatturn.adani.com:3478?transport=udp\",\"turn:achatturn.adani.com:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

//    JSONObject responseJSON = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:52.172.9.125:3478\"]},{\"urls\": [\"turn:52.172.9.125:3478?transport=udp\",\"turn:52.172.9.125:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        JSONArray iceServers = responseJSON.getJSONArray("iceServers");
        for (int i = 0; i < iceServers.length(); ++i) {
            JSONObject server = iceServers.getJSONObject(i);
            JSONArray turnUrls = server.getJSONArray("urls");
            String username = server.has("username") ? server.getString("username") : "";
            String credential = server.has("credential") ? server.getString("credential") : "";
            for (int j = 0; j < turnUrls.length(); j++) {
                String turnUrl = turnUrls.getString(j);
                turnServers.add(new PeerConnection.IceServer(turnUrl, username, credential));
            }
        }
        return turnServers;
    }

    // Return the list of ICE servers described by a WebRTCPeerConnection
    // configuration string.
    private LinkedList<PeerConnection.IceServer> iceServersFromPCConfigJSON(String pcConfig) throws JSONException {
        //JSONObject json = new JSONObject(pcConfig);
        //  JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:stun.l.google.com:19302\"]},{\"urls\": [\"turn:103.88.129.42:3478?transport=tcp\", \"turn:103.88.129.42:3478?transport=udp\"],\"username\": \"test\", \"credential\": \"test123\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        //  JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\"]},{\"urls\": [\"turn:bturn1.xirsys.com:3478?transport=tcp\", \"turn:bturn1.xirsys.com:3478?transport=udp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        //  JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\",\"stun:13.233.83.168:3478\"]},{\"urls\": [\"turn:bturn1.xirsys.com:3478?transport=tcp\", \"turn:bturn1.xirsys.com:3478?transport=udp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"},{\"urls\": [\"turn:13.233.83.168:3478?transport=tcp\", \"turn:13.233.83.168:3478?transport=udp\"],\"username\": \"chatuser\", \"credential\": \"802c89f51bf63330beb314bad225342d\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");


//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:13.233.83.168:3478\"]},{\"urls\": [\"turn:13.233.83.168:3478?transport=udp\"],\"username\": \"chatuser\", \"credential\": \"802c89f51bf63330beb314bad225342d\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        //   JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:104.211.100.224:3478\"]},{\"urls\": [\"turn:104.211.100.224:3478?transport=tcp\",\"turn:104.211.100.224:3478?transport=udp\"],\"username\": \"chatappuser\", \"credential\": \"17c2cbad8052a41ef522a9be3c6f05e4\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        // JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\"]},{\"urls\": [\"turn:bturn1.xirsys.com:80?transport=tcp\", \"turn:bturn1.xirsys.com:80?transport=udp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        // TBT Solution build

        //  JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:bturn1.xirsys.com\"]},{\"urls\": [\"turn:bturn1.xirsys.com:80?transport=udp\",\"turn:bturn1.xirsys.com:80?transport=tcp\",\"turn:bturn1.xirsys.com:3478?transport=udp\",\"turn:bturn1.xirsys.com:3478?transport=tcp\"],\"username\": \"f243839c-c2e8-11e8-891a-05b9ac3da258\", \"credential\": \"f2438478-c2e8-11e8-a5f5-8147accdc336\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

//27-6-2019
//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:eu-turn5.xirsys.com\"]},{\"urls\": [\"turn:eu-turn5.xirsys.com:80?transport=udp\",\"turn:eu-turn5.xirsys.com:80?transport=tcp\",\"turn:eu-turn5.xirsys.com:3478?transport=udp\",\"turn:eu-turn5.xirsys.com:3478?transport=tcp\"],\"username\": \"0hOtjM5fdR61Q6doUdGSCFaqExBOJktGQ_XhyPOMKXOiLMiRAHV-T8-S9SXR4Z8GAAAAAFy1r1xhZGhhc2g=\", \"credential\": \"020b243c-6033-11e9-b9c2-8e4d62b186e1\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:101.53.141.74:3478\"]},{\"urls\": [\"turn:101.53.141.74:3478?transport=udp\",\"turn:101.53.141.74:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:stun.l.google.com:19302\",\"stun:stun1.l.google.com:19302\",\"stun:stun2.l.google.com:19302\",\"stun:stun3.l.google.com:19302\",\"stun:stun4.l.google.com:19302\"]},{\"urls\": [\"turn:101.53.141.74:3478?transport=udp\",\"turn:101.53.141.74:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:stun.vodafone.ro:3478\"]},{\"urls\": [\"turn:101.53.141.74:3478?transport=udp\",\"turn:101.53.141.74:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:syncchat.in:3478\"]},{\"urls\": [\"turn:syncchat.in:3478?transport=udp\",\"turn:syncchat.in:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:104.211.241.104:3478\"]},{\"urls\": [\"turn:104.211.241.104:3478?transport=udp\",\"turn:104.211.241.104:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");
//    JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:52.172.4.166:3478\"]},{\"urls\": [\"turn:52.172.4.166:3478?transport=udp\",\"turn:52.172.4.166:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

        JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:achatstun.adani.com:3478\"]},{\"urls\": [\"turn:achatturn.adani.com:3478?transport=udp\",\"turn:achatturn.adani.com:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");

// JSONObject json = new JSONObject("{\"lifetimeDuration\": \"86400s\",\"iceServers\": [{\"urls\": [\"stun:52.172.9.125:3478\"]},{\"urls\": [\"turn:52.172.9.125:3478?transport=udp\",\"turn:52.172.9.125:3478?transport=tcp\"],\"username\": \"adanichat\", \"credential\": \"AChat123turn\"}],\"blockStatus\": \"NOT_BLOCKED\",\"iceTransportPolicy\": \"all\"}");


        JSONArray servers = json.getJSONArray("iceServers");
        LinkedList<PeerConnection.IceServer> ret = new LinkedList<PeerConnection.IceServer>();
        for (int i = 0; i < servers.length(); ++i) {
            JSONObject server = servers.getJSONObject(i);
            String url = server.getString("urls");
            String credential = server.has("credential") ? server.getString("credential") : "";
            ret.add(new PeerConnection.IceServer(url, "", credential));
        }
        return ret;
    }

    // Return the contents of an InputStream as a String.
    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

