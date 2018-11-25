package no.hiof.oleedvao.bardun.fragment;

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

import no.hiof.oleedvao.bardun.teltplass.TeltplassActivity;

import static no.hiof.oleedvao.bardun.R.*;
import static no.hiof.oleedvao.bardun.R.id.*;

/**
 * Created by Caroline on 19.10.2018.
 */

public class TeltplassQuickviewBottomSheetDialog extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private static final String TAG = "Superman";
    private View v;


    @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

                v = inflater.inflate(layout.bottomsheet_teltplass_quickview, container, false);
                Log.d(TAG, "quickview teltplass skal vises");

                //Tar i mot data fra MainAcitivty
                String latlong = getArguments().getString("latlong");
                String tittel = getArguments().getString("tittel");
                String brukernavn = getArguments().getString("brukernavn");
                String dato = getArguments().getString("dato");
                final String id = getArguments().getString("id");

                //Henter id på de ulike layout-elementene i bottom sheet
                TextView tv_bottomsheet_tittel = v.findViewById(bottom_sheet_teltplass_tittel);
                TextView tv_latlong = v.findViewById(tv_bottomsheet_latlong);
                TextView tv_brukernavn = v.findViewById(tv_bottomsheet_brukernavn);
                TextView tv_dato = v.findViewById(tv_bottomsheet_dato);

                //Putter inn data fra marker her
                tv_bottomsheet_tittel.setText(tittel);
                tv_latlong.setText(latlong);
                tv_brukernavn.setText(brukernavn);
                tv_dato.setText(dato);

                final ImageButton ib_favoritt = v.findViewById(image_button_favoritt);
                ib_favoritt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Favoritt lagt til", Toast.LENGTH_SHORT).show();
                    }
                });

                Button btnVisTeltplass = v.findViewById(btn_visTeltplassActivity);
                btnVisTeltplass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onButtonClicked("Vis teltplass klikket");
                        //Start TeltplassActivity ved å sende med ID
                        Intent intent = new Intent(getActivity(), TeltplassActivity.class);
                        intent.putExtra("Id", id);
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
                mListener = (BottomSheetListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement BottomSheetListener");
            }
        }

}
