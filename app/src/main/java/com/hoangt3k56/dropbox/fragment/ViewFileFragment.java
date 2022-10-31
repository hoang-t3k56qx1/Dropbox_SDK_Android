package com.hoangt3k56.dropbox.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.hoangt3k56.dropbox.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ViewFileFragment extends Fragment {

    ImageView img;
    VideoView video;
    RelativeLayout layout;
    public String url, typeFile;

    public ViewFileFragment(String url, String typeFile){
        this.url        = url;
        this.typeFile   = typeFile;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_file, container, false);
        initUi(view);
        Uri uri = Uri.parse(url);
        Log.d("hoangdev", url);
        if (typeFile.equals("img")) {
            Glide.with(getContext())
                    .load(uri)
                    .error(R.drawable.img_fail)
                    .into(img);


        } else if (typeFile.equals("mp4")) {
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(video);
            video.setMediaController(mediaController);
            video.setVideoURI(uri);
            video.start();
        }
        return view;
    }

    private void initUi(View view) {
        img = (ImageView) view.findViewById(R.id.img_View);
        video = (VideoView) view.findViewById(R.id.video_view);
    }
}
