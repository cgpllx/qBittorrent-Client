/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.qbittorrentclient;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

public class TorrentDetailsFragment extends Fragment {

    // Torrent variables
    String name, info, hash, ratio, size, progress, state, leechs, seeds, priority, savePath, creationDate, comment, totalWasted, totalUploaded,
            totalDownloaded, timeElapsed, nbConnections, shareRatio, uploadRateLimit, downloadRateLimit, downloaded, eta, downloadSpeed, uploadSpeed,
            percentage = "";

    String hostname;
    String protocol;
    int port;
    String username;
    String password;
    String url;

    int position;

    JSONObject json2;

    // Arrays for holding retrieved data
    static ContentFile[] files;
    static Tracker[] trackers;
    static String[] names, trackerNames;

    // TAGS
    protected static final String TAG_SAVE_PATH = "save_path";
    protected static final String TAG_CREATION_DATE = "creation_date";
    protected static final String TAG_COMMENT = "comment";
    protected static final String TAG_TOTAL_WASTED = "total_wasted";
    protected static final String TAG_TOTAL_UPLOADED = "total_uploaded";
    protected static final String TAG_TOTAL_DOWNLOADED = "total_downloaded";
    protected static final String TAG_TIME_ELAPSED = "time_elapsed";
    protected static final String TAG_NB_CONNECTIONS = "nb_connections";
    protected static final String TAG_SHARE_RATIO = "share_ratio";
    protected static final String TAG_UPLOAD_LIMIT = "up_limit";
    protected static final String TAG_DOWNLOAD_LIMIT = "dl_limit";
    protected static final String TAG_URL = "url";


    // Adapters
    myFileAdapter fileAdpater;
    myTrackerAdapter trackerAdapter;
    myPropertyAdapter propertyAdapter;

    private AdView adView;
    private View rootView;

    public TorrentDetailsFragment() {
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Restore last position from savedInstanceState
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            position = savedInstanceState.getInt("itemPosition", 0);
        }

        // Tell the host activity that your fragment has menu options that it
        // wants to add/replace/delete using the onCreateOptionsMenu method.
        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.torrent_details, container, false);

        savePath = "";
        creationDate = "";
        comment = "";
        uploadRateLimit = "";
        downloadRateLimit = "";
        totalWasted = "";
        totalUploaded = "";
        totalDownloaded = "";
        timeElapsed = "";
        nbConnections = "";
        shareRatio = "";

        try {

            if (savedInstanceState != null) {

                // Get saved values

                name = savedInstanceState.getString("torrentDetailName", "");
                size = savedInstanceState.getString("torrentDetailSize", "");
                hash = savedInstanceState.getString("torrentDetailHash", "");
                ratio = savedInstanceState.getString("torrentDetailRatio", "");
                state = savedInstanceState.getString("torrentDetailState", "");
                leechs = savedInstanceState.getString("torrentDetailLeechs", "");
                seeds = savedInstanceState.getString("torrentDetailSeeds", "");
                progress = savedInstanceState.getString("torrentDetailProgress", "");
                priority = savedInstanceState.getString("torrentDetailPriority", "");
                eta = savedInstanceState.getString("torrentDetailEta", "");
                uploadSpeed = savedInstanceState.getString("torrentDetailUploadSpeed", "");
                downloadSpeed = savedInstanceState.getString("torrentDetailDownloadSpeed", "");
                downloaded = savedInstanceState.getString("torrentDetailDownloaded", "");

            } else {

                name = MainActivity.lines[position].getFile();
                size = MainActivity.lines[position].getSize();
                hash = MainActivity.lines[position].getHash();
                ratio = MainActivity.lines[position].getRatio();
                progress = MainActivity.lines[position].getProgress();
                state = MainActivity.lines[position].getState();
                leechs = MainActivity.lines[position].getLeechs();
                seeds = MainActivity.lines[position].getSeeds();
                hash = MainActivity.lines[position].getHash();
                priority = MainActivity.lines[position].getPriority();
                eta = MainActivity.lines[position].getEta();
                uploadSpeed = MainActivity.lines[position].getUploadSpeed();
                downloadSpeed = MainActivity.lines[position].getDownloadSpeed();
                downloaded = MainActivity.lines[position].getDownloaded();
            }

            TextView nameTextView = (TextView) rootView.findViewById(R.id.torrentName);
            TextView sizeTextView = (TextView) rootView.findViewById(R.id.torrentSize);
            TextView ratioTextView = (TextView) rootView.findViewById(R.id.torrentRatio);
            TextView progressTextView = (TextView) rootView.findViewById(R.id.torrentProgress);
            TextView stateTextView = (TextView) rootView.findViewById(R.id.torrentState);
            TextView leechsTextView = (TextView) rootView.findViewById(R.id.torrentLeechs);
            TextView seedsTextView = (TextView) rootView.findViewById(R.id.torrentSeeds);
            TextView hashTextView = (TextView) rootView.findViewById(R.id.torrentHash);
            TextView etaTextView = (TextView) rootView.findViewById(R.id.torrentEta);
            TextView priorityTextView = (TextView) rootView.findViewById(R.id.torrentPriority);
            TextView downloadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentDownloadSpeed);
            TextView uploadSpeedTextView = (TextView) rootView.findViewById(R.id.torrentUploadSpeed);

//			TextView pathTextView = (TextView) rootView.findViewById(R.id.torrentSavePath);
//			TextView creationDateTextView = (TextView) rootView.findViewById(R.id.torrentCreationDate);
//			TextView commentTextView = (TextView) rootView.findViewById(R.id.torrentComment);
//			TextView totalWastedTextView = (TextView) rootView.findViewById(R.id.torrentTotalWasted);
//			TextView totalUploadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalUploaded);
//			TextView totalDownloadedTextView = (TextView) rootView.findViewById(R.id.torrentTotalDownloaded);
//			TextView timeElapsedTextView = (TextView) rootView.findViewById(R.id.torrentTimeElapsed);
//			TextView nbConnectionsTextView = (TextView) rootView.findViewById(R.id.torrentNbConnections);
//			TextView shareRatioTextView = (TextView) rootView.findViewById(R.id.torrentShareRatio);
//			TextView uploadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentUploadRateLimit);
//			TextView downloadRateLimitTextView = (TextView) rootView.findViewById(R.id.torrentDownloadRateLimit);

            nameTextView.setText(name);
            sizeTextView.setText(size);
            ratioTextView.setText(ratio);
            stateTextView.setText(state);
            leechsTextView.setText(leechs);
            seedsTextView.setText(seeds);
            progressTextView.setText(progress);
            hashTextView.setText(hash);
            etaTextView.setText(eta);
            priorityTextView.setText(priority);
            downloadSpeedTextView.setText(downloadSpeed);
            uploadSpeedTextView.setText(uploadSpeed);

//			pathTextView.setText(savePath);
//			creationDateTextView.setText(creationDate);
//			commentTextView.setText(comment);
//			totalWastedTextView.setText(totalWasted);
//			totalUploadedTextView.setText(totalUploaded);
//			totalDownloadedTextView.setText(totalDownloaded);
//			timeElapsedTextView.setText(timeElapsed);
//			nbConnectionsTextView.setText(nbConnections);
//			shareRatioTextView.setText(shareRatio);
//			uploadRateLimitTextView.setText(uploadRateLimit);
//			downloadRateLimitTextView.setText(downloadRateLimit);

            // Set status icon

            nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checking, 0, 0, 0);

            if ("pausedUP".equals(state) || "pausedDL".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.paused, 0, 0, 0);
            }

            if ("stalledUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalledup, 0, 0, 0);
            }

            if ("stalledDL".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stalleddl, 0, 0, 0);
            }

            if ("downloading".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.downloading, 0, 0, 0);
            }

            if ("uploading".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.uploading, 0, 0, 0);
            }

            if ("queuedDL".equals(state) || "queuedUP".equals(state)) {
                nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.queued, 0, 0, 0);
            }

            // Show progressBar
            if (MainActivity.progressBar != null) {
                MainActivity.progressBar.setVisibility(View.VISIBLE);
            }

            // Get Content files in background
            qBittorrentContentFile qcf = new qBittorrentContentFile();
            qcf.execute(new View[]{rootView});

            // Get trackers in background
            qBittorrentTrackers qt = new qBittorrentTrackers();
            qt.execute(new View[]{rootView});

            // get general info in background
            qBittorrentGeneralInfoTask qgit = new qBittorrentGeneralInfoTask();
            qgit.execute(new View[]{rootView});

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("TorrentDetailsFragment - onCreateView", e.toString());
        }

        // Load banner
        loadBanner();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("torrentDetailName", name);
        outState.putString("torrentDetailSize", size);
        outState.putString("torrentDetailHash", hash);
        outState.putString("torrentDetailRatio", ratio);
        outState.putString("torrentDetailState", state);
        outState.putString("torrentDetailLeechs", leechs);
        outState.putString("torrentDetailSeeds", seeds);
        outState.putString("torrentDetailProgress", progress);
        outState.putString("torrentDetailPriority", priority);
        outState.putString("torrentDetailEta", eta);
        outState.putString("torrentDetailUploadSpeed", uploadSpeed);
        outState.putString("torrentDetailDownloadSpeed", downloadSpeed);
        outState.putString("torrentDetailDownloaded", downloaded);

    }

    // @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {

    }

    // @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu != null) {

            if (getActivity().findViewById(R.id.one_frame) != null) {
                menu.findItem(R.id.action_refresh).setVisible(false);
            }

            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_resume_all).setVisible(false);
            menu.findItem(R.id.action_pause_all).setVisible(false);
            menu.findItem(R.id.action_add).setVisible(false);

            menu.findItem(R.id.action_resume).setVisible(true);
            menu.findItem(R.id.action_pause).setVisible(true);
            menu.findItem(R.id.action_increase_prio).setVisible(true);
            menu.findItem(R.id.action_decrease_prio).setVisible(true);
            menu.findItem(R.id.action_delete).setVisible(true);
            menu.findItem(R.id.action_delete_drive).setVisible(true);
            menu.findItem(R.id.action_download_rate_limit).setVisible(true);
            menu.findItem(R.id.action_upload_rate_limit).setVisible(true);

        }
    }

    // Load Banner
    public void loadBanner() {

        // Get the adView.
        adView = (AdView) getActivity().findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

    }


    // // Here is where the action happens
    private class qBittorrentContentFile extends AsyncTask<View, View, View[]> {

        String name, size;
        Double progress;
        int priority;

        protected View[] doInBackground(View... rootViews) {
            // Get torrent's extra info
            url = "json/propertiesFiles/";

            files = null;
            names = null;

            try {

                JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
                        MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

                JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

                if (jArray != null) {

                    files = new ContentFile[jArray.length()];
                    TorrentDetailsFragment.names = new String[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        name = json.getString(MainActivity.TAG_NAME);
                        size = json.getString(MainActivity.TAG_SIZE).replace(",", ".");
                        progress = json.getDouble(MainActivity.TAG_PROGRESS);
                        priority = json.getInt(MainActivity.TAG_PRIORITY);

                        files[i] = new ContentFile(name, size, progress, priority);
                        names[i] = name;

                    }

                }

            } catch (Exception e) {

                Log.e("TorrentFragment:", e.toString());

            }

            return rootViews;

        }

        @Override
        protected void onPostExecute(View[] rootViews) {

            try {

                View rootView = rootViews[0];

                fileAdpater = new myFileAdapter(getActivity(), names, files);

                ListView lv = (ListView) rootView.findViewById(R.id.theList);

                lv.setAdapter(fileAdpater);

                setListViewHeightBasedOnChildren(lv);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("Content2", e.toString());

            }

        }

    }

    // // Here is where the action happens
    private class qBittorrentTrackers extends AsyncTask<View, View, View[]> {

        String url;

        protected View[] doInBackground(View... rootViews) {
            // Get torrent's extra info
            url = "json/propertiesTrackers/";

            trackers = null;
            trackerNames = null;

            try {

                JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
                        MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

                JSONArray jArray = jParser.getJSONArrayFromUrl(url + hash);

                if (jArray != null) {

                    trackers = new Tracker[jArray.length()];
                    TorrentDetailsFragment.trackerNames = new String[jArray.length()];

                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject json = jArray.getJSONObject(i);

                        url = json.getString(TAG_URL);

                        trackers[i] = new Tracker(url);
                        trackerNames[i] = url;

                    }

                }

            } catch (Exception e) {

                Log.e("TorrentFragment:", e.toString());

            }

            return rootViews;

        }

        @Override
        protected void onPostExecute(View[] rootViews) {

            try {

                View rootView = rootViews[0];

                trackerAdapter = new myTrackerAdapter(getActivity(), trackerNames, trackers);

                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.trackers);

                for (int i = 0; i < trackerAdapter.getCount(); i++) {
                    View item = trackerAdapter.getView(i, null, null);
                    layout.addView(item);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("Trackers", e.toString());

            }

        }

    }

    // Here is where the action happens
    private class qBittorrentGeneralInfoTask extends AsyncTask<View, View, View[]> {

        String[] labels;
        String[] values;

        protected View[] doInBackground(View... rootViews) {
            // Get torrent's extra info
            url = "json/propertiesGeneral/";

            try {

                JSONParser jParser = new JSONParser(MainActivity.hostname, MainActivity.subfolder, MainActivity.protocol, MainActivity.port,
                        MainActivity.username, MainActivity.password, MainActivity.connection_timeout, MainActivity.data_timeout);

                json2 = jParser.getJSONFromUrl(url + hash);

                if (json2 != null && json2.length() > 0) {

                    labels = new String[11];
                    values = new String[11];

                    // Save path
                    labels[0] = getString(R.string.torrent_details_save_path);
                    values[0] = json2.getString(TAG_SAVE_PATH);

                    // Creation date
                    labels[1] = getString(R.string.torrent_details_created_date);
                    values[1] = json2.getString(TAG_CREATION_DATE);

                    // Comment
                    labels[2] = getString(R.string.torrent_details_comment);
                    values[2] = json2.getString(TAG_COMMENT);

                    // Total wasted
                    labels[3] = getString(R.string.torrent_details_total_wasted);
                    values[3] = json2.getString(TAG_TOTAL_WASTED);

                    // Total uploaded
                    labels[4] = getString(R.string.torrent_details_total_uploaded);
                    values[4] = json2.getString(TAG_TOTAL_UPLOADED);

                    // Total downloaded
                    labels[5] = getString(R.string.torrent_details_total_downloaded);
                    values[5] = json2.getString(TAG_TOTAL_DOWNLOADED);

                    // Time elapsed
                    labels[6] = getString(R.string.torrent_details_time_elapsed);
                    values[6] = json2.getString(TAG_TIME_ELAPSED);

                    // Number of connections
                    labels[7] = getString(R.string.torrent_details_num_connections);
                    values[7] = json2.getString(TAG_NB_CONNECTIONS);

                    // Share ratio
                    labels[8] = getString(R.string.torrent_details_share_ratio);
                    values[8] = json2.getString(TAG_SHARE_RATIO);

                    // Upload limit
                    labels[9] = getString(R.string.torrent_details_upload_rate_limit);
                    values[9] = json2.getString(TAG_UPLOAD_LIMIT);

                    // Download limit
                    labels[10] = getString(R.string.torrent_details_download_rate_limit);
                    values[10] = json2.getString(TAG_DOWNLOAD_LIMIT);

                }

            } catch (Exception e) {

                Log.e("TorrentFragment:", e.toString());

            }

            return rootViews;

        }

        @Override
        protected void onPostExecute(View[] rootViews) {

            try {

                View rootView = rootViews[0];

                propertyAdapter = new myPropertyAdapter(getActivity(), labels, values);

                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.lines);

                for (int i = 0; i < propertyAdapter.getCount(); i++) {
                    View item = propertyAdapter.getView(i, null, null);
                    layout.addView(item);
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e("TorrentFragment:", e.toString());
            }

            // Hide progressBar
            if (MainActivity.progressBar != null) {
                MainActivity.progressBar.setVisibility(View.INVISIBLE);
            }

        }

    }

    // My custom adapters
    class myPropertyAdapter extends ArrayAdapter<String> {

        private String[] labels;
        private String[] values;
        private Context context;

        public myPropertyAdapter(Context context, String[] labels, String[] values) {
            // TODO Auto-generated constructor stub
            super(context, R.layout.property_row, R.id.label, values);

            this.context = context;
            this.labels = labels;
            this.values = values;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub}
            return (labels != null) ? labels.length : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = super.getView(position, convertView, parent);

            TextView label = (TextView) row.findViewById(R.id.label);
            TextView value = (TextView) row.findViewById(R.id.value);

            label.setText("" + labels[position]);
            value.setText("" + values[position]);

            return (row);
        }
    }

    class myFileAdapter extends ArrayAdapter<String> {

        private String[] filesNames;
        private ContentFile[] files;
        private Context context;

        public myFileAdapter(Context context, String[] filesNames, ContentFile[] files) {
            // TODO Auto-generated constructor stub
            super(context, R.layout.contentfile_row, R.id.file, filesNames);

            this.context = context;
            this.filesNames = filesNames;
            this.files = files;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub}
            return (filesNames != null) ? filesNames.length : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = super.getView(position, convertView, parent);

            TextView info = (TextView) row.findViewById(R.id.info);

            info.setText("" + files[position].getSize());

            // Set progress bar
            ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
            TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

            int index = files[position].getProgressAsString().indexOf(".");

            if (index == -1) {
                index = files[position].getProgressAsString().indexOf(",");

                if (index == -1) {
                    index = files[position].getProgressAsString().length();
                }
            }

            String percentage = files[position].getProgressAsString().substring(0, index);

            progressBar.setProgress(Integer.parseInt(percentage));

            percentageTV.setText(percentage + "%");

            return (row);
        }
    }

    class myTrackerAdapter extends ArrayAdapter<String> {

        private String[] trackersNames;
        private Tracker[] trackers;
        private Context context;

        public myTrackerAdapter(Context context, String[] trackersNames, Tracker[] trackers) {
            // TODO Auto-generated constructor stub
            super(context, R.layout.tracker_row, R.id.tracker, trackersNames);

            this.context = context;
            this.trackersNames = trackersNames;
            this.trackers = trackers;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub}
            return (trackersNames != null) ? trackersNames.length : 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = super.getView(position, convertView, parent);

            TextView tracker = (TextView) row.findViewById(R.id.tracker);

            tracker.setText("" + trackers[position].getUrl());

            return (row);
        }
    }

    /**
     * *
     * Method for Setting the Height of the ListView dynamically. Hack to fix
     * the issue of not showing all the items of the ListView when placed inside
     * a ScrollView
     * **
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }


}
