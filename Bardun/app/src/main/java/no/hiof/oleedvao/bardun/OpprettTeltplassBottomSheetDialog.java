/**
 * Created by Caroline on 17.11.2018.
 */
package no.hiof.oleedvao.bardun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import static no.hiof.oleedvao.bardun.R.*;
import static no.hiof.oleedvao.bardun.R.id.*;

public class OpprettTeltplassBottomSheetDialog extends BottomSheetDialogFragment {

    private TeltplassQuickviewBottomSheetDialog.BottomSheetListener mListener;
    private static final String TAG = "Iron Man";
    private View v;
    private String bottomsheetTag;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            v = inflater.inflate(layout.bottomsheet_registrer_teltplass, container, false);

            String latlongstring = getArguments().getString("latlong");
            String[] latlongtemp =  latlongstring.split(",");
            double latitude = Double.parseDouble(latlongtemp[0]);
            double longitude = Double.parseDouble(latlongtemp[1]);
            final LatLng location = new LatLng(latitude, longitude);

            String tittel = getArguments().getString("tittel");

            //Henter id på de ulike layout-elementene i bottom sheet
            TextView tv_bottomsheet_tittel = v.findViewById(bottom_sheet_registrer_tittel);
            TextView tv_latlong = v.findViewById(tv_bottomsheet_registrer_latlong);

            tv_bottomsheet_tittel.setText(tittel);
            tv_latlong.setText("Lat/Long: " + latlongstring);


            Button btnVisTeltplass = v.findViewById(btn_OpprettTeltplassActivity);
            btnVisTeltplass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClicked("Opprett teltplass klikket");
                    //Start OpprettTeltplassActivity ved å sende med LatLng
                    Intent intent = new Intent(getActivity(), OpprettTeltplassActivity.class);
                    intent.putExtra("LatLng", location);
                    startActivity(intent);

                    dismiss();
                }
            });


        return v;

    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (TeltplassQuickviewBottomSheetDialog.BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

}


