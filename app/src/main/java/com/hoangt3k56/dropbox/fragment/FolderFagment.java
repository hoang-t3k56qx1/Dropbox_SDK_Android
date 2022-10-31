package com.hoangt3k56.dropbox.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DeleteResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.RelocationResult;
import com.dropbox.core.v2.sharing.PathLinkMetadata;
import com.hoangt3k56.dropbox.listener.ListeberListMetadata;
import com.hoangt3k56.dropbox.listener.ListenerBoolean;
import com.hoangt3k56.dropbox.listener.ListenerMetadata;
import com.hoangt3k56.dropbox.R;
import com.hoangt3k56.dropbox.adapter.FileAdapter;
import com.hoangt3k56.dropbox.listener.ListenerString;
import com.hoangt3k56.dropbox.model.DropBoxAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class FolderFagment extends Fragment {

    RecyclerView recyclerView;
    FileAdapter fileAdapter;
    HomeFragment homeFragment;
    RelativeLayout relativeLayout;

    String token, mpath;
    String type_file;

    public static String copy_move_path ="";
    public static int paste = 0;

    DropBoxAPI Api;

    public FolderFagment(String token, String path){
        this.token = token;
        this.mpath = path;
    }

    public void setMpath(String mpath) {
        this.mpath = mpath;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        relativeLayout = view.findViewById(R.id.loadingViewListFolder);
        relativeLayout.setVisibility(View.VISIBLE);
        recyclerView=view.findViewById(R.id.rcView_home_fragment);
        recyclerView.setHasFixedSize(true);
        homeFragment = new HomeFragment();
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        Api = new DropBoxAPI(compositeDisposable, token);

        fileAdapter=new FileAdapter(new ListenerMetadata() {
            @Override
            public void listener(Metadata metadata, int i) {
                if (i == 0) {
                    if (metadata instanceof FolderMetadata) {
                        Log.d("hoangdev",  "den path:   " + metadata.getPathLower());
                        replaceFragment(new FolderFagment(token, metadata.getPathLower()));
                        HomeFragment.mpath = metadata.getPathLower();
                    } else if (metadata instanceof FileMetadata) {
                        String file_name = metadata.getName();
                        type_file = typeFile(file_name);
                        Api.showMetadata(metadata, new ListenerString() {
                            @Override
                            public void listenerString(String path) {
                                if (path != null && !type_file.equals("error")) {
                                    replaceFragment(new ViewFileFragment(path, type_file));
                                } else {
                                    Toast.makeText(getContext(), "File khong dc ho tro!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                } else if (i == 1) {
                    showMenu(metadata);
                }
            }
        });
        recyclerView.setAdapter(fileAdapter);
        loadData();

    }

    public void loadData() {
        relativeLayout.setVisibility(View.VISIBLE);
        Api.loadMatadata(mpath, new ListeberListMetadata() {
            @Override
            public void Listener(List<Metadata> metadataList) {
                if (metadataList != null) {
                    fileAdapter.setMetadataList(metadataList);
                } else {
                    Toast.makeText(getContext(), "No data!", Toast.LENGTH_SHORT).show();
                }
                relativeLayout.setVisibility(View.GONE);
            }
        });
    }

    private String typeFile(String file_name) {
        String type_file = "error";
        if (file_name.contains(".jpg") || file_name.contains(".jpge") || !file_name.contains(".")) {
            type_file = "img";
        } else if (file_name.contains(".mp4")) {
            type_file = "mp4";
        }
        return type_file;
    }


    private void showMenu(Metadata metadata) {
        PopupMenu popupMenu = new PopupMenu(getContext(), getView().findViewById(R.id.item_file));
        popupMenu.getMenuInflater().inflate(R.menu.onclick_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.properties:
                        properties(metadata);
                        break;
                    case R.id.delete:
                        delete(metadata);
                        break;
                    case R.id.move:
                        FolderFagment.paste = 1;
                        FolderFagment.copy_move_path = metadata.getPathLower();
                        Log.d("hoangdev", copy_move_path);
                        break;
                    case R.id.copy:
                        FolderFagment.paste = 2;
                        FolderFagment.copy_move_path = metadata.getPathLower();
                        Log.d("hoangdev", copy_move_path);
                        break;
                    case R.id.paste:
                        paste(metadata);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void paste(Metadata metadata) {
        if (paste == 0) {
            Log.d("hoangdev", "" + paste);
        } else if (paste == 1){
            // move Folder/file
            Api.move(copy_move_path, metadata.getPathLower(), new ListenerBoolean() {
                @Override
                public void listener(Boolean isBoolean) {
                    if (isBoolean) {
                        Toast.makeText(getContext(), "Move success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Error move", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            FolderFagment.paste = 0;
            replaceFragment(new FolderFagment(token, metadata.getPathLower()));
        }  else if (paste == 2){
            // copy Folder/file
           Api.copy(copy_move_path, metadata.getPathLower(), new ListenerBoolean() {
               @Override
               public void listener(Boolean isBoolean) {
                   if (isBoolean) {
                       Toast.makeText(getContext(), "Copy success", Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(getContext(), "Error copy", Toast.LENGTH_SHORT).show();
                   }
               }
           });
            FolderFagment.paste = 0;
            replaceFragment(new FolderFagment(token, metadata.getPathLower()));
        }
    }



    private void delete(Metadata metadata)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Delete");
        dialog.setMessage("Bạn chắc chắn muốn xóa "+ metadata.getName() + " ?");
        dialog.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Api.deleteMetadata(metadata, new ListenerBoolean() {
                    @Override
                    public void listener(Boolean isBoolean) {
                        if (isBoolean) {
                            Toast.makeText(getContext(), "Delete success", Toast.LENGTH_SHORT).show();
                            replaceFragment(new FolderFagment(token, HomeFragment.mpath));
                        } else {
                            Toast.makeText(getContext(), "Delete error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        dialog.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.show();

    }

    private void properties(Metadata metadata) {
        String message = "";
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Properties");

        if (metadata instanceof FileMetadata) {
            FileMetadata  fileMetadata = (FileMetadata) metadata;
            message = "Tên:  " + fileMetadata.getName() +"\n"
                    + "Path:  " + fileMetadata.getPathLower() +"\n"
                    + "Size:  " + fileMetadata.getSize() +" B\n"
                    + "Ngày tạo:  " + fileMetadata.getServerModified() +"\n"
                    + "Ngày edit:  " + fileMetadata.getClientModified();
        } else {
            FolderMetadata folderMetadata = (FolderMetadata) metadata;
            message = "Tên:  " + folderMetadata.getName() +"\n"
                    + "Path:  " + folderMetadata.getPathLower();
        }

        dialog.setMessage(message);
        dialog.show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_home, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}