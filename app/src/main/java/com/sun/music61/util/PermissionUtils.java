package com.sun.music61.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import com.sun.music61.R;
import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    private static final String PACKAGE = "package";
    private static final int REQUEST_CODE_WRITE_SD = 201;
    private static final int REQUEST_CODE_READ_SD = 202;
    private static List<PermissionReq> sPermissionReqList = new ArrayList<>();

    public static void checkWriteExternalPermission(Activity activity,
            PermissionUtils.ReqPermissionCallback callback) {
        PermissionUtils.checkPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                REQUEST_CODE_WRITE_SD,
                activity.getString(R.string.text_req_reason_write_external),
                activity.getString(R.string.text_rejected_msg),
                callback);
    }

    public static void checkReadExternalPermission(Activity activity,
            PermissionUtils.ReqPermissionCallback callback) {
        PermissionUtils.checkPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                REQUEST_CODE_READ_SD,
                activity.getString(R.string.text_req_reason_read_external),
                activity.getString(R.string.text_rejected_msg),
                callback);
    }

    public interface ReqPermissionCallback {
        void onResult(boolean success);
    }

    public static boolean hasPermission(Activity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkPermission(Activity activity,
            String permission,
            int reqCode,
            CharSequence reqReason,
            CharSequence rejectedMsg,
            final ReqPermissionCallback callback) {
        if (hasPermission(activity, permission)) {
            // we shouldn't callback directly, it will mix the sync and async logic
            // if you want to check permission sync, you should manually call hasPermission() method
            // callback.onResult(true);
            activity.getWindow().getDecorView().post(() -> callback.onResult(true));
        } else {
            boolean shouldShowReqReason = ActivityCompat
                    .shouldShowRequestPermissionRationale(activity, permission);
            PermissionReq req = new PermissionReq(
                    activity, permission, reqCode, reqReason, rejectedMsg, callback);
            if (shouldShowReqReason) {
                showReqReason(req);
            } else {
                reqPermission(req);
            }
        }
    }

    private static void showReqReason(final PermissionReq req) {
        new AlertDialog.Builder(req.getActivity())
                .setCancelable(false)
                .setMessage(req.getReqReason())
                .setPositiveButton(R.string.text_ok, (dialog, which) -> reqPermission(req))
                .show();
    }

    private static void reqPermission(PermissionReq req) {
        sPermissionReqList.add(req);
        ActivityCompat.requestPermissions(req.getActivity(), new String[]{req.getPermission()}, req.getReqCode());
    }

    private static void showRejectedMsg(final PermissionReq req) {
        new AlertDialog.Builder(req.getActivity())
                .setCancelable(false)
                .setMessage(req.getRejectedMsg())
                .setPositiveButton(R.string.text_ok, (dialog, which) -> {
                    req.getCallback().onResult(false);
                    sPermissionReqList.remove(req);
                })
                .setNegativeButton(R.string.text_change,
                        (dialog, which) -> openAppDetailSetting(req))
                .show();
    }

    private static void openAppDetailSetting(PermissionReq req) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts(PACKAGE, req.getActivity().getPackageName(), null);
        intent.setData(uri);
        req.getActivity().startActivityForResult(intent, req.getReqCode());
    }
    
    public static void onRequestPermissionResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        PermissionReq targetReq = null;
        for (PermissionReq req : sPermissionReqList) {
            if (req.getActivity().equals(activity) && req.getReqCode() == requestCode && req.getPermission().equals(permissions[0])) {
                targetReq = req;
                break;
            }
        }
        if (targetReq != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                targetReq.getCallback().onResult(true);
                sPermissionReqList.remove(targetReq);
            } else {
                if (TextUtils.isEmpty(targetReq.getRejectedMsg())) {
                    targetReq.getCallback().onResult(false);
                    sPermissionReqList.remove(targetReq);
                } else {
                    showRejectedMsg(targetReq);
                }
            }
        }
    }

    public static void onActivityResult(Activity activity,
            int reqCode) {
        PermissionReq targetReq = null;
        for (PermissionReq req : sPermissionReqList) {
            if (req.getActivity().equals(activity) && req.getReqCode() == reqCode) {
                targetReq = req;
                break;
            }
        }
        if (targetReq != null) {
            if (hasPermission(targetReq.getActivity(), targetReq.getPermission())) {
                targetReq.getCallback().onResult(true);
            } else {
                targetReq.getCallback().onResult(false);
            }
            sPermissionReqList.remove(targetReq);
        }
    }

    private static class PermissionReq {
        private final Activity mActivity;
        private final String mPermission;
        private final int mReqCode;
        private final CharSequence mReqReason;
        private final CharSequence mRejectedMsg;
        private final ReqPermissionCallback mCallback;

        PermissionReq(Activity activity, String permission, int reqCode, CharSequence reqReason, CharSequence rejectedMsg,
                ReqPermissionCallback callback) {
            mActivity = activity;
            mPermission = permission;
            mReqCode = reqCode;
            mReqReason = reqReason;
            mRejectedMsg = rejectedMsg;
            mCallback = callback;
        }

        public Activity getActivity() {
            return mActivity;
        }

        public String getPermission() {
            return mPermission;
        }

        public int getReqCode() {
            return mReqCode;
        }

        public CharSequence getReqReason() {
            return mReqReason;
        }

        public CharSequence getRejectedMsg() {
            return mRejectedMsg;
        }

        public ReqPermissionCallback getCallback() {
            return mCallback;
        }
    }
}
