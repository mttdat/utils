package mttdat.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;

import java.util.HashSet;

import ss.wohui.R;

public class DialogUtils {

    /**
     * create waiting dialog
     *
     * @param context
     * @return
     */
    @SuppressLint("InlinedApi")
    public static Dialog createWaitingDialog(Context context) {
        int themes = R.style.LoadingDialogTheme;
        Dialog dialog = new Dialog(context, themes);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_waiting);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    /**
     * create message dialog
     *
     * @param context
     * @param message
     * @param okConfirm
     * @return
     */
    public static Dialog createMessageDialog(Context context, Dialog dialog,
                                             String message, String okConfirm) {
        if(dialog != null){
            AlertDialog _dialog = (AlertDialog) dialog;
            _dialog.setMessage(message);
            return _dialog;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle(context.getString(R.string.app_name)).setMessage(message)
                .setCancelable(false)
                .setPositiveButton(okConfirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                            }
                        });
        dialog = builder.create();

        return dialog;
    }

    public interface OnPositiveClickListener{
        void onPositiveClick();
    }

    public interface OnNegativeClickListener{
        void onNegativeClick();
    }

    public static Dialog createMessageDialog(Context context, Dialog dialog,
                                             String message, String okConfirm, final OnPositiveClickListener onPositiveClickListener) {

        if(dialog != null){
            AlertDialog _dialog = (AlertDialog) dialog;
            _dialog.setMessage(message);
            return _dialog;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle(context.getString(R.string.app_name)).setMessage(message)
                .setCancelable(false)
                .setPositiveButton(okConfirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.dismiss();
                                if(onPositiveClickListener != null) {
                                    onPositiveClickListener.onPositiveClick();
                                }
                            }
                        });

        dialog = builder.create();

        return dialog;
    }

    public static Dialog createMessageDialog(Context context, Dialog dialog, String message,
                                             String okConfirm, final OnPositiveClickListener onPositiveClickListener,
                                             String cancelConfirm, final OnNegativeClickListener onNegativeClickListener) {

        if(dialog != null){
            AlertDialog _dialog = (AlertDialog) dialog;
            _dialog.setMessage(message);
            return _dialog;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        builder.setTitle(context.getString(R.string.app_name)).setMessage(message)
                .setCancelable(false)
                .setPositiveButton(okConfirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        if(onPositiveClickListener != null) {
                            onPositiveClickListener.onPositiveClick();
                        }
                    }
                })
                .setNegativeButton(cancelConfirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        if(onNegativeClickListener != null) {
                            onNegativeClickListener.onNegativeClick();
                        }
                    }
                });

        dialog = builder.create();

        return dialog;
    }

    /**
     * show loading
     */
    public static Dialog showLoadingDialog(Context context, Dialog mLoadingDialog) {
        try {
            if (mLoadingDialog == null) {
                mLoadingDialog = DialogUtils.createWaitingDialog(context);
            }
            if (!mLoadingDialog.isShowing()) {
                mLoadingDialog.show();
            }

            return mLoadingDialog;
        } catch (Exception ex) {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     * close loading
     */
    public static  void closeLoadingDialog(Dialog mLoadingDialog) {
        try {
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* Show dialog */
    public static void showDialog(Dialog dialog) {
        try {
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showDialog(HashSet<Dialog> dialogs, Dialog dialog) {
        try {
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
                dialogs.add(dialog);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* Close dialog. */
    public static void closeDialog(Dialog dialog) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void closeDialog(HashSet<Dialog> dialogs, Dialog dialog) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
                dialogs.remove(dialog);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
