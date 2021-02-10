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
        View fragment_layout = inflater.inflate(R.layout.fragment_country, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Article currentStory = (Article) args.getSerializable("STORY_DATA");
            if (currentStory == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");


            TextView country = fragment_layout.findViewById(R.id.country);
//
            country.setText(currentStory.getTitle());
//            TextView region = fragment_layout.findViewById(R.id.region);
//            region.setText(String.format(Locale.getDefault(),
//                    "%s (%s)", currentCountry.getRegion(), currentCountry.getSubRegion()));
//
//            TextView capital = fragment_layout.findViewById(R.id.capital);
//            capital.setText(currentCountry.getCapital());
//
//            TextView population = fragment_layout.findViewById(R.id.population);
//            population.setText(String.format(Locale.US, "%,d", currentCountry.getPopulation()));
//
//            TextView area = fragment_layout.findViewById(R.id.area);
//            area.setText(String.format(Locale.US, "%,d sq km", currentCountry.getArea()));

//            TextView citizen = fragment_layout.findViewById(R.id.citizens);
//            citizen.setText(currentCountry.getCitizen());
//
//            TextView codes = fragment_layout.findViewById(R.id.codes);
//            codes.setText(currentCountry.getCallingCodes());
//
//            TextView borders = fragment_layout.findViewById(R.id.borders);
//            borders.setText(currentCountry.getBorders());

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
