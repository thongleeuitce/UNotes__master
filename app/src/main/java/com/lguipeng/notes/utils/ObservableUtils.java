package com.lguipeng.notes.utils;

import android.content.Context;

import com.evernote.edam.type.User;
import com.lguipeng.notes.model.UNote;

import net.tsz.afinal.FinalDb;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by lgp on 2015/9/4.
 */
public class ObservableUtils {

    @Inject public ObservableUtils(){}

    public Observable<List<UNote>> getLocalNotesByType(FinalDb finalDb, int type){
       return create(new GetLocalNotesByTypeFun(finalDb, type));
    }

    public Observable<User> getEverNoteUser(EverNoteUtils everNoteUtils){
       return create(new GetEverNoteUserFun(everNoteUtils));
    }

    public Observable<Boolean> backNotes(Context context, FinalDb finalDb, FileUtils fileUtils){
        return create(new BackupNotesFun(context, finalDb, fileUtils));
    }

    public Observable<EverNoteUtils.SyncResult> sync(EverNoteUtils everNoteUtils, EverNoteUtils.SyncType type){
        return create(new SyncFun(everNoteUtils, type));
    }

    public Observable<Boolean> pushNote(EverNoteUtils everNoteUtils, UNote note){
        return create(new PushNoteFun(everNoteUtils, note));
    }

    private <T> Observable<T> create(Fun<T> fun){
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    T t = fun.call();
                    subscriber.onNext(t);
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        });
    }

    private class SyncFun implements Fun<EverNoteUtils.SyncResult>{

        private EverNoteUtils mEverNoteUtils;
        private EverNoteUtils.SyncType type;

        public SyncFun(EverNoteUtils mEverNoteUtils, EverNoteUtils.SyncType type) {
            this.mEverNoteUtils = mEverNoteUtils;
            this.type = type;
        }

        @Override
        public EverNoteUtils.SyncResult call() throws Exception{
            return mEverNoteUtils.sync(type);
        }
    }

    private class PushNoteFun implements Fun<Boolean>{
        private EverNoteUtils mEverNoteUtils;
        private UNote mUNote;

        public PushNoteFun(EverNoteUtils mEverNoteUtils, UNote uNote) {
            this.mEverNoteUtils = mEverNoteUtils;
            this.mUNote = uNote;
        }

        @Override
        public Boolean call() throws Exception {
            return mEverNoteUtils.pushNote(mUNote);
        }
    }

    private class BackupNotesFun implements Fun<Boolean>{
        private Context mContext;
        private FinalDb mFinalDb;
        private FileUtils mFileUtils;

        public BackupNotesFun(Context mContext, FinalDb mFinalDb, FileUtils mFileUtils) {
            this.mContext = mContext;
            this.mFinalDb = mFinalDb;
            this.mFileUtils = mFileUtils;
        }

        @Override
        public Boolean call()  throws Exception{
            List<UNote> notes = mFinalDb.findAllByWhere(UNote.class, " type = 0");
            return mFileUtils.backupSNotes(mContext, notes);
        }
    }

    private class GetEverNoteUserFun implements Fun<User>{
        private EverNoteUtils mEverNoteUtils;

        public GetEverNoteUserFun(EverNoteUtils mEverNoteUtils) {
            this.mEverNoteUtils = mEverNoteUtils;
        }

        @Override
        public User call() throws Exception{
            return mEverNoteUtils.getUser();
        }
    }

    private class GetLocalNotesByTypeFun implements Fun<List<UNote>>{

        private FinalDb mFinalDb;
        private int type;
        public GetLocalNotesByTypeFun(FinalDb mFinalDb, int type) {
            this.mFinalDb = mFinalDb;
            this.type  = type;
        }

        @Override
        public List<UNote> call() throws Exception {
            return mFinalDb.findAllByWhere(UNote.class, "type = " + type
                    , "lastOprTime", true);
        }
    }

    /**
     * Created by lgp on 2015/9/11.
     */
    public interface Fun<T> {
        T call() throws Exception;
    }
}
