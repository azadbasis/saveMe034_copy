/*
 * Copyright 2015 chenupt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nanosoft.bd.saveme.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nanosoft.bd.saveme.R;

/**
 * Created by chenupt@gmail.com on 2015/1/31.
 * Description TODO
 */
public class GuideFragment extends Fragment {

    private LinearLayout lo;
    private ImageView imageView;

    TextView titleTv, featureDescriptionTv;
    private int fragmentId;
    private ImageView featureImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentId = getArguments().getInt("data");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //imageView = (ImageView) getView().findViewById(R.id.image);
        // lo = (LinearLayout) getView().findViewById(R.id.lo);
        //imageView.setBackgroundResource(bgRes);
        //lo.setBackgroundResource(bgRes);

        featureImage = (ImageView) getView().findViewById(R.id.featureImage);
        titleTv = (TextView) getView().findViewById(R.id.titleTv);
        featureDescriptionTv = (TextView) getView().findViewById(R.id.featureDescriptionTv);


        if (fragmentId == R.drawable.img_emergency_call){
            titleTv.setText("Emergency Call");
            featureImage.setImageResource(R.drawable.img_emergency_call);
            featureDescriptionTv.setText(R.string.emergency_call);
        } if (fragmentId == R.drawable.img_emergency_text){
            titleTv.setText("Emergency Text");
            featureImage.setImageResource(R.drawable.img_emergency_text);
            featureDescriptionTv.setText(R.string.emergency_text);
           // setImageViewRatio(featureImage,640,459);
        } if (fragmentId == R.drawable.img_map_direction){
            titleTv.setText("Map Direction");
            featureImage.setImageResource(R.drawable.img_map_direction);
            featureDescriptionTv.setText(R.string.map_direction);

        } if (fragmentId == R.drawable.img_location_notifier){
            titleTv.setText("Location Notifier");
            featureImage.setImageResource(R.drawable.img_location_notifier);
            featureDescriptionTv.setText(R.string.location_notifier);

        } if (fragmentId == R.drawable.img_sms_tracking){
            titleTv.setText("Sms Tracking");
            featureImage.setImageResource(R.drawable.img_sms_tracking);
            featureDescriptionTv.setText(R.string.sms_tracking);
        } if (fragmentId ==  R.drawable.img_fnf_tracking){
            titleTv.setText("FnF Tracking");
            featureImage.setImageResource(R.drawable.img_fnf_tracking);
            featureDescriptionTv.setText(R.string.fnf_tracking);
        }if (fragmentId ==  R.drawable.img_phone_book){
            titleTv.setText("Phone Book");
            featureImage.setImageResource(R.drawable.img_phone_book);
            featureDescriptionTv.setText(R.string.phone_book);
        }if (fragmentId ==  R.drawable.img_position_tracker){
            titleTv.setText("Position Tracker");
            featureImage.setImageResource(R.drawable.img_position_tracker);
            featureDescriptionTv.setText(R.string.position_tracker);
        }if (fragmentId ==  R.drawable.img_news_feed){
            titleTv.setText("News Feed");
            featureImage.setImageResource(R.drawable.img_news_feed);
            featureDescriptionTv.setText(R.string.news_feed);
        }

       // setImageViewRatio(featureImage,640,459);


    }


    private void setTitle(final String title){

        getActivity().setTitle(title);
        new Handler().postDelayed(new Runnable() {
            public void run() {

            }
        }, 100);

    }

    private void setImageViewRatio(final ImageView imageView, final float width, final float height){

        new Handler().postDelayed(new Runnable() {
            public void run() {
               // int maxHeight = imageView.getHeight();
                int maxWidth = imageView.getWidth();

                float ratio = height/width;

                float heightFloat =  ratio * maxWidth;

                //long heightLong = (long) heightDouble ;

                int heightFinal = (int)heightFloat;

                imageView.getLayoutParams().height = heightFinal;

               // Toast.makeText(getActivity(), maxWidth +", "+ maxHeight +", "+g, Toast.LENGTH_SHORT).show();
            }
        }, 0);

    }

    @Override
    public void onDestroy() {
        Operations.IntSaveToSharedPreference(getActivity(),"bitForTitle",0);
        super.onDestroy();
    }
}
