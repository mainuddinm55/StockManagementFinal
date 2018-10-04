package com.kcirque.stockmanagementfinal.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kcirque.stockmanagementfinal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellProductFragment extends Fragment {

    private static SellProductFragment INSTANCE;

    public static synchronized SellProductFragment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SellProductFragment();
        }

        return INSTANCE;
    }
    public SellProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sell_product, container, false);
    }

}
