package com.hoangt3k56.dropbox.fragment;


import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoangt3k56.dropbox.listener.ListenerBoolean;
import com.hoangt3k56.dropbox.listener.ListenerString;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.model.DropBoxAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class HomeFragment extends Fragment {

    String token;
    Toolbar toolbar;

    Uri uri_up_load, mUri;
    FolderFagment folderFagment;
    FolderNewFragment folderNewFragment;

    private int CAMERA_REQUEST =1221;
    private int SELESECT_FILE_REQUEST = 1111;
    public static String mpath="";

    DropBoxAPI Api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_home, container, false);
        token = getArguments().getString("TOKEN");
        initToobar(view);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        Api = new DropBoxAPI(compositeDisposable, token);
        folderFagment = new FolderFagment(token, mpath);
        replaceFragment(folderFagment);
        return view;
    }

    private void initToobar(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.right_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case  R.id.newFolder:
                        newFodel();
                        break;
                    case R.id.refresh:
                        replaceFragment(new FolderFagment(token, mpath));
                        break;
                    case R.id.upload:
                        openFile();
                        break;
                    case R.id.takePhoto:
                        // loi camera
                        takePhoto();
                        break;
                }
                return false;
            }
        });
    }

    private void newFodel() {
        folderNewFragment = new FolderNewFragment(token, mpath, new ListenerBoolean() {
            @Override
            public void listener(Boolean isBoolean) {
                removeFragment(folderNewFragment);
                if (isBoolean) {
                    replaceFragment(new FolderFagment(token, mpath));
                }
            }
        });
        replaceFragment(folderNewFragment);
    }



    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, SELESECT_FILE_REQUEST);

    }

    private void upload() {
        Api.upLoad(getContext(), mpath, uri_up_load, new ListenerBoolean() {
            @Override
            public void listener(Boolean isBoolean) {
                if (isBoolean) {
                    Toast.makeText(getContext(), "UpLoad file success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "UpLoad file error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        replaceFragment(new FolderFagment(token, mpath));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST && data.getExtras().get("data") != null ) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                 uri_up_load = getImageUri(getContext(), bitmap);
                Log.d("hoangdev", "uri take photo:  " + uri_up_load.toString());
                upload();
//                Log.d("hoangdev", "bitmap take photo:  " + bitmap.toString());
            }

            if (requestCode == SELESECT_FILE_REQUEST && data.getData() != null) {
                uri_up_load = data.getData();
                Log.d("","URI = "+ uri_up_load);
                upload();
            }
        }
    }

    public void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,CAMERA_REQUEST);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "image", null);
        return Uri.parse(path);
    }

    private void takePhoto() {
        rqPermissions();
    }

    public void rqPermissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickImage();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("Nếu bạn từ chối quyền, bạn không thể sử dụng dịch vụ này\n\nVui lòng bật quyền tại [Setting]> [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }



    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
