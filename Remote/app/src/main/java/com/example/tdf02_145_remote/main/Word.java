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

/**
 * @file Word.java
 * @brief Represents a collection of resource IDs
 * @details It can contain resource IDs for Strings, Images, audio files, among other things
 * @details It contains resource IDs for Strings and optional Images
 */

/**
 * @brief Represents a collection of resource IDs
 * @details It contains resource IDs for Strings and optional Images
 */
public class Word {

    /** String resource ID for the text associated with a Word */
    private int mStringResourceId;

    private String mStringResource;

    /** Optional image resource ID for the image associated with a Word */
    private int mImageResourceId = NO_IMAGE_PROVIDED;

    /** Constant value that represents no image was provided for this word */
    private static final int NO_IMAGE_PROVIDED = -1;

    /**
     * @brief Constructor
     * @details A constructor that only takes in a String resourceID. Not used. Kept for later use.
     * @param stringResourceId is the string resource ID for the text associated with a Word
     */
    public Word(int stringResourceId) {
        mStringResourceId = stringResourceId;
    }

    /**
     * @brief Constructor
     * @details A constructor that only takes in a String resourceID and an Image resourceId
     *
     * @param stringResourceId is the string resource ID for the text associated with a Word
     * @param imageResourceId is the drawable resource ID for the image associated with the Word
     */
    public Word(int stringResourceId, int imageResourceId) {
        mStringResourceId = stringResourceId;
        mImageResourceId = imageResourceId;
    }

    public Word(String stringResource) {
        mStringResource = stringResource;
    }
    /**
     * Get the string resource ID for the text associated with the Word
     * @return integer Resource ID
     */
    public int getStringResourceId() {
        return mStringResourceId;
    }

    public String getStringResource() {
        return mStringResource;
    }


    /**
     * Get the image resource ID for the image associated with the Word
     * @return integer Resource ID
     */
    public int getImageResourceId() {
        return mImageResourceId;
    }

    /**
     * Returns whether or not there is an image for this word.
     * @return boolean TRUE or FALSE
     */
    public boolean hasImage() {
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }


}