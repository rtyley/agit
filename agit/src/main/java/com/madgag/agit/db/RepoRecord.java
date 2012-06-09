package com.madgag.agit.db;

import com.google.common.base.Function;

import java.io.File;

public class RepoRecord {

    public final static Function<RepoRecord, File> GITDIR_FOR_RECORD = new Function<RepoRecord, File>() {
        public File apply(RepoRecord repoRecord) {
            return repoRecord.gitdir;
        }
    };

    public long id;
    public File gitdir;

    public RepoRecord(long id, File gitdir) {
        this.id = id;
        this.gitdir = gitdir;
    }

    public String toString() {
        return "["+id+","+gitdir+"]";
    }
}
