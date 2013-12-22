/*
 * Copyright (c) 2011, 2012 Roberto Tyley
 *
 * This file is part of 'Agit' - an Android Git client.
 *
 * Agit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Agit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/ .
 */

package com.madgag.agit;


import static android.os.Build.VERSION_CODES.HONEYCOMB;
import static android.text.Html.fromHtml;
import static com.madgag.agit.GitIntents.GITDIR;
import static com.madgag.agit.GitIntents.PATH;
import static com.madgag.agit.GitIntents.UNTIL_REVS;
import static com.madgag.agit.GitIntents.gitDirFrom;
import static com.madgag.android.HtmlStyleUtil.boldCode;
import static com.madgag.android.IntentUtil.isIntentAvailable;
import static org.apache.commons.io.FileUtils.copyInputStreamToFile;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;

import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.RawParseUtils;

public class BlobViewFragment extends com.madgag.agit.WebViewFragment implements LoaderManager
        .LoaderCallbacks<BlobView> {

    private static final String TAG = "BlobViewFragment";

    public static BlobViewFragment newInstance(File gitdir, String revision, String path) {
        BlobViewFragment f = new BlobViewFragment();

        Bundle args = new Bundle();
        args.putString(GITDIR, gitdir.getAbsolutePath());
        args.putString(UNTIL_REVS, revision);
        args.putString(PATH, path);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<BlobView> onCreateLoader(int id, Bundle b) {
        return new AsyncLoader<BlobView>(getActivity()) {
            public BlobView loadInBackground() {
                Bundle args = getArguments();
                try {
                    Repository repo = FileRepositoryBuilder.create(gitDirFrom(args));
                    ObjectId revision = repo.resolve(args.getString(UNTIL_REVS));
                    RevWalk revWalk = new RevWalk(repo);
                    RevCommit commit = revWalk.parseCommit(revision);
                    TreeWalk treeWalk = TreeWalk.forPath(repo, args.getString(PATH), commit.getTree());
                    ObjectId blobId = treeWalk.getObjectId(0);

                    ObjectLoader objectLoader = revWalk.getObjectReader().open(blobId, Constants.OBJ_BLOB);
                    ObjectStream binaryTestStream = objectLoader.openStream();
                    boolean blobIsBinary = RawText.isBinary(binaryTestStream);
                    binaryTestStream.close();
                    Log.d(TAG, "blobIsBinary="+blobIsBinary);
                    return blobIsBinary?new BinaryBlobView(objectLoader, treeWalk.getNameString()):new TextBlobView(objectLoader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


        };
    }

    @Override
    public void onLoadFinished(Loader<BlobView> stringLoader, BlobView r) {
        r.displayBlob();
    }

    @Override
    public void onLoaderReset(Loader<BlobView> stringLoader) {
    }

    private class TextBlobView implements BlobView {

        private static final String TAG = "BlobViewFragment";

        private final String blobHTML;

        TextBlobView(ObjectLoader objectLoader) throws IOException {
            byte[] cachedBytes = objectLoader.getCachedBytes();
            Log.d(TAG, "Got " + cachedBytes.length + " of data");

            String decode = RawParseUtils.decode(cachedBytes);

            blobHTML = dressFileContentForWebView(decode);
        }

        private String dressFileContentForWebView(String decode) {
            GoogleCodePrettify googleCodePrettify = new GoogleCodePrettify();
            String boom = TextUtils.htmlEncode(decode).replace("\n", "<br>");
            String contentString = "";
            contentString += "<html><head>";
            for (String css : googleCodePrettify.getCssFiles()) {
                contentString += "<link href='file:///android_asset/" + css + "' rel='stylesheet' type='text/css'/>";
            }
            for (String js : googleCodePrettify.getJsFiles()) {
                contentString += "<script src='file:///android_asset/" + js + "' type='text/javascript'></script> ";
            }
            // contentString += handler.getFileScriptFiles();
            contentString += "</head><body onload='prettyPrint()'><pre class='prettyprint'>";
            //String sourceString = new String(array);
            contentString += boom;
            contentString += "</pre></body></html>";
            return contentString;
        }

        public void displayBlob() {
            WebView webView = getWebView();
            WebSettings settings = webView.getSettings();
            settings.setUseWideViewPort(true);
            settings.setJavaScriptEnabled(true);
            // settings.setLoadWithOverviewMode(true);

            settings.setBuiltInZoomControls(true);

            if (Build.VERSION.SDK_INT >= HONEYCOMB) {
                // see also http://stackoverflow.com/q/5125851/438886
                settings.setDisplayZoomControls(false);
            }
            webView.loadDataWithBaseURL("file:///android_asset", blobHTML, "text/html", "UTF-8", null);
        }
    }

    private class BinaryBlobView implements BlobView {
        private final File tempFile;
        private final String mimeType;
        private final String nameString;

        BinaryBlobView(ObjectLoader objectLoader, String nameString) throws IOException {
            this.nameString = nameString;
            ObjectStream stream = objectLoader.openStream();
            tempFile= new File(getActivity().getExternalCacheDir(), nameString);
            copyInputStreamToFile(stream, tempFile);
            mimeType=URLConnection.getFileNameMap().getContentTypeFor(nameString);
            Log.d(TAG, "mimeType="+mimeType+" tempFile="+tempFile);
        }

        public void displayBlob() {
            Uri data = Uri.parse("file://" + tempFile.getAbsolutePath());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(data, mimeType);
            if (isIntentAvailable(getActivity(), intent)) {
                startActivity(intent);
            } else {
                Spanned messageHtml = fromHtml(getString(R.string.no_viewer_available_for_file, boldCode(nameString)));
                Toast.makeText(getActivity(), messageHtml, Toast.LENGTH_LONG).show();
            }
            getActivity().finish();
        }
    }
}

interface BlobView {
    void displayBlob();
}