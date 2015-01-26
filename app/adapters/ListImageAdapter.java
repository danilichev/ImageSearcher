package ua.in.danilichev.imagesearcher.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import ua.in.danilichev.imagesearcher.app.R;

import java.util.List;

public class ListImageAdapter extends BaseAdapter {

    Context context;
    List<DesiredImage> desiredImageList;

    public ListImageAdapter(Context context, List<DesiredImage> desiredImageList) {
        this.context = context;
        this.desiredImageList = desiredImageList;
    }

    @Override
    public int getCount() {
        return desiredImageList.size();
    }

    @Override
    public Object getItem(int position) {
        return desiredImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        DesiredImage image = (DesiredImage) getItem(position);
        return desiredImageList.indexOf(image);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_image_list, null);
        }

        DesiredImage image = desiredImageList.get(position);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewPicture);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(image.getImageUrl(), imageView, options);

        TextView textView = (TextView) convertView.findViewById(R.id.textViewPictureTitle);
        textView.setText(image.getImageTitle());

        return convertView;
    }
}
