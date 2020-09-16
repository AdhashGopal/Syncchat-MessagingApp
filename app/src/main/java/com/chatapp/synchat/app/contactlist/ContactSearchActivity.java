package com.chatapp.synchat.app.contactlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.MessageData;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.ContactSearchModel;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.SocketManager;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class ContactSearchActivity extends Fragment {
    private boolean txtVisible = false;
    private static final String TAG = ContactSearchActivity.class.getSimpleName();
    private final int DISMISS_TIMEOUT = 2000;
    RecyclerView lvContacts;
    SearchedContactListAdapter adapter;
    ArrayList<ContactSearchModel> contactListData = new ArrayList<ContactSearchModel>();
    String mCurrentUserId;
    ArrayList<String> userIdList = new ArrayList<>();
    private SessionManager sessionManager;
    private Session session;
    private SearchView searchView;
    private ArrayList<ContactSearchModel> chatappEntries = new ArrayList<ContactSearchModel>();
    private UserInfoSession userInfoSession;
    private Getcontactname getcontactname;
    private Toolbar toolbar;
    private String searchKey = "";
    private int page = 0;
    private TextView emptyData;
    private ProgressBar emptyDataLoader;
    private boolean isHasNextPage;
    private LinearLayoutManager mediaManager;
    private SwipyRefreshLayout swipyRefreshLayout;
    private AlertDialog loaderDialog;
    private RelativeLayout relativeLayout, internetLay;
    private TextView noDatafoundTxt;

    /**
     * Contact list response
     */
    private ServiceRequest.ServiceListener resultListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            try {
                showLoaderDialog(false);
                JSONObject jsonObject = new JSONObject(response);


                isHasNextPage = jsonObject.getBoolean("isNext");

                if (jsonObject.getInt("Status") == 1) {
                    if (isHasNextPage) {
                        page++;
                        swipyRefreshLayout.setRefreshing(true);
                    } else {
                        swipyRefreshLayout.setRefreshing(false);
                    }

                    ContactSearchModel contactSearchModel = null;
                    JSONArray jArray = (JSONArray) jsonObject.getJSONArray("users");
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            contactSearchModel = new ContactSearchModel();

                            contactSearchModel.setId(jArray.getJSONObject(i).getString("id"));
                            contactSearchModel.setMsisdn(jArray.getJSONObject(i).getString("msisdn"));
                            contactSearchModel.setName(jArray.getJSONObject(i).getString("Name"));
                            contactSearchModel.setRequestStatus(jArray.getJSONObject(i).getInt("RequestStatus"));
                            contactSearchModel.setBlockStatus(jArray.getJSONObject(i).getString("IsBlocked"));
                            contactSearchModel.setFavouriteStatus(jArray.getJSONObject(i).getInt("Fav_type"));
//                            if (jArray.getJSONObject(i).getJSONObject("Privacy").getString("profile_photo").equalsIgnoreCase("everyone"))
                            contactSearchModel.setAvatarImageUrl(jArray.getJSONObject(i).getString("AvatarImageUrl"));
//                            else
//                                contactSearchModel.setAvatarImageUrl("");
//                            if (jArray.getJSONObject(i).getJSONObject("Privacy").getString("status").equalsIgnoreCase("everyone")) {
                            contactSearchModel.setUserStatus(jArray.getJSONObject(i).getString("Status"));

/*
                            } else {
                                contactSearchModel.setUserStatus("");
                            }
*/
                            /*if(contactListData.size()==0){
                                contactListData.add(contactSearchModel);
                            }
                            else {
                                if(!contactListData.get(i).getId().equalsIgnoreCase(jArray.getJSONObject(i).getString("id"))){
                                    contactListData.add(contactSearchModel);
                                }
                            }*/
                            contactListData.add(contactSearchModel);
                            Log.e("Data", contactListData.size() + "");
                        }
                    }
                    lvContacts.post(new Runnable() {
                        @Override
                        public void run() {
                            lvContacts.scrollToPosition(adapter.getItemCount() - 1);
                            // Here adapter.getItemCount()== child count
                        }
                    });

                    List<ContactSearchModel> allEvents = contactListData;
                    ArrayList<ContactSearchModel> noRepeat = new ArrayList<ContactSearchModel>();

                    for (ContactSearchModel event : allEvents) {
                        boolean isFound = false;
                        for (ContactSearchModel e : noRepeat) {
                            if (e.getId().equals(event.getId()) || (e.equals(event))) {
                                isFound = true;
                                break;
                            }
                        }
                        if (!isFound) noRepeat.add(event);
                        Log.e("noRepeat", noRepeat.size() + "");
                        adapter = new SearchedContactListAdapter(getActivity(), noRepeat);
                    }


                    lvContacts.setAdapter(adapter);
                    lvContacts.setVisibility(View.VISIBLE);
                    internetLay.setVisibility(View.GONE);
                    emptyData.setVisibility(View.GONE);
                    emptyDataLoader.setVisibility(View.GONE);
                    swipyRefreshLayout.setRefreshing(false);
                    if (contactListData.size() > 2)
                        lvContacts.scrollToPosition(contactListData.size() - 1);
                } else if (jsonObject.getInt("Status") == -1) {
                    swipyRefreshLayout.setRefreshing(false);
                    lvContacts.setVisibility(View.GONE);
                    emptyData.setVisibility(View.VISIBLE);
                    emptyData.setText(jsonObject.getString("Message"));
                    emptyDataLoader.setVisibility(View.GONE);
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showLoaderDialog(false);
                swipyRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onErrorListener(int state) {
            showLoaderDialog(false);
        }
    };

    /**
     *  handing UI view
     */
    @Override
    public void onResume() {
        super.onResume();
        if (!AppUtils.isNetworkAvailable(getActivity())) {
            internetLay.setVisibility(View.VISIBLE);
            lvContacts.setVisibility(View.GONE);
            emptyData.setVisibility(View.GONE);
        } else {
            internetLay.setVisibility(View.GONE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_list_search, parent, false);
        emptyData = rootView.findViewById(R.id.contact_empty);
        emptyDataLoader = rootView.findViewById(R.id.contact_empty_loader);
        noDatafoundTxt = (TextView) rootView.findViewById(R.id.noDatafoundTxt);
        userInfoSession = new UserInfoSession(getActivity());
        setHasOptionsMenu(true);
        relativeLayout = rootView.findViewById(R.id.contact_list_lay);
        internetLay = rootView.findViewById(R.id.internet_lay);
        getcontactname = new Getcontactname(getActivity());
        sessionManager = SessionManager.getInstance(getActivity());
        swipyRefreshLayout = rootView.findViewById(R.id.swipyrefreshlayout);
        swipyRefreshLayout.setColorSchemeResources(R.color.tabFourColor);
       /* if (!sessionManager.isValidDevice()) {
            Toast.makeText(getActivity(), "Something wrong", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }*/

        mCurrentUserId = sessionManager.getCurrentUserID();
        session = new Session(getActivity());
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        lvContacts = (RecyclerView) rootView.findViewById(R.id.listContacts);
        mediaManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);


        lvContacts.setLayoutManager(mediaManager);
        lvContacts.setNestedScrollingEnabled(false);


        lvContacts.addOnItemTouchListener(new RItemAdapter(getActivity(), lvContacts, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ContactSearchModel contactSearchModel = adapter.getItem(position);

                String userId = contactSearchModel.getId();
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());
                ChatappContactModel contact = new ChatappContactModel();

                contact.set_id(userId);
                contact.setStatus(decodeBase64(contactSearchModel.getUserStatus()));
                contact.setAvatarImageUrl(contactSearchModel.getAvatarImageUrl());
                contact.setMsisdn(contactSearchModel.getMsisdn());
                contact.setRequestStatus("3");

                contact.setFirstName(contactSearchModel.getName());

                contactDB_sqlite.updateUserDetails(userId, contact);


                if (!contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {

                    try {

                        Intent intent = new Intent(getActivity(), ChatViewActivity.class);
//                intent.putExtra("receiverUid", contactSearchModel.getNumberInDevice());
                        intent.putExtra("receiverUid", contactSearchModel.getMsisdn());
                        String firstName = "";
                        if (contactSearchModel.getName() != null)
                            firstName = contactSearchModel.getName();
                        intent.putExtra("receiverName", firstName);
                        intent.putExtra("documentId", contactSearchModel.getId());
                        intent.putExtra("Username", firstName);
                        intent.putExtra("Image", contactSearchModel.getAvatarImageUrl());
                        intent.putExtra("type", 0);
                        intent.putExtra("Userstatus", contactSearchModel.getUserStatus());
                        String msisdn = contactSearchModel.getMsisdn();
                        intent.putExtra("msisdn", msisdn);
                        getActivity().startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    String message = "Unblock" + " " + contactSearchModel.getName() + " to start conversation?";
                    displayAlert(message, userId);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        sessionManager = SessionManager.getInstance(getActivity());
        setupProgressDialog();


        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));


                if (isHasNextPage) {
                    CallContactSearchAPI(searchKey);
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity(), "No more contacts", Toast.LENGTH_SHORT).show();
                }
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipyRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);*/
            }
        });
//        showLoaderDialog(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FavListCall();
            }
        }, 800);
        return rootView;
    }

    /**
     * Eventbus value
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageData data) {
        if (data.noMsg.equals("nodatafound")) {
            if (AppUtils.isNetworkAvailable(getActivity())) {
                noDatafoundTxt.setVisibility(View.VISIBLE);
                txtVisible = true;
            }
        } else {
            noDatafoundTxt.setVisibility(View.GONE);
            txtVisible = false;
        }

    }

    /**
     * Call API for contact list
     */
    private void FavListCall() {
        contactListData.clear();
        page = 0;
        CallContactSearchAPI("");
    }

    /**
     * Start eventbus
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop eventbus
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Based on UI response
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (txtVisible) {
                noDatafoundTxt.setVisibility(View.GONE);
            }
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            contactListData.clear();
            page = 0;
            CallContactSearchAPI("");
        }
    }

    /**
     *  unblock display Alert
     * @param txt value of text
     * @param userId value of userid
     */
    private void displayAlert(final String txt, final String userId) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(txt);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                BlockUserUtils.changeUserBlockedStatus(getActivity(), EventBus.getDefault(), mCurrentUserId, userId, false);
                dialog.dismiss();
                FavListCall();
            }

            @Override

            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getActivity().getSupportFragmentManager(), "Unblock a person");

    }

    /**
     * load Block User Message
     * @param event getting value from model class
     */
    private void loadBlockUserMessage(ReceviceMessageEvent event) {
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String from = object.getString("from");
            String to = object.getString("to");

            if (mCurrentUserId.equalsIgnoreCase(from)) {
                for (int i = 0; i < contactListData.size(); i++) {
                    String userId = contactListData.get(i).getId();
                    if (userId.equals(to)) {
                        String stat = object.getString("status");
                        if (stat.equalsIgnoreCase("1")) {
                            Toast.makeText(getActivity(), "Contact is blocked", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Contact is Unblocked", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Eventbus value
     * @param event based on value call socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            Object[] args = event.getObjectsArray();
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            loadBlockUserMessage(event);
        } else {

            if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {

                try {
                    Object[] obj = event.getObjectsArray();
                    JSONObject object = new JSONObject(obj[0].toString());
                    String groupAction = object.getString("groupType");

                    if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MESSAGE)) {
//                        handleGroupMessage(event);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * menu view
     * @param menu munu layout
     * @param inflater view binding
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.new_chat_fragment, menu);
        MenuItem chats_contactIcon = menu.findItem(R.id.chats_contactIcon);
        chats_contactIcon.setVisible(false);
        MenuItem menurefresh = menu.findItem(R.id.menurefresh);
        menurefresh.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.chatsc_searchIcon);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FavListCall();
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery("", false);
                searchView.clearFocus();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView.clearFocus();
                    }
                /*contactListData.clear();
                page = 0;
                lvContacts.setVisibility(View.GONE);
                emptyData.setVisibility(View.GONE);
                emptyDataLoader.setVisibility(View.VISIBLE);
                searchKey = newText;*/
                if (adapter!=null){
                    adapter.getFilter().filter(newText);
                }

                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }
        });

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);

        searchTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchTextView.setTextColor(Color.WHITE);

        /*final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (searchKey.length() >= 1) {
                    if (contactListData.size() == 0)
                        CallContactSearchAPI(searchKey);
                } else {
                    FavListCall();
                }

            }
        };

        final Handler handler = new Handler();

        TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {

                handler.postDelayed(runnable, 500);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacks(runnable);

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

        };

        searchTextView.addTextChangedListener(filterTextWatcher);*/




      /*  searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (searchKey.length() >= 1) {
                        if (contactListData.size() == 0)
                            CallContactSearchAPI(searchKey);
                        hideKeyboard(searchTextView);
                    } *//*else {
                        Toast.makeText(getActivity(), "Please type atleast 1 characters to search", Toast.LENGTH_SHORT).show();
                    }*//*


                    return true;
                }
                return false;
            }
        });*/

        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(searchTextView);


                    return true;
                }
                return false;
            }
        });


        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        return true;
    }

    /**
     * menu selected screen
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chats_settings:
                ActivityLauncher.launchSettingScreen(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    /**
     * Shown ProgressDialog
     */
    public void setupProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getActivity());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getActivity());
        tvText.setText("Please wait searching contacts...");
//        tvText.setTextColor(Color.parseColor("#000000"));
//        tvText.setTextSize(16);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(ll);

        loaderDialog = builder.create();

        Window window = loaderDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(loaderDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            loaderDialog.getWindow().setAttributes(layoutParams);
        }

    }

    private void showLoaderDialog(boolean show) {

        if (show)
            loaderDialog.show();
        else {
            if (loaderDialog.isShowing()) {
                loaderDialog.dismiss();
            }
        }
    }

    /**
     * hide keyboard
     * @param searchTextView
     */
    public void hideKeyboard(AutoCompleteTextView searchTextView) {

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);


    }

    private void showKeyboard(AutoCompleteTextView searchTextView) {
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.toggleSoftInputFromWindow(searchTextView.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

    }


    /**
     * Call contact api params
     * @param searchKey
     */
    private void CallContactSearchAPI(String searchKey) {
       /* if (!TextUtils.isEmpty(searchKey)) {
            showLoaderDialog(true);
        }*/

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", SessionManager.getInstance(getActivity()).getCurrentUserID());
        params.put("searchKey", searchKey);
        params.put("limit", "10");
        params.put("page", String.valueOf(page));

        ServiceRequest request = new ServiceRequest(getActivity());
        request.makeServiceRequest(Constants.SEARCH_CONTACTS, Request.Method.POST, params, resultListener);

    }

    /**
     * decodeBase64
     * @param encodedStatus value of decodeBase64 data
     * @return value
     */
    private String decodeBase64(String encodedStatus) {
        return new String(Base64.decode(encodedStatus, Base64.DEFAULT));
    }

    private String decodeString(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {
            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return decodedString;
        }
    }

}

