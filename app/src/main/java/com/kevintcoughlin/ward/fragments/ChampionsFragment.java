package com.kevintcoughlin.ward.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kevintcoughlin.ward.R;
import com.kevintcoughlin.ward.adapters.ChampionsAdapter;
import com.kevintcoughlin.ward.http.DataDragonClient;
import com.kevintcoughlin.ward.http.RiotGamesClient;
import com.kevintcoughlin.ward.models.ChampionMetaData;
import com.kevintcoughlin.ward.models.DataDragonChampion;
import com.kevintcoughlin.ward.models.DataDragonChampionsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public final class ChampionsFragment extends Fragment implements Callback<DataDragonChampionsData> {
    @InjectView(R.id.list) RecyclerView mRecyclerView;
    public static final String TAG = "Champions";
    private ChampionsAdapter mAdapter;
    private ArrayList<DataDragonChampion> mChampions = new ArrayList<>();
    private HashMap<Integer, DataDragonChampion> mChampionsData = new HashMap<>();

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_champions, container, false);
        ButterKnife.inject(this, view);

        getActivity().setTitle(TAG);
        final int mNumColumns = 3;
        final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), mNumColumns);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ChampionsAdapter(getActivity(), mChampions);
        mRecyclerView.setAdapter(mAdapter);

        DataDragonClient.getClient().getChampions(this);

        return view;
    }

    private void getFreeToPlay() {
        RiotGamesClient.getClient().listChampions("na", true, new Callback<Map<String, ChampionMetaData[]>>() {
            @Override
            public void success(Map<String, ChampionMetaData[]> stringChampionMetaDataMap, Response response) {
                for (final ChampionMetaData meta : stringChampionMetaDataMap.get("champions")) {
                    final DataDragonChampion champion = mChampionsData.get(meta.id);
                    champion.setFreeToPlay(meta.freeToPlay);
                }
                mAdapter.sort();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override public void success(DataDragonChampionsData dataDragonChampionsData, Response response) {
        for (final Map.Entry<String, DataDragonChampion> champion : dataDragonChampionsData.data.entrySet()) {
            mChampionsData.put(Integer.valueOf(champion.getValue().getKey()), champion.getValue());
        }
        mChampions.addAll(mChampionsData.values());
        mAdapter.notifyDataSetChanged();
        getFreeToPlay();
    }

    @Override public void failure(RetrofitError error) {
        Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}