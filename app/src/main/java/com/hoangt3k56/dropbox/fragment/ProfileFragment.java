package com.hoangt3k56.dropbox.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.account.PhotoSourceArg;
import com.dropbox.core.v2.account.SetProfilePhotoResult;
import com.dropbox.core.v2.users.FullAccount;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.hoangt3k56.dropbox.listener.Listener;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.listener.ListenerBoolean;
import com.hoangt3k56.dropbox.listener.ListenerFullAccount;
import com.hoangt3k56.dropbox.model.DropBoxAPI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class ProfileFragment extends Fragment {

    ImageView imageView;
    TextView tvCancel, tvSave, tvName;
    RelativeLayout relativeLayout;

    Listener listener;
    String token, base64;

    private int CAMERA_CODE =12345;
    private static final int CAMERA_PIC_REQUEST = 1111;

    CompositeDisposable compositeDisposable;
    DropBoxAPI Api;

    public ProfileFragment(String token, Listener listener) {
        this.token=token;
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile,container,false);
        initView(view);
        compositeDisposable = new CompositeDisposable();
        Api = new DropBoxAPI(compositeDisposable, token);
        getFullAccount();
        return view;
    }

    private void initView(View view) {
        imageView       = view.findViewById(R.id.imgAvatar);
        tvCancel        = view.findViewById(R.id.tvCancel);
        tvSave          = view.findViewById(R.id.tvSave);
        tvName          = view.findViewById(R.id.tvName);
        relativeLayout  =    view.findViewById(R.id.loadingView);
        base64          = "error";

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAvatar();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rqPermissions();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.listener();
            }
        });
    }

    private void getFullAccount() {
        relativeLayout.setVisibility(View.VISIBLE);
        Api.getFullAccount(new ListenerFullAccount() {
            @Override
            public void listener(FullAccount fullAccount) {
                if (fullAccount != null) {
                    tvName.setText(fullAccount.getName().getDisplayName());
                    Glide.with(imageView.getContext())
                            .load(fullAccount.getProfilePhotoUrl())
                            .apply(new RequestOptions().placeholder(R.drawable.loading)).error(R.drawable.img_fail)
                            .into(imageView);
                } else {
                    Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                }

                relativeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void saveAvatar() {
        relativeLayout.setVisibility(View.VISIBLE);
        Api.saveAvata(base64, new ListenerBoolean() {
            @Override
            public void listener(Boolean isBoolean) {
                if (!base64.equals("error")) {
                    if (isBoolean) {
                        base64 = "error";
                        getFullAccount();
                        Toast.makeText(getContext(), "save Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                    }
                }
                relativeLayout.setVisibility(View.GONE);
            }
        });
    }


    private class saveImg extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            DbxClientV2 client = new DbxClientV2(config, token);
            try {
                SetProfilePhotoResult account = client.account().setProfilePhoto(PhotoSourceArg.base64Data(base64));
                return true;
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "ket noi tk khong thanh cong trong Frofile\n" + e.toString());
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                Log.d("hoangdev", "save img");
            } else {
                Log.e("hoangdev", "error save img");
            }
            relativeLayout.setVisibility(View.GONE);
        }
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

    public void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST && data.getExtras().get("data") != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                 Glide.with(this).load(bitmap).into(imageView);
                 base64 = encodeImage(bitmap);
                Log.d("hoangdev", "uri take photo:  " + bitmap);
            }

        }
    }

    private String encodeImage(Bitmap bm) {
        if(bm==null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

}