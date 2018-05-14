package com.enpara.EnparaApp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enpara.EnparaApp.R;
import com.enpara.EnparaApp.model.AlertModel;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ilhan.kaya on 6/3/2017.
 */

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.AlertViewHolder> {

    private ArrayList<AlertModel> arrayAlerts;
    private AlertsAdapterListener alertsAdapterListener;

    public AlertsAdapter(AlertsAdapterListener listener) {
        alertsAdapterListener = listener;
    }

    @Override
    public AlertViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_alert, parent, false);
        return new AlertViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlertViewHolder holder, int position) {
        final AlertModel alertModel = arrayAlerts.get(position);
        holder.txtCurrency.setText(alertModel.getCurrency());
        holder.txtAlertType.setText(alertModel.getAlertType());
        holder.txtValue.setText(String.format("%s TL", alertModel.getValue()));
        holder.txtTime.setText(getDate(alertModel.getTime()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                alertsAdapterListener.onLongClickItem(alertModel);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayAlerts == null ? 0 : arrayAlerts.size();
    }

    public void setArrayAlerts(ArrayList<AlertModel> arrayAlerts) {
        this.arrayAlerts = arrayAlerts;
        notifyDataSetChanged();
    }

    private String getDate(long timeStamp){

        try{
//            timeStamp *= 1000;
            DateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }


    class AlertViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cell_alert_txtCurrency)
        TextView txtCurrency;

        @BindView(R.id.cell_alert_txtAlertType)
        TextView txtAlertType;

        @BindView(R.id.cell_alert_txtValue)
        TextView txtValue;

        @BindView(R.id.cell_alert_txtTime)
        TextView txtTime;

        public AlertViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

    public interface AlertsAdapterListener {
        void onLongClickItem(AlertModel alertModel);
    }
}
