package com.enpara.EnparaApp;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpara.EnparaApp.model.CurrenciesModel;
import com.enpara.EnparaApp.model.CurrencyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.enpara.EnparaApp.MainActivity.EnparaCurrency;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    @BindView(R.id.home_txtUSD_Buy)
    TextView txtUSD_BUY;

    @BindView(R.id.home_txtUSD_Sell)
    TextView txtUSD_SELL;

    @BindView(R.id.home_txtEUR_Buy)
    TextView txtEUR_BUY;

    @BindView(R.id.home_txtEUR_Sell)
    TextView txtEUR_SELL;

    @BindView(R.id.home_txtGOLD_Buy)
    TextView txtGOLD_BUY;

    @BindView(R.id.home_txtGOLD_Sell)
    TextView txtGOLD_SELL;

    @BindView(R.id.home_txtLoading)
    TextView txtLoading;

    String[] currencyTypes = {
            "usd",
            "eur",
            "gold",
    };
    String[] urls = {
            "http://www.doviz.com/api/v1/currencies/USD/latest",
            "http://www.doviz.com/api/v1/currencies/EUR/latest",
            "http://www.doviz.com/api/v1/golds/gram-altin/latest",
    };
    private final OkHttpClient client = new OkHttpClient();
    private boolean isLoading = false;


    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        txtUSD_BUY.setText("-");
        txtUSD_SELL.setText("-");
        txtEUR_BUY.setText("-");
        txtEUR_SELL.setText("-");
        txtGOLD_BUY.setText("-");
        txtGOLD_SELL.setText("-");

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    fetchCurrencies();
                } catch (Exception e) {

                }
            }
        }, 100, 10000);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void refreshData(CurrenciesModel currenciesModel) {
        CurrencyModel usd = currenciesModel.getUsd();
        if (usd != null) {
            txtUSD_BUY.setText(String.format("%f TL", usd.getBuy()));
            txtUSD_SELL.setText(String.format("%f TL",usd.getSell()));
        }
        CurrencyModel eur = currenciesModel.getEur();
        if (usd != null) {
            txtEUR_BUY.setText(String.format("%f TL",eur.getBuy()));
            txtEUR_SELL.setText(String.format("%f TL",eur.getSell()));
        }
        CurrencyModel gold = currenciesModel.getGold();
        if (usd != null) {
            txtGOLD_BUY.setText(String.format("%f TL",gold.getBuy()));
            txtGOLD_SELL.setText(String.format("%f TL",gold.getSell()));
        }
    }

    void fetchCurrencies() throws Exception {
        if (!isLoading) {
            Handler mainHandler = new Handler(getActivity().getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    txtLoading.setText("Refreshing...");
                    txtLoading.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            };
            mainHandler.post(myRunnable);
            isLoading = true;
            fetchCurrency(0);
        }
    }

    void fetchCurrency(final int typeIndex) {
        if (typeIndex < currencyTypes.length) {
            final String currencyType = currencyTypes[typeIndex];
            String url = urls[typeIndex];

            Log.d("Request URL", url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    fetchCurrency(typeIndex + 1);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);
                    String responseString = response.body().string();
                    System.out.println(responseString);
//                Log.d(currencyType, response.body().string());
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        double selling = jsonObject.getDouble("selling");
                        double buying = jsonObject.getDouble("buying");
                        CurrencyModel curUSD = new CurrencyModel(buying, selling);
                        updateCurrency(currencyType, curUSD);
                    } catch (JSONException je) {

                    }
                    fetchCurrency(typeIndex + 1);
                }
            });
        } else {
            Handler mainHandler = new Handler(getActivity().getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    txtLoading.setText("Refreshed");
                    txtLoading.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            };
            mainHandler.post(myRunnable);

            isLoading = false;
        }
    }

    void updateCurrency(String currency, CurrencyModel value) {

        FirebaseDatabase.getInstance()
                .getReference(EnparaCurrency)
                .child(currency)
                .updateChildren(value.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Currency Updated", "^^^^^^^^^&&&&^^^^^^^^^");
                    }
                });

    }

}
