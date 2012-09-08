package com.bourke.glimmr.tasks;


import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;

import android.net.Uri;

import android.os.AsyncTask;

import com.bourke.glimmr.common.Constants;
import com.bourke.glimmr.activities.BaseActivity;
import com.bourke.glimmr.common.FlickrHelper;
import com.bourke.glimmr.event.Events.IRequestTokenReadyListener;
import com.bourke.glimmr.R;

import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuthToken;

import java.net.URL;
import android.app.Activity;

/**
 *
 */
public class GetRequestToken extends AsyncTask<Void, Integer, String> {

    private static final String TAG = "Glimmr/GetRequestToken";

    private static final Uri OAUTH_CALLBACK_URI = Uri.parse(
            Constants.CALLBACK_SCHEME + "://oauth");

    private ProgressDialog mProgressDialog;
    private Activity mActivity;
    private IRequestTokenReadyListener mListener;

    public GetRequestToken(IRequestTokenReadyListener listener, Activity a) {
        super();
        mListener = listener;
        mActivity = a;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mActivity, "",
                mActivity.getString(R.string.just_a_moment));
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                GetRequestToken.this.cancel(true);
            }
        });
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();

            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
                    OAUTH_CALLBACK_URI.toString());
            saveRequestToken(null, null, null,
                    oauthToken.getOauthTokenSecret());

            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
                    Permission.WRITE, oauthToken);

            return oauthUrl.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveRequestToken(String userName, String userId,
            String token, String tokenSecret) {
        SharedPreferences sp = mActivity.getSharedPreferences(
                Constants.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.KEY_OAUTH_TOKEN, token);
        editor.putString(Constants.KEY_TOKEN_SECRET, tokenSecret);
        editor.putString(Constants.KEY_USER_NAME, userName);
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.commit();
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mListener.onRequestTokenReady(result);
    }
}
