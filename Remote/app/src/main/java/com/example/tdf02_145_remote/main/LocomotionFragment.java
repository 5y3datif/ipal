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
 * @file LocomotionFragment.java
 * @brief Extends Fragment
 * @details
 * @li A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity.
 * @li This one deals with the Locomotionaspect
 */


/**
 * @brief Locomotion Fragment
 * @details
 * @li A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity.
 * @li This one deals with the Locomotion aspect
 */
public class LocomotionFragment extends Fragment {

    /**
     * @brief Constructor
     * @details Required empty public constructor
     */
    public LocomotionFragment() {
    }

    /**
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          This is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return The View for the fragment's UI, or null.
     * @brief Called to have the fragment instantiate its user interface view
     * @details
     * @li This fragment has a View inflated from the grid_view.xml file
     * @li This View has a GridView with the resourceID "grid"
     * @li This GridView is associated with an Adapter which is an LocomotionWordAdapter object
     * @li The LocomotionWordAdapter object returns custom itemView object to populate the GridView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating grid_view.xml //
        View rootView = inflater.inflate(R.layout.grid_view, container, false);

        // A list of Word objects //
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.forward));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.leftward));
        words.add(new Word(R.string.empty, R.drawable.color_red));
        words.add(new Word(R.string.empty, R.drawable.rightward));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.backward));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.empty));
        words.add(new Word(R.string.empty, R.drawable.empty));

        // Command String variables for expressions //
        final String FORWARD_MOVEMENT_CLASSIFIER = getString(R.string.FORWARD_MOVEMENT_CLASSIFIER);
        final String BACKWARD_MOVEMENT_CLASSIFIER = getString(R.string.BACKWARD_MOVEMENT_CLASSIFIER);
        final String LEFT_MOVEMENT_CLASSIFIER = getString(R.string.LEFT_MOVEMENT_CLASSIFIER);
        final String RIGHT_MOVEMENT_CLASSIFIER = getString(R.string.RIGHT_MOVEMENT_CLASSIFIER);
        final String STOP_MOVEMENT_CLASSIFIER = getString(R.string.STOP_MOVEMENT_CLASSIFIER);
        final String MODIFIER = "000_000";
        final String distance = "1000";

        // A LocomotionWordAdapter whose data source is a list of Words //
        // The adapter knows how to create custom itemViews for each item in the list //
        LocomotionWordAdapter adapter = new LocomotionWordAdapter(getActivity(), words, R.color.category_color);

        // Find the GridView view hierarchy of the Activity with the ID "grid" //
        GridView gridView = rootView.findViewById(R.id.grid);

        // Number of columns in the gridView //
        gridView.setNumColumns(3);

        // Make gridView use the LocomotionWordAdapter object we created above, so that the gridView will display items for each Word in the list.
        gridView.setAdapter(adapter);

        // Set a click listener to send command String when the list item is clicked //
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position) {
                    case 4:
                        MainActivity.writeToBluetooth(FORWARD_MOVEMENT_CLASSIFIER, distance, MODIFIER);
                        break;
                    case 6:
                        MainActivity.writeToBluetooth(LEFT_MOVEMENT_CLASSIFIER, distance, MODIFIER);
                        break;
                    case 7:
                        MainActivity.writeToBluetooth(STOP_MOVEMENT_CLASSIFIER, "", "");
                        break;
                    case 8:
                        MainActivity.writeToBluetooth(RIGHT_MOVEMENT_CLASSIFIER, distance, MODIFIER);
                        break;
                    case 10:
                        MainActivity.writeToBluetooth(BACKWARD_MOVEMENT_CLASSIFIER, distance, MODIFIER);
                        break;
                     default:
                        break;
                }
            }
        });

        return rootView;
    }
}
