package com.enpara.EnparaApp;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.afollestad.materialdialogs.internal.MDTintHelper;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.enpara.EnparaApp.model.AlertModel;
import com.enpara.EnparaApp.model.CurrenciesModel;
import com.enpara.EnparaApp.model.CurrencyModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        AlertFragment.AlertFragmentListener {

    public final static String EnparaCurrency = "enpara_currency1";
    public final static String EnparaAlert = "enpara_alert";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private HomeFragment homeFragment;
    private AlertFragment alertFragment;

    private String currentUserId;

    private CurrenciesModel currenciesModel;
    private ArrayList<AlertModel> arrayAlerts = new ArrayList<>();

    private Spinner spinnerCurrencyType;
    private Spinner spinnerAlertType;

    private double usdBuy = 0.0;
    private double usdSell = 0.0;
    private double eurBuy = 0.0;
    private double eurSell = 0.0;
    private double goldBuy = 0.0;
    private double goldSell = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        homeFragment = HomeFragment.newInstance();
        alertFragment = AlertFragment.newInstance();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        initOneSignal();

    }

    private void initOneSignal() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.InAppAlert)
                .unsubscribeWhenNotificationsAreDisabled(false)
                .setNotificationReceivedHandler(new OneSignal.NotificationReceivedHandler() {
                    @Override
                    public void notificationReceived(OSNotification notification) {
                        Log.d("OneSignal Notification", notification.toString());
                    }
                })
                .init();

        final KProgressHUD hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();

        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {

                Log.d("OneSignal", "PlayerID: " + userId + "\nPushToken: " + registrationId);

                registerUserToFirebase(userId, registrationId);
                hud.dismiss();
            }
        });
    }

    private void registerUserToFirebase(String userId, String pushToken) {
        if (userId == null) return;
        if (pushToken == null)
            pushToken = "unknown";

        currentUserId = userId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("userId", userId);
        childUpdates.put("pushToken", pushToken);

        FirebaseDatabase.getInstance()
                .getReference("enpara_users")
                .child(userId)
                .updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        initFirebaseObserver();
    }

    void initFirebaseObserver() {
        FirebaseDatabase.getInstance()
                .getReference(EnparaCurrency)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        currenciesModel = dataSnapshot.getValue(CurrenciesModel.class);
                        if (currenciesModel != null) {
                            homeFragment.refreshData(currenciesModel);
                        }
                        checkAlert();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        FirebaseDatabase.getInstance()
                .getReference(getAlertPath())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        arrayAlerts.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AlertModel alertModel = snapshot.getValue(AlertModel.class);
                            arrayAlerts.add(alertModel);
                        }
                        alertFragment.setArrayAlerts(arrayAlerts);
                        checkAlert();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onLongClickAlert(AlertModel alertModel) {
        showEditDialog(alertModel);
    }

    String selectedCurrencyType = "USD";
    String selectedAlertType    = "BUY";
    Double selectedValue        = 0.0;
    int tempCount = 0;

    private void showEditDialog(final AlertModel alertModel) {
        String title = "Add new Alert";
        tempCount = 2;
        if (alertModel != null) {
            tempCount = 0;
            title = "Edit alert";
        }

        MaterialDialog.SingleButtonCallback buttonCallback =
                new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("MDDailog", which.name());
                        if (which == DialogAction.POSITIVE) {
                            AlertModel willUpdateAlert;
                            if (alertModel == null) {
                                willUpdateAlert = new AlertModel(
                                        selectedCurrencyType,
                                        selectedAlertType,
                                        selectedValue
                                );
                            } else {
                                willUpdateAlert = alertModel;
                                willUpdateAlert.setCurrency(selectedCurrencyType);
                                willUpdateAlert.setAlertType(selectedAlertType);
                                willUpdateAlert.setValue(selectedValue);
                            }
                            saveAlert(willUpdateAlert);
                        } else if (which == DialogAction.NEUTRAL) {
                            deleteAlert(alertModel);
                        }
                    }
                };
        MaterialDialog.Builder builder =
                new MaterialDialog.Builder(this)
                        .title(title)
                        .customView(R.layout.dialog_edit_alert, true)
                        .positiveText("Save")
                        .negativeText(android.R.string.cancel)
                        .onPositive(buttonCallback)
                        .onNeutral(buttonCallback);

        if (alertModel != null) {
            builder.neutralText("Delete")
                    .neutralColor(getResources().getColor(android.R.color.holo_red_dark));
        }
        MaterialDialog dialog = builder.build();

        final MDButton btnPositive = dialog.getActionButton(DialogAction.POSITIVE);
        final MDButton btnNeutral = dialog.getActionButton(DialogAction.NEUTRAL);

        final Spinner spinnerCurrencyType = (Spinner) dialog.getCustomView().findViewById(R.id.dialog_edit_spinnerCurrencyType);
        final Spinner spinnerAlertType    = (Spinner) dialog.getCustomView().findViewById(R.id.dialog_edit_spinnerAlertType);
        final EditText edtValue = (EditText) dialog.getCustomView().findViewById(R.id.dialog_edit_txtValue);

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerCurrencyType.getSelectedItem() != null) {
                    selectedCurrencyType = spinnerCurrencyType.getSelectedItem().toString();
                    CurrencyModel selectedCurrency;
                    if (selectedCurrencyType.toUpperCase().equals("USD")) {
                        selectedCurrency = currenciesModel.getUsd();
                    } else if (selectedCurrencyType.toUpperCase().equals("EUR")) {
                        selectedCurrency = currenciesModel.getEur();
                    } else {
                        selectedCurrency = currenciesModel.getGold();
                    }
                    if (spinnerAlertType.getSelectedItemPosition() >= 0) {
                        selectedValue = 0.0;
                        if (spinnerAlertType.getSelectedItemPosition() == 0) {
                            selectedValue = selectedCurrency.getBuy();
                        } else {
                            selectedValue = selectedCurrency.getSell();
                        }
                        selectedAlertType = spinnerAlertType.getSelectedItem().toString();
                        if (tempCount > 1) {
                            edtValue.setText(String.valueOf(selectedValue));
                        }
                        tempCount ++;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        spinnerCurrencyType.setOnItemSelectedListener(listener);
        spinnerAlertType.setOnItemSelectedListener(listener);
        if (alertModel != null) {
            if (alertModel.getCurrency().toUpperCase().equals("USD")) {
                spinnerCurrencyType.setSelection(0);
            } else if (alertModel.getCurrency().toUpperCase().equals("EUR")) {
                spinnerCurrencyType.setSelection(1);
            } else {
                spinnerCurrencyType.setSelection(2);
            }
            if (alertModel.getAlertType().toUpperCase().equals("BUY")) {
                spinnerAlertType.setSelection(0);
            } else {
                spinnerAlertType.setSelection(1);
            }
            edtValue.setText(String.valueOf(alertModel.getValue()));
        }

        edtValue.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().length() > 0) {
                            btnPositive.setEnabled(true);
                            selectedValue = Double.valueOf(s.toString());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        int widgetColor = ThemeSingleton.get().widgetColor;

        MDTintHelper.setTint(
                edtValue,
                widgetColor == 0 ? ContextCompat.getColor(this, R.color.colorAccent) : widgetColor);

        dialog.show();
        btnPositive.setEnabled(false); // disabled by default

    }

    void saveAlert(AlertModel alertModel) {

        if (currentUserId == null) return;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getAlertPath());
        String alertKey = alertModel.getKey();
        Map<String, Object> updateChildren = alertModel.toMap();

        if (alertKey == null) {
            alertKey = reference.push().getKey();
            updateChildren.put("key", alertKey);
        }
        reference.child(alertKey)
                .updateChildren(updateChildren)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    void deleteAlert(AlertModel alertModel) {
        FirebaseDatabase.getInstance()
                .getReference(getAlertPath())
                .child(alertModel.getKey())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private String getAlertPath() {
        if (currentUserId == null) {
            return String.format("%s/unknown", EnparaAlert);
        } else {
            return String.format("%s/%s", EnparaAlert, currentUserId);
        }
    }

    private void checkAlert() {
        if (currenciesModel != null) {
            String message = "";
            for (AlertModel alertModel : arrayAlerts) {
                if (alertModel.getCurrency().toUpperCase().equals("USD") &&
                        alertModel.getAlertType().toUpperCase().equals("BUY")) {
                    if (alertModel.getValue() >= currenciesModel.getUsd().getBuy() &&
                            usdBuy != currenciesModel.getUsd().getBuy()) {
                        message += String.format("USD Buy : %f TL\n", currenciesModel.getUsd().getBuy());
                        usdBuy = currenciesModel.getUsd().getBuy();
                    }
                }
                else if (alertModel.getCurrency().toUpperCase().equals("USD") &&
                        alertModel.getAlertType().toUpperCase().equals("SELL")) {
                    if (alertModel.getValue() <= currenciesModel.getUsd().getSell() &&
                            usdSell != currenciesModel.getUsd().getSell()) {
                        message += String.format("USD Sell : %f TL\n", currenciesModel.getUsd().getSell());
                        usdSell = currenciesModel.getUsd().getSell();
                    }
                }
                else if (alertModel.getCurrency().toUpperCase().equals("EUR") &&
                        alertModel.getAlertType().toUpperCase().equals("BUY")) {
                    if (alertModel.getValue() >= currenciesModel.getEur().getBuy() &&
                            eurBuy != currenciesModel.getEur().getBuy()) {
                        message += String.format("EUR Buy : %f TL\n", currenciesModel.getEur().getBuy());
                        eurBuy = currenciesModel.getEur().getSell();
                    }
                }
                else if (alertModel.getCurrency().toUpperCase().equals("EUR") &&
                        alertModel.getAlertType().toUpperCase().equals("SELL")) {
                    if (alertModel.getValue() <= currenciesModel.getEur().getSell() &&
                            eurSell != currenciesModel.getEur().getSell()) {
                        message += String.format("EUR Sell : %f TL\n", currenciesModel.getEur().getSell());
                        eurSell = currenciesModel.getEur().getSell();
                    }
                }
                else if (alertModel.getCurrency().toUpperCase().equals("GOLD") &&
                        alertModel.getAlertType().toUpperCase().equals("BUY")) {
                    if (alertModel.getValue() >= currenciesModel.getGold().getBuy() &&
                            goldBuy != currenciesModel.getGold().getBuy()) {
                        message += String.format("GOLD Buy : %f TL\n", currenciesModel.getGold().getBuy());
                        goldBuy = currenciesModel.getGold().getBuy();
                    }
                }
                else if (alertModel.getCurrency().toUpperCase().equals("GOLD") &&
                        alertModel.getAlertType().toUpperCase().equals("SELL")) {
                    if (alertModel.getValue() <= currenciesModel.getGold().getSell() &&
                            goldSell != currenciesModel.getGold().getSell()) {
                        message += String.format("GOLD Sell : %f TL\n", currenciesModel.getGold().getSell());
                        goldSell = currenciesModel.getGold().getSell();
                    }
                }
            }
            if (message.length() > 0) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(this);
                }
                builder.setTitle("Updated!")
                        .setMessage(message)
                        .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return homeFragment;
            } else {
                return alertFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.home);
                case 1:
                    return getString(R.string.alert);
            }
            return null;
        }
    }
}
