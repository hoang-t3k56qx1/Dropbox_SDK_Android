package com.hoangt3k56.dropbox.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.hoangt3k56.dropbox.listener.ListenerBoolean;
import com.hoangt3k56.dropbox.listener.ListenerString;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.model.DropBoxAPI;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class FolderNewFragment extends Fragment {

    ListenerBoolean listener;
    Button btn_tao, btn_huy;
    EditText edt_name_fodel;
    String name, token, mpath;
    DropBoxAPI Api;


    public FolderNewFragment(String token,String path, ListenerBoolean listener){
        this.listener = listener;
        this.token = token;
        this.mpath = path;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_new_folder, container, false);
        initUi(view);
        btn_tao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = edt_name_fodel.getText().toString();
                if(name != null  && !name.isEmpty()) {
                    listener.listener(true);
                    newFolder();
                } else {
                    Toast.makeText(getContext(), "không được bỏ trống", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btn_huy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.listener(true);
            }
        });


        return view;
    }

    private void initUi(View view) {
        btn_huy = (Button) view.findViewById(R.id.btn_cancel_new_folder);
        btn_tao = (Button) view.findViewById(R.id.btn_new_folder);
        edt_name_fodel = (EditText) view.findViewById(R.id.edt_name_folder);

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        Api = new DropBoxAPI(compositeDisposable, token);
    }

    private void newFolder() {
        Api.newFolder(mpath, name, new ListenerBoolean() {
            @Override
            public void listener(Boolean isBoolean) {
                if (isBoolean) {
                    Toast.makeText(getContext(), "NewFolder success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "NewFolder error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
