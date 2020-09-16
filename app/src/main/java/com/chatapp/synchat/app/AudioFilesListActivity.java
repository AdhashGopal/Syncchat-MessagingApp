package com.chatapp.synchat.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.AudioFilesListAdapter;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.model.AudioFilePojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 2/1/2017.
 */
public class AudioFilesListActivity extends CoreActivity implements AudioFilesListAdapter.AudioFileListItemClickListener {

    private RecyclerView rvAudioFiles;
    private SearchView searchView;

    private AudioFilesListAdapter adapter;
    private List<AudioFilePojo> audioFilesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_files_list);

        initView();
        initData();
    }

    /**
     * binding the widgets
     */
    private void initView() {
        rvAudioFiles = (RecyclerView) findViewById(R.id.rvAudioFiles);
        LinearLayoutManager manager = new LinearLayoutManager(AudioFilesListActivity.this);
        rvAudioFiles.setLayoutManager(manager);
    }

    /**
     * binding the data
     */
    private void initData() {
        audioFilesList = new ArrayList<>();
        adapter = new AudioFilesListAdapter(AudioFilesListActivity.this, audioFilesList);
        rvAudioFiles.setAdapter(adapter);
        adapter.setFileItemClickListener(AudioFilesListActivity.this);

        loadAudioFilesList();
    }


    /**
     * load Audio FilesList
     */
    private void loadAudioFilesList() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                AudioFilePojo pojo = new AudioFilePojo();
                pojo.setFilePath(cursor.getString(3));
                pojo.setFileName(cursor.getString(4));

                Long duration = Long.parseLong(cursor.getString(5));
                int minutes = (int) ((duration % (1000 * 60 * 60)) / (1000 * 60));
                int seconds = (int) (((duration % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

                String durationStr = String.format("%02d", minutes).concat(":").concat(String.format("%02d", seconds));

                pojo.setDuration(durationStr);
                audioFilesList.add(pojo);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * menu in layout
     * @param menu menu view
     * @return value
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // // TODO: 1/3/2017

        getMenuInflater().inflate(R.menu.audio_files_list, menu);
        final MenuItem searchItem = menu.findItem(R.id.menuSearch);
//        searchItem.setVisible(false);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery(query, false);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView.clearFocus();
                        //closeKeypad();
                    }
                    adapter.getFilter().filter(newText);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
        // return super.onCreateOptionsMenu(menu);
    }

    /**
     * Send file item
     * @param item get file name, path and duration
     */
    @Override
    public void onFileItemClick(AudioFilePojo item) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("FileName", item.getFileName());
        resultIntent.putExtra("FilePath", item.getFilePath());
        resultIntent.putExtra("Duration", item.getDuration());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
