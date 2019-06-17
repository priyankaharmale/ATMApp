package com.hnweb.atmap.utils;

/**
 * Created by Priyanka on 26/09/2018.
 */
public class NotificationUpdateModel {

    private boolean mState;
    private OnCustomNotificationStateListener mListener;

    public boolean ismState() {
        return mState;
    }

    private NotificationUpdateModel() {}

    private static NotificationUpdateModel mInstance;

    public static NotificationUpdateModel getInstance() {
        if(mInstance == null) {
            mInstance = new NotificationUpdateModel();
        }
        return mInstance;
    }

    public void setmState(boolean mState) {
        this.mState = mState;
    }
    public void changeState(boolean state) {
        if(mListener != null) {
            mState = state;
            notifyStateChange();
        }
    }

    public void setListener(OnCustomNotificationStateListener listener) {
        mListener = listener;
    }

    public boolean getState() {
        return mState;
    }

    private void notifyStateChange() {
        mListener.notificationStateChanged();
    }

    public OnCustomNotificationStateListener getmListener() {
        return mListener;
    }

    public void setmListener(OnCustomNotificationStateListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCustomNotificationStateListener {
        void notificationStateChanged();
    }
}
