package com.example.tdf02_145_remote.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.example.tdf02_145_remote.MainActivity;
import com.example.tdf02_145_remote.R;

import java.util.ArrayList;

/**
 * @file ExpressionsFragment.java
 * @brief Extends Fragment
 * @details
 * @li A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity.
 * @li This one deals with the Expressions aspect
 */


/**
 * @brief Expressions Fragment
 * @details
 * @li A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity
 * @li This one deals with the Expressions aspect
 */
public class ExpressionsFragment extends Fragment {

    /**
     * @brief Constructor
     * @details Required empty public constructor
     */
    public ExpressionsFragment() {
    }

    /**
     * @brief Called to have the fragment instantiate its user interface view
     * @details
     * @li This fragment has a View inflated from the grid_view.xml file
     * @li This View has a GridView with the resourceID "grid"
     * @li This GridView is associated with an Adapter which is an ExpressionsWordAdapter object
     * @li The ExpressionsWordAdapter object returns custom itemView object to populate the GridView
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container This is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating grid_view.xml //
        View rootView = inflater.inflate(R.layout.grid_view, container, false);

        // A list of Word objects //
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word(R.string.happy, R.drawable.smile));
        words.add(new Word(R.string.sad, R.drawable.sad));
        words.add(new Word(R.string.angry, R.drawable.angry));
        words.add(new Word(R.string.idle, R.drawable.idle));
        words.add(new Word(R.string.surprised, R.drawable.surprise));

        // Command String variables for expressions //
        final String EMOTION_CLASSIFIER = getString(R.string.EMOTION_CLASSIFIER);
        final String TAIL_STRING = "000";
        final String VERBALIZE_EMOTION = "V";

        // An ExpressionsWordAdapter whose data source is a list of Words //
        // The adapter knows how to create custom itemViews for each item in the list //
        ExpressionsWordAdapter adapter = new ExpressionsWordAdapter(getActivity(), words, R.color.category_color);

        // Find the GridView view hierarchy of the Activity with the ID "grid" //
        GridView gridView = rootView.findViewById(R.id.grid);

        // Number of columns in the gridView //
        gridView.setNumColumns(1);

        // Make gridView use the ExpressionsWordAdapter object we created above, so that the gridView will display items for each Word in the list.
        gridView.setAdapter(adapter);

        // Set a click listener to send command String when the list item is clicked //
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Word word = words.get(position);
                MainActivity.writeToBluetooth(EMOTION_CLASSIFIER, getString(word.getStringResourceId()), TAIL_STRING);

            }
        });

        // Set a click listener to play audio when the list item is long pressed //
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Word word = words.get(position);
                MainActivity.writeToBluetooth(EMOTION_CLASSIFIER, getString(word.getStringResourceId()) + VERBALIZE_EMOTION, TAIL_STRING);
                return true;
            }
        });

        return rootView;
    }
}