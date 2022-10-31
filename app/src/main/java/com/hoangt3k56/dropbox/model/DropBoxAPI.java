package com.hoangt3k56.dropbox.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxHost;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.account.PhotoSourceArg;
import com.dropbox.core.v2.account.SetProfilePhotoResult;
import com.dropbox.core.v2.files.CreateFolderResult;
import com.dropbox.core.v2.files.DeleteResult;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.RelocationResult;
import com.dropbox.core.v2.sharing.PathLinkMetadata;
import com.dropbox.core.v2.users.FullAccount;
import com.hoangt3k56.dropbox.fragment.FolderFagment;
import com.hoangt3k56.dropbox.listener.ListeberListMetadata;
import com.hoangt3k56.dropbox.listener.ListenerBoolean;
import com.hoangt3k56.dropbox.listener.ListenerFullAccount;
import com.hoangt3k56.dropbox.listener.ListenerString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DropBoxAPI {

    DbxClientV2 client;
    CompositeDisposable compositeDisposable;


    public DropBoxAPI(CompositeDisposable compositeDisposable, String token) {
        DbxRequestConfig config     = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        this.client                 = new DbxClientV2(config, token);
        this.compositeDisposable    = compositeDisposable;
    }


    public void getFullAccount(ListenerFullAccount listener) {
        compositeDisposable.add(observableFullAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FullAccount>() {

                    @Override
                    public void onNext(@NonNull FullAccount fullAccount) {
                        listener.listener(fullAccount);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(null);
                        Log.e("hoangdev","Error FullAccount \n" + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<FullAccount> observableFullAccount() {
        return Observable.create((ObservableOnSubscribe<FullAccount>) subscriber ->{
            try {
                FullAccount account = client.users().getCurrentAccount();
                subscriber.onNext(account);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }


    public void saveAvata(String base64, ListenerBoolean listener) {
        compositeDisposable.add(observableSaveAvatar(base64)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        listener.listener(success);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(false);
                        Log.e("hoangdev","Error saveAvata \n" + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<Boolean> observableSaveAvatar(String base64) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber ->{
            try {
                SetProfilePhotoResult setProfilePhoto = client.account().setProfilePhoto(PhotoSourceArg.base64Data(base64));
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }


    public void loadMatadata(String path, ListeberListMetadata listener) {
        compositeDisposable.add(observableListMetadta(path)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<Metadata>>() {


                    @Override
                    public void onNext(@NonNull List<Metadata> metadataList) {
                        listener.Listener(metadataList);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.Listener(null);
                        Log.e("hoangdev","Error loadMetadata \n" + e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<List<Metadata>> observableListMetadta(String path) {
        return Observable.create((ObservableOnSubscribe<List<Metadata>>) subscriber ->{
            List<Metadata> list = new ArrayList<>();
            ListFolderResult result = null;
            try {
                result = client.files().listFolder(path);
                Log.d("hoangdev", "List metadata path: " + path);
                while (true) {
                    for (Metadata metadata : result.getEntries()) {
//                        Log.d("hoangdev", metadata.getPathLower().toString());
                        list.add(metadata);
                    }

                    if (!result.getHasMore()) {
                        break;
                    }
                    result = client.files().listFolderContinue(result.getCursor());
                }
                subscriber.onNext(list);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }

    public void upLoad(Context context, String path, Uri uri, ListenerBoolean listener) {
        compositeDisposable.add(observableUpLoad(context, path, uri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        listener.listener(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(false);
                        Log.e("hoangdev", "loi upload \n" +  e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<Boolean> observableUpLoad(Context context, String path, Uri uri) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber ->{
            try {
                InputStream in = context.getContentResolver().openInputStream(uri);
                String name_file = uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
                FileMetadata metadata = client.files().uploadBuilder(path+"/"+name_file).uploadAndFinish(in);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                subscriber.onError(e);
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }


    public void newFolder(String mpath, String name, ListenerBoolean listener) {
        compositeDisposable.add(observableNewFolder(mpath, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        listener.listener(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(false);
                        Log.e("hoangdev", "loi newFolder \n" +  e);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<Boolean> observableNewFolder(String mpath, String name ) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber ->{
            CreateFolderResult folder = null;
            try {
                String path = mpath+ "/" +name;
                Log.d("hoangdev","path them:  " +path);
                folder = client.files().createFolderV2(path);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }


    public void showMetadata(Metadata metadata, ListenerString listener) {
        compositeDisposable.add(observableShowFileMetadata(metadata)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {

                    @Override
                    public void onNext(@NonNull String path) {
                        listener.listenerString(path);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listenerString(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<String> observableShowFileMetadata(Metadata metadata) {
        return Observable.create((ObservableOnSubscribe<String>) subscriber ->{
            try {
                PathLinkMetadata sharedLinkMetadata = client.sharing().createSharedLink(metadata.getPathLower());
                Log.d("hoangdev", sharedLinkMetadata.getUrl());
                String link =  sharedLinkMetadata.getUrl().replace("?dl=0", "?raw=1");
                subscriber.onNext(link);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                Log.e("hoangdev", "Loi tao url link \n" + e.getRequestId());
                subscriber.onError(e);
            }
        });
    }


    public void deleteMetadata(Metadata metadata, ListenerBoolean listener) {
        compositeDisposable.add(observableDeleteMetadata(metadata)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        listener.listener(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<Boolean> observableDeleteMetadata(Metadata metadata) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber ->{
            try {
                DeleteResult deleteV2 = client.files().deleteV2(metadata.getPathLower());
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
                Log.e("hoangdev", "Loi xoa file "+ e.toString());
            }
        });
    }


    public void copy(String path, String topath, ListenerBoolean listener) {
        compositeDisposable.add(observableCopy(path, topath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        listener.listener(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<Boolean> observableCopy(String path, String topath) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber ->{
            String [] a = path.split("/");
            try {
                Log.d("hoangdev", "copy:  " + path + "  -->  " + topath);
                RelocationResult copyV2 =
                        client.files().copyV2(path, topath + "/" + a[a.length-1]);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }


    public void move(String path, String topath, ListenerBoolean listener) {
        compositeDisposable.add(observableMove(path, topath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(@NonNull Boolean aBoolean) {
                        listener.listener(aBoolean);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        listener.listener(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));

    }

    private Observable<Boolean> observableMove(String path, String topath) {
        return Observable.create((ObservableOnSubscribe<Boolean>) subscriber ->{
            String [] a = path.split("/");
            try {
                Log.d("hoangdev", "move:   " + path + "  -->  " + topath);
                RelocationResult copyV2 =
                        client.files().moveV2(path, topath + "/" + a[a.length-1]);
                subscriber.onNext(true);
                subscriber.onComplete();
            } catch (DbxException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }
}
