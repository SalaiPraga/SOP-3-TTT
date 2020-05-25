package com.example.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class PlayerNameDialog extends AppCompatDialogFragment {

    String nameA , nameB;
    private mDialogListener listener;
    EditText playerA ;
    EditText playerB ;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.player_names, null);

        playerA = (EditText) view.findViewById(R.id.playerAName);
        playerB = (EditText) view.findViewById(R.id.playerBName);

        builder.setView(view)
                .setTitle("Enter Names")
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        nameA = playerA.getText().toString();
                        nameB = playerB.getText().toString();
                        listener.applytexts(nameA, nameB);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (mDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" Must implement mDialogListener");
        }
    }

    public interface mDialogListener{
        void applytexts(String nameA, String nameB);
    }
}
