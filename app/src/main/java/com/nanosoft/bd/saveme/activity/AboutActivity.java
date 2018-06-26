/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nanosoft.bd.saveme.activity;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;
import android.util.Property;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.nanosoft.bd.saveme.R;


/**
 * This is the About activity in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 */

public class AboutActivity extends DashboardActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        setTitleFromActivityLabel(R.id.title_text);
        final TextView textView1 = (TextView) findViewById(R.id.text1);
        final TextView textView2 = (TextView) findViewById(R.id.text2);
        final TextView textView3 = (TextView) findViewById(R.id.text3);
        final TextView textView4 = (TextView) findViewById(R.id.text4);
        final TextView textView5 = (TextView) findViewById(R.id.text5);
        final TextView textView6 = (TextView) findViewById(R.id.text6);
        final TextView textView7 = (TextView) findViewById(R.id.text7);
        final TextView textView8 = (TextView) findViewById(R.id.text8);
        final TextView textView9 = (TextView) findViewById(R.id.text9);
        String text1 = textView1.getText().toString();
        String text2 = textView2.getText().toString();
        String text3 = textView3.getText().toString();
        String text4 = textView4.getText().toString();
        String text5 = textView5.getText().toString();
        String text6 = textView6.getText().toString();
        String text7 = textView7.getText().toString();
        String text8 = textView8.getText().toString();
        String text9 = textView9.getText().toString();

        AnimatedColorSpan span = new AnimatedColorSpan(this);

        final SpannableString spannableString1 = new SpannableString(text1);
        final SpannableString spannableString2 = new SpannableString(text2);
        final SpannableString spannableString3 = new SpannableString(text3);
        final SpannableString spannableString4 = new SpannableString(text4);
        final SpannableString spannableString5 = new SpannableString(text5);
        final SpannableString spannableString6 = new SpannableString(text6);
        final SpannableString spannableString7 = new SpannableString(text7);
        final SpannableString spannableString8 = new SpannableString(text8);
        final SpannableString spannableString9 = new SpannableString(text9);

        String substring1 = getString(R.string.animated_rainbow_span1).toLowerCase();
        String substring2 = getString(R.string.animated_rainbow_span2).toLowerCase();
        String substring3 = getString(R.string.animated_rainbow_span3).toLowerCase();
        String substring4 = getString(R.string.animated_rainbow_span4).toLowerCase();
        String substring5 = getString(R.string.animated_rainbow_span5).toLowerCase();
        String substring6 = getString(R.string.animated_rainbow_span6).toLowerCase();
        String substring7 = getString(R.string.animated_rainbow_span7).toLowerCase();
        String substring8 = getString(R.string.animated_rainbow_span8).toLowerCase();
        String substring9 = getString(R.string.animated_rainbow_span9).toLowerCase();

        int start1 = text1.toLowerCase().indexOf(substring1);
        int start2 = text2.toLowerCase().indexOf(substring2);
        int start3 = text3.toLowerCase().indexOf(substring3);
        int start4 = text4.toLowerCase().indexOf(substring4);
        int start5 = text5.toLowerCase().indexOf(substring5);
        int start6 = text6.toLowerCase().indexOf(substring6);
        int start7 = text7.toLowerCase().indexOf(substring7);
        int start8 = text8.toLowerCase().indexOf(substring8);
        int start9 = text9.toLowerCase().indexOf(substring9);

        int end1 = start1 + substring1.length();
        int end2 = start2 + substring2.length();
        int end3 = start3 + substring3.length();
        int end4 = start4 + substring4.length();
        int end5 = start5 + substring5.length();
        int end6 = start6 + substring6.length();
        int end7 = start7 + substring7.length();
        int end8 = start8 + substring8.length();
        int end9 = start9 + substring9.length();

        spannableString1.setSpan(span, start1, end1, 0);
        spannableString2.setSpan(span, start2, end2, 0);
        spannableString3.setSpan(span, start3, end3, 0);
        spannableString4.setSpan(span, start4, end4, 0);
        spannableString5.setSpan(span, start5, end5, 0);
        spannableString6.setSpan(span, start6, end6, 0);
        spannableString7.setSpan(span, start7, end7, 0);
        spannableString8.setSpan(span, start8, end8, 0);
        spannableString9.setSpan(span, start9, end9, 0);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                span, ANIMATED_COLOR_SPAN_FLOAT_PROPERTY, 0, 100);
        objectAnimator.setEvaluator(new FloatEvaluator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                textView1.setText(spannableString1);
                textView2.setText(spannableString2);
                textView3.setText(spannableString3);
                textView4.setText(spannableString4);
                textView5.setText(spannableString5);
                textView6.setText(spannableString6);
                textView7.setText(spannableString7);
                textView8.setText(spannableString8);
                textView9.setText(spannableString9);
            }
        });
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(DateUtils.MINUTE_IN_MILLIS * 3);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.start();
    }

    private static final Property<AnimatedColorSpan, Float> ANIMATED_COLOR_SPAN_FLOAT_PROPERTY
            = new Property<AnimatedColorSpan, Float>(Float.class, "ANIMATED_COLOR_SPAN_FLOAT_PROPERTY") {
        @Override
        public void set(AnimatedColorSpan span, Float value) {
            span.setTranslateXPercentage(value);
        }

        @Override
        public Float get(AnimatedColorSpan span) {
            return span.getTranslateXPercentage();
        }
    };

    private static class AnimatedColorSpan extends CharacterStyle implements UpdateAppearance {
        private final int[] colors;
        private Shader shader = null;
        private Matrix matrix = new Matrix();
        private float translateXPercentage = 0;

        public AnimatedColorSpan(Context context) {
            colors = context.getResources().getIntArray(R.array.rainbow);
        }

        public void setTranslateXPercentage(float percentage) {
            translateXPercentage = percentage;
        }

        public float getTranslateXPercentage() {
            return translateXPercentage;
        }

        @Override
        public void updateDrawState(TextPaint paint) {
            paint.setStyle(Paint.Style.FILL);
            float width = paint.getTextSize() * colors.length;
            if (shader == null) {
                shader = new LinearGradient(0, 0, 0, width, colors, null,
                        Shader.TileMode.MIRROR);
            }
            matrix.reset();
            matrix.setRotate(90);
            matrix.postTranslate(width * translateXPercentage, 0);
            shader.setLocalMatrix(matrix);
            paint.setShader(shader);
        }
    }


    @Override
    public void onBackPressed() {
        animation(R.id.aboutLayout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();

                // super.onBackPressed();
            }
        }, 700);

    }

    private void animation(int layoutId){

        View view = (View) findViewById(layoutId);

        Animation animation = new TranslateAnimation(0,1500f, 0, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setDuration(800);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);

    }

}