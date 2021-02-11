package com.johnberry.assignment2_newsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class StoryFragment extends Fragment {

    public StoryFragment(){}

    public static StoryFragment newInstance(Article article, int index, int max){
        StoryFragment f = new StoryFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("STORY_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
//        System.out.println("New Fragement Created size: " + max);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);


        Bundle args = getArguments();
        if (args != null) {
            final Article currentStory = (Article) args.getSerializable("STORY_DATA");
            if (currentStory == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");


            TextView titleTextview = fragment_layout.findViewById(R.id.titleTextView);

            TextView publishedAtTextView = fragment_layout.findViewById(R.id.publishedAtTextView);
            TextView authorTextView = fragment_layout.findViewById(R.id.authorTextView);
            ImageView articleImageView = fragment_layout.findViewById(R.id.articleImageView);
            TextView descriptionTextView = fragment_layout.findViewById(R.id.descriptionTextView);

            titleTextview.setText(currentStory.getTitle());
            publishedAtTextView.setText(currentStory.getPublishedAt());
            authorTextView.setText(currentStory.getAuthor());
            descriptionTextView.setText(currentStory.getDescription());

            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));

            try {
                articleImageView.setImageBitmap(currentStory.getBitmap());


            } catch (Exception e) {
                e.printStackTrace();
            }

            titleTextview.setOnClickListener(v -> clickFlag(currentStory.getUrl()));
            articleImageView.setOnClickListener(v -> clickFlag(currentStory.getUrl()));


            return fragment_layout;
        } else {
            return null;
        }
    }

    private Bitmap downloadImage(String imageURL) throws IOException {
        InputStream is = (InputStream) new URL(imageURL).getContent();
        Bitmap bm = BitmapFactory.decodeStream(is);
        is.close();
        return bm;
    }

    public void clickFlag(String name) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(name));
        startActivity(intent);
    }
}
