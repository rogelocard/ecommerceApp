package com.example.conceptsandroidstudio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment productsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductsFragment newInstance(String param1, String param2) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    /*public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<MyItem> itemList = new ArrayList<>();
        itemList.add(new MyItem(R.drawable.image1, "Item 1"));
        itemList.add(new MyItem(R.drawable.image2, "Item 2"));
        itemList.add(new MyItem(R.drawable.image3, "Item 3"));
        itemList.add(new MyItem(R.drawable.image4, "Item 4"));
        itemList.add(new MyItem(R.drawable.image5, "Item 5"));

        MyAdapter adapter = new MyAdapter(itemList);
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false);*/

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_products, container, false);

            RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

            List<MyItem> itemList = new ArrayList<>();
            itemList.add(new MyItem(R.drawable.image_portatil, "Pc"));
            itemList.add(new MyItem(R.drawable.image_tv, "Tv"));
            itemList.add(new MyItem(R.drawable.image_celular, "Celulares"));
            itemList.add(new MyItem(R.drawable.image_reloj, "Relojes"));
            itemList.add(new MyItem(R.drawable.image_accesorios, "Accesorios"));

            MyAdapter adapter = new MyAdapter(itemList);
            recyclerView.setAdapter(adapter);

            return view;
        }
}
