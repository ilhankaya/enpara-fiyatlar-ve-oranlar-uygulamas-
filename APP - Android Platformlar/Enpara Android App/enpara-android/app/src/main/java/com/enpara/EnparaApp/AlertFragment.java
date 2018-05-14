package com.enpara.EnparaApp;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enpara.EnparaApp.adapter.AlertsAdapter;
import com.enpara.EnparaApp.model.AlertModel;
import com.enpara.EnparaApp.model.CurrenciesModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlertFragment extends Fragment implements AlertsAdapter.AlertsAdapterListener{

    @BindView(R.id.alert_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.fab_addAlert)
    FloatingActionButton btnAddAlert;

    private AlertFragmentListener alertFragmentListener;
    private Context mContext;
    private ArrayList<AlertModel> arrayAlerts = new ArrayList<>();
    private AlertsAdapter alertsAdapter;


    public AlertFragment() {
        // Required empty public constructor
    }

    public static AlertFragment newInstance() {
        AlertFragment fragment = new AlertFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);
        ButterKnife.bind(this, view);

        alertsAdapter = new AlertsAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(alertsAdapter);

        btnAddAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alertFragmentListener != null) {
                    alertFragmentListener.onLongClickAlert(null);
                }
            }
        });
        btnAddAlert.setEnabled(false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof AlertFragmentListener) {
            alertFragmentListener = (AlertFragmentListener) context;
        }
    }


    public void setArrayAlerts(ArrayList<AlertModel> arrayAlerts) {
        this.arrayAlerts = arrayAlerts;
        alertsAdapter.setArrayAlerts(arrayAlerts);
        btnAddAlert.setEnabled(true);
    }

    @Override
    public void onLongClickItem(AlertModel alertModel) {
        if (alertFragmentListener != null) {
            alertFragmentListener.onLongClickAlert(alertModel);
        }
    }


    public interface AlertFragmentListener {
        void onLongClickAlert(AlertModel alertModel);
    }

}
