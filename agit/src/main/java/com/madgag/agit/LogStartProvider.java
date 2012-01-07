package com.madgag.agit;

import static com.google.common.collect.Iterables.transform;
import static java.util.Arrays.asList;
import static org.eclipse.jgit.lib.Constants.R_REMOTES;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import javax.annotation.Nullable;
import java.io.IOException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

import android.util.Log;

public class LogStartProvider {

    private static final String TAG = "LSP";
    public static final Function<Ref,ObjectId> OBJECT_IDS_FOR_REFS = new Function<Ref, ObjectId>(){
        public ObjectId apply(Ref ref) {
            return ref.getObjectId();
        }
    };

    @Inject Repository repository;
    @Inject @Named("branch") @Nullable
    Ref branch;

    public Iterable<ObjectId> get() {
        Iterable<Ref> refs = getRefs();
        Log.d(TAG, "Using refs " + refs);
        return transform(refs, OBJECT_IDS_FOR_REFS);
    }

    private Iterable<Ref> getRefs() {
        if (branch != null) {
            return asList(branch);
        }
        
        try {
            return repository.getRefDatabase().getRefs(R_REMOTES).values();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
