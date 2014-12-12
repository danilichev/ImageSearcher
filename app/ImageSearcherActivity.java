package ua.in.danilichev.imagesearcher.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import ua.in.danilichev.imagesearcher.app.fragments.ImageListFragment;
import ua.in.danilichev.imagesearcher.app.fragments.ImageSearcherFragment;


public class ImageSearcherActivity extends ActionBarActivity {

    ImageSearcherFragment searcherFragment;
    ImageListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_searcher);

        searcherFragment = (ImageSearcherFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentSearchImage);
        listFragment = (ImageListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentImageList);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCacheExtraOptions(100, 100)
                .build();
        ImageLoader.getInstance().init(config);
    }

}
