package ua.in.danilichev.imagesearcher.app.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.in.danilichev.imagesearcher.app.R;
import ua.in.danilichev.imagesearcher.app.SettingsActivity;
import ua.in.danilichev.imagesearcher.app.adapters.DesiredImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ImageSearcherFragment extends Fragment {

    EditText editTextSearchQuery;
    Button buttonSearchImage;
    Button buttonTakePicture;

    File directory;
    Uri outputPhotoUri;

    private String mSearchQuery= "";
    private String mTargetQuery = "";
    private ArrayList<DesiredImage> mDesiredImages;

    SharedPreferences sharedPreferences;

    private static int REQUEST_CODE_PHOTO = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mDesiredImages = savedInstanceState.getParcelableArrayList("key");
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        createDirectory();
    }

    @Override
    public void onResume() {
        super.onResume();
        clarifyRequestParameters();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_searcher, container, false);

        setHasOptionsMenu(true);

        if (mDesiredImages == null) mDesiredImages = new ArrayList<DesiredImage>();

        editTextSearchQuery = (EditText) view.findViewById(R.id.editTextSearchQuery);

        buttonSearchImage = (Button) view.findViewById(R.id.buttonSearchImage);
        buttonSearchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = editTextSearchQuery.getText().toString();
                mTargetQuery = "&q=" + Uri.encode(searchQuery);

                new SearchImagesTask().execute();
            }
        });

        buttonTakePicture = (Button) view.findViewById(R.id.buttonTakePicture);
        buttonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, generatePhotoUri());
                startActivityForResult(intent, REQUEST_CODE_PHOTO);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.image_searcher, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE_PHOTO) return;
        if (data != null) return;

        DesiredImage photo = new DesiredImage(
                outputPhotoUri.getLastPathSegment(), outputPhotoUri.toString());
        mDesiredImages.add(0, photo);

        showResults();
    }

    private ArrayList<DesiredImage> getDesiredImages(JSONArray jsonArray) {
        ArrayList<DesiredImage> images = new ArrayList<DesiredImage>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String imageTitle =  jsonObject.getString("title");
                String imageUrl = jsonObject.getString("tbUrl");
                DesiredImage image = new DesiredImage(imageTitle, imageUrl);
                images.add(image);
            }

            return images;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("key", mDesiredImages);
    }

    public ArrayList<DesiredImage> getmDesiredImages() { return mDesiredImages; }

    private Uri generatePhotoUri() {
        File photo = new File(directory.getPath() + "/" + "photo_" +
                System.currentTimeMillis() + ".jpg");
        outputPhotoUri = Uri.fromFile(photo);
        return outputPhotoUri;
    }

    private void createDirectory() {
        File environment = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        directory = new File(environment, "ImageSearchers");

        if (!directory.exists()) directory.mkdirs();
    }

    private void showResults() {
        ImageListFragment listFragment = (ImageListFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentImageList);
        listFragment.showDesiredImages(mDesiredImages);
    }

    private void clarifyRequestParameters() {
        String source = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0";
        String narrowQuery = "";

        String narrowSearchKey = getResources()
                .getString(R.string.pref_narrow_search_key);

        if (sharedPreferences.getBoolean(narrowSearchKey, false)) {
            String countOfImageKey = getResources()
                    .getString(R.string.pref_count_of_image_key);
            String defaultCountOfImage = getResources()
                    .getString(R.string.pref_count_of_image_default);
            String countOfImage = sharedPreferences
                    .getString(countOfImageKey, defaultCountOfImage);

            String imageColorizationKey = getResources()
                    .getString(R.string.pref_image_colorization_key);
            String defaultImageColorization = getResources()
                    .getString(R.string.image_colorization_default);
            String imageColorization = sharedPreferences
                    .getString(imageColorizationKey, defaultImageColorization);

            narrowQuery = "&rsz=" + countOfImage + "&imgc=" + imageColorization;

            String domainKey = getResources()
                    .getString(R.string.pref_domain_key);
            String domain = sharedPreferences.getString(domainKey, "");

            if (!domain.equals("")) narrowQuery += "&as_sitesearch=" + domain;
        }

        mSearchQuery = source + narrowQuery;
        Log.d("->", "mSearchQuery = " + mSearchQuery);
    }

    public class SearchImagesTask extends AsyncTask<Void, Void, Void> {

        JSONObject json;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = ProgressDialog.show(getActivity(), "", "Please wait...");
        }

        @Override
        protected Void doInBackground(Void... params) {

            URL url;
            try {
                url = new URL(mSearchQuery + mTargetQuery);
                Log.d("->", "query = " + mSearchQuery + mTargetQuery);

                URLConnection connection = url.openConnection();
                connection.addRequestProperty("Referer", "http://danilichev.in.ua");

                String line;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                json = new JSONObject(builder.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (dialog.isShowing()) { dialog.dismiss(); }

            try {
                JSONObject responseObject = json.getJSONObject("responseData");
                JSONArray resultArray = responseObject.getJSONArray("results");
                mDesiredImages = getDesiredImages(resultArray);
                showResults();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
