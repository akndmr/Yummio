package io.github.akndmr.yummio.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.github.akndmr.yummio.R;
import io.github.akndmr.yummio.model.Recipe;
import io.github.akndmr.yummio.ui.RecipeActivity;

/**
 * Created by AKIN on 25.02.2018.
 */

public class DialogUtil {

    private static AlertDialog dialog;
    private static AlertDialog dialogWithButtons;

    public static void closeDialog() {
        dialog.dismiss();
    }

    // To be implemented on 2nd Stage
    public static void showDialogWithButtons(final Context context, int resId, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.custom_dialog_with_buttons, null);
        builder.setView(customView);

        CardView cardView = customView.findViewById(R.id.cv_dialog_card);
        ImageView dialogImage = customView.findViewById(R.id.iv_dialog_image);
        ImageView dialogClose = customView.findViewById(R.id.iv_dialog_close);
        TextView dialogText = customView.findViewById(R.id.tv_dialog_text);
        Button dialogButtonRetry = customView.findViewById(R.id.btn_retry);
        Button dialogButtonExit = customView.findViewById(R.id.btn_exit);
        Picasso.with(context).load(resId).into(dialogImage);
        dialogText.setText(message);
        cardView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));

        dialogWithButtons = builder.create();

        dialogButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWithButtons.dismiss();
                ((Activity)context).finish();
            }
        });

        dialogButtonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWithButtons.dismiss();
                Intent intent = ((Activity) context).getIntent();
                ((Activity)context).finish();
                context.startActivity(intent);
            }
        });

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWithButtons.dismiss();
            }
        });

        dialogWithButtons.show();
    }
}
