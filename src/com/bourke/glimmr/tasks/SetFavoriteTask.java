package com.bourke.glimmrpro.tasks;

import android.os.AsyncTask;

import com.bourke.glimmrpro.common.FlickrHelper;
import com.bourke.glimmrpro.event.Events.IFavoriteReadyListener;
import com.bourke.glimmrpro.fragments.base.BaseFragment;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import android.util.Log;

public class SetFavoriteTask extends AsyncTask<OAuth, Void, Exception> {

    private static final String TAG = "Glimmr/SetFavoriteTask";

    private IFavoriteReadyListener mListener;
    private BaseFragment mBaseFragment;
    private Photo mPhoto;

    public SetFavoriteTask(BaseFragment a, IFavoriteReadyListener listener,
            Photo photo) {
        mBaseFragment = a;
        mListener = listener;
        mPhoto = photo;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mBaseFragment.showProgressIcon(true);
    }

    @Override
    protected Exception doInBackground(OAuth... params) {
        OAuth oauth = params[0];
        if (oauth != null) {
            OAuthToken token = oauth.getToken();
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
                    token.getOauthToken(), token.getOauthTokenSecret());
            try {
                if (mPhoto.isFavorite()) {
                    f.getFavoritesInterface().remove(mPhoto.getId());
                } else {
                    f.getFavoritesInterface().add(mPhoto.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e;
            }
        } else {
            Log.e(TAG, "SetFavoriteTask requires authentication");
        }
        return null;
    }

    @Override
    protected void onPostExecute(final Exception result) {
        mListener.onFavoriteComplete(result);
        mBaseFragment.showProgressIcon(false);
    }
}
