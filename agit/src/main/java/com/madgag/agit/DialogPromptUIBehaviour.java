/*
 * Copyright (c) 2011 Roberto Tyley
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.madgag.agit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;
import com.google.inject.Inject;
import com.madgag.android.blockingprompt.PromptUI;
import com.madgag.android.blockingprompt.PromptUIRegistry;
import com.madgag.android.blockingprompt.ResponseInterface;

public class DialogPromptUIBehaviour implements PromptUI {


    private final Activity activity;
    private final PromptUIRegistry promptUIRegistry;

    public static final int YES_NO_DIALOG=1,STRING_ENTRY_DIALOG=2;
    private final String TAG="DPM";
    private ResponseInterface responseInterface;

    @Inject
    public DialogPromptUIBehaviour(Activity activity, PromptUIRegistry promptUIRegistry) {
        this.activity = activity;
        this.promptUIRegistry = promptUIRegistry;
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		switch (id) {
		case YES_NO_DIALOG:
			builder.setMessage("...")
                .setPositiveButton("Yes", sendDialogResponseOf(true))
                .setNegativeButton("No", sendDialogResponseOf(false));
            break;
		case STRING_ENTRY_DIALOG:
			final EditText input = new EditText(activity);
			builder.setView(input);
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
                    responseInterface.setResponse(input.getText().toString());
				}
			});
            break;
		}
        return builder.create();
	}


    public void registerReceiverForServicePromptRequests() {
        Log.d(TAG, "Registering as prompt UI provider with "+ promptUIRegistry);
    	promptUIRegistry.setActivityPromptUI(this);
	}

	public void unregisterRecieverForServicePromptRequests() {
		promptUIRegistry.clearActivityUIProvider(this);
	}

    private DialogInterface.OnClickListener sendDialogResponseOf(final boolean bool) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
                responseInterface.setResponse(bool);
			}
		};
	}

	public void onPrepareDialog(int id, Dialog dialog) {
        AlertDialog alertDialog = (AlertDialog) dialog;
		switch (id) {
		case YES_NO_DIALOG:
        case STRING_ENTRY_DIALOG:
            if (responseInterface!=null) {
                String msg = responseInterface.getOpPrompt().getOpNotification().getEventDetail();
                Log.d(TAG, "Will prompt with: " + msg);
                alertDialog.setMessage(msg);
            }
		default:
		}
	}


	void updateUIToReflectServicePromptRequests() {
		if (responseInterface!=null && responseInterface.getOpPrompt()!=null) {
			Class<?> requiredResponseType = responseInterface.getOpPrompt().getRequiredResponseType();
			if (String.class.equals(requiredResponseType)) {
				activity.showDialog(STRING_ENTRY_DIALOG);
			} else if(Boolean.class.equals(requiredResponseType)) {
				activity.showDialog(YES_NO_DIALOG);
			} else {
	//			hideAllPrompts();
	//			view.requestFocus();
			}
		}
	}

    public void acceptPrompt(ResponseInterface responseInterface) {
        this.responseInterface = responseInterface;
        updateUIToReflectServicePromptRequests();
    }

    public void clearPrompt() {
        // TODO clear any actual prompt that's going on...
        this.responseInterface = null;
    }
}
