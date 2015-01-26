package ua.in.danilichev.imagesearcher.app.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ua.in.danilichev.imagesearcher.app.R;
import ua.in.danilichev.imagesearcher.app.adapters.DesiredImage;
import ua.in.danilichev.imagesearcher.app.adapters.ListImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ImageListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ImageSearcherFragment searcherFragment = (ImageSearcherFragment) getActivity()
                .getSupportFragmentManager().findFragmentById(R.id.fragmentSearchImage);
        ArrayList<DesiredImage> images = searcherFragment.getmDesiredImages();

        if (images != null) showDesiredImages(images);

        return inflater.inflate(R.layout.fragment_image_list, null, false);
    }

    public void showDesiredImages(List<DesiredImage> images) {
        ListImageAdapter adapter = new ListImageAdapter(
                getActivity(), images);
        setListAdapter(adapter);
    }

}
