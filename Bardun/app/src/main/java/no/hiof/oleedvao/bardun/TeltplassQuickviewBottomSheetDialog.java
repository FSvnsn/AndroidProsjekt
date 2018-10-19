package no.hiof.oleedvao.bardun;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import no.hiof.oleedvao.bardun.R;

/**
 * Created by Caroline on 19.10.2018.
 */

public class TeltplassQuickviewBottomSheetDialog extends BottomSheetDialogFragment {

        private BottomSheetListener mListener;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.bottomsheet_teltplass_quickview, container, false);

            // TODO: Fyll inn teltplassinfo i bottomsheet

            Button btnVisTeltplass = v.findViewById(R.id.btn_visTeltplassActivity);
            btnVisTeltplass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClicked("Vis teltplass klikket");
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
