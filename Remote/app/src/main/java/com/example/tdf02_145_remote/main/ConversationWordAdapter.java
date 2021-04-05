/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.tdf02_145_remote.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.tdf02_145_remote.R;

import java.util.ArrayList;

/**
 * @file ExpressionsWordAdapter.java
 * @brief An ArrayAdapter Implementation
 * @details Contains an ArrayAdapter that can provide the layout for each list item based on a data source, which is a list of Word class objects.
 */

/**
 * @brief An ArrayAdapter Implementation
 * @details
 * @li This is an ArrayAdapter used to provide views for the AdapterView
 * @li Returns a view for each object in a collection of Word objects provided
 */
public class ConversationWordAdapter extends ArrayAdapter<Word> {

    /**
     * Resource ID for the background color for this list of Word objects
     */
    private int mColorResourceId;

    /**
     * @brief Constructor
     * @details Constructor for an ExpressionsWordAdapter object.
     * @param context         the current context (i.e. Activity) that the adapter is being created in
     * @param words           the list of Word objects to be displayed
     * @param colorResourceId the resource ID for the background color for this list of words
     *
     */
    public ConversationWordAdapter(Context context, ArrayList<Word> words, int colorResourceId) {
        super(context, 0, words);
        mColorResourceId = colorResourceId;
    }

    /**
     * @param position    specified position in the data set of the Word to be displayed
     * @param convertView a View type. Here value is null. Instead expressions_grid_item.xml is inflated to create the View
     * @param parent      Parent View object of the View object being returned
     * @return a View that displays the data at the specified position in the data set
     * @brief Return a View object
     * @details
     * @li Check if an existing view is being reused, otherwise inflate the view
     * @li getView() was `@Override` in order to return a custom type View
     * @li Called back automatically by Android
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;

        if (itemView == null) {
            // Inflating expressions_grid_item.xml //
            itemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.expressions_grid_item, parent, false);
        }

        // Get the Word object located at this position in the list //
        Word currentWord = getItem(position);

        // Find the TextView in the expressions_grid_item.xml layout with the ID "default_text_view" //
        TextView defaultTextView = (TextView) itemView.findViewById(R.id.default_text_view);

        // Get the text from the currentWord object and set this text on the default TextView //
        defaultTextView.setText(currentWord.getStringResource());

        // Find the ImageView in the expressions_grid_item.xml layout with the ID "image" //
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);

        if (currentWord.hasImage()) {
            // If an image is available, display the provided image based on the resource ID //
            imageView.setImageResource(currentWord.getImageResourceId());

            // Make sure the view is visible //
            imageView.setVisibility(View.VISIBLE);
        } else {
            // Otherwise hide the ImageView (set visibility to GONE) //
            imageView.setVisibility(View.GONE);
        }

        // Set the background color of the text container View //
        View textContainer = itemView.findViewById(R.id.text_container);
        int color = ContextCompat.getColor(getContext(), mColorResourceId); // Find the color that the resource ID maps to //
        textContainer.setBackgroundColor(color);

        // Return the itemView object(containing one TextView and one ImageView) so that it can be shown in the GridView //
        return itemView;
    }
}