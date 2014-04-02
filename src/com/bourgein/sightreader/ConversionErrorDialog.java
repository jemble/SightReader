package com.bourgein.sightreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class ConversionErrorDialog extends DialogFragment {

	public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("there was an error converting the song. Sorry!")
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
                       startActivity(homeIntent);
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
