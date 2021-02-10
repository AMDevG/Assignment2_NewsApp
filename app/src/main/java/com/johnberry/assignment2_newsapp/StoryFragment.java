package com.johnberry.assignment2_newsapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class StoryFragment extends Fragment {

    public StoryFragment(){
    }

    public static StoryFragment newInstance(Article article, int index, int max){
        StoryFragment f = new StoryFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("STORY_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        System.out.println("New Fragement Created size: " + max);
        return f;
    }



    // SET ALL
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
//
            titleTextview.setText(currentStory.getTitle());
            publishedAtTextView.setText(currentStory.getPublishedAt());
            authorTextView.setText(currentStory.getAuthor());
            descriptionTextView.setText(currentStory.getDescription());

            TextView pageNum = fragment_layout.findViewById(R.id.page_num);
            pageNum.setText(String.format(Locale.US, "%d of %d", index, total));
//
//            ImageView imageView = fragment_layout.findViewById(R.id.imageView);
//            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//
//            imageView.setImageDrawable(currentCountry.getDrawable());
//            imageView.setOnClickListener(v -> clickFlag(currentCountry.getName()));
            return fragment_layout;
        } else {
            return null;
        }
    }





}
