package com.example.tdf02_145_remote.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.tdf02_145_remote.R;

/**
 * @file SectionsPagerAdapter.java
 * @brief Handles the tabbed "Whatsapp" look.
 * @details Class that extends FragmentPagerAdapter. Implementation of PagerAdapter that represents each page as a Fragment that is persistently kept in the fragment manager as long as the user can return to the page.
 */

/**
 * @brief Handles the tabbed "Whatsapp" look.
 * @details Class that extends FragmentPagerAdapter. Implementation of PagerAdapter that represents each page as a Fragment that is persistently kept in the fragment manager as long as the user can return to the page.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    /**
     * @brief Integer references to String resources
     * @details Integer Array consisting of references to String resources. `@StringRes` identifies the integers as String references.
     */
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3, R.string.tab_text_4};

    /**
     * @brief Holder of Activity context reference
     * @details Context refers to the activity where the adapter is created. It is helps you with accessing system services and resources in case you need them. mContext will store the reference to the Activity where the SectionsPagerAdapter object instance is created.
     */
    private final Context mContext;

    /**
     * @brief Constructor for SectionsPagerAdapter
     * @details Used to create an instance of SectionsPagerAdapter from an Activity. The super() method initializes the parent class  FragmentPagerAdapter by calling its default constructor. The FragmentManager object passed to this constructor is passed along to the constructor of the base class.
     * @param context Reference to the Activity where the SectionsPagerAdapter object instance is created
     * @param fm FragmentManager passed from the Activity where the SectionsPagerAdapter object instance is created. ??
     */
    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * @brief Return the Fragment associated with a specified position
     * @details
     * @li getItem is called to instantiate the fragment for the given page
     * @li Here it returns a Fragment of a certain class type
     * @param position Integer for position
     * @return Fragment associated with position
     */
    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                return new ExpressionsFragment();
            case 1:
                return new LocomotionFragment();
            case 2:
                return new SpeakingFragment();
            default: //case 3://
                return new ConversationFragment();
        }
    }

    /**
     * @brief Called by ViewPager to obtain title for page
     * @details This method is called by the ViewPager to obtain a title string to describe the specified page. This method may return null indicating no title for this page. The default implementation returns null.
     * @param position The position of the title requested
     * @return `@Nullable` denotes that a value can be null.
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     * @brief Total pages
     * @details Returns the number of views available
     * @return Number of views available
     */
    @Override
    public int getCount() {
        return 4;
    }
}