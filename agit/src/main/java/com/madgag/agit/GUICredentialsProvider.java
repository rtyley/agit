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

import com.google.inject.Inject;
import com.madgag.agit.guice.RepositoryScoped;
import com.madgag.agit.operations.OpNotification;
import com.madgag.agit.operations.OpPrompt;
import com.madgag.android.blockingprompt.BlockingPromptService;
import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

import static com.madgag.agit.operations.OpNotification.alert;
import static com.madgag.agit.operations.OpPrompt.prompt;
import static com.madgag.agit.operations.OpPrompt.promptYesOrNo;

public class GUICredentialsProvider extends CredentialsProvider {

	@Inject	BlockingPromptService blockingPrompt;

	@Override
	public boolean isInteractive() {
		return true;
	}

	@Override
	public boolean supports(CredentialItem... items) {
		return true;
	}

	@Override
	public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
		for (CredentialItem ci : items) {
			if (ci instanceof CredentialItem.YesNoType) {
				handle((CredentialItem.YesNoType) ci);
			} else if (ci instanceof CredentialItem.CharArrayType) {
				handle((CredentialItem.CharArrayType) ci);
			} else {
				return false;
			}
		}
		return true;
	}

	private void handle(CredentialItem.CharArrayType ci) {
		ci.setValue(blockingPrompt.request(prompt(String.class, alert(ci.getPromptText(), ci.getPromptText()))).toCharArray());
	}

	private void handle(CredentialItem.YesNoType ci) {
		ci.setValue(blockingPrompt.request(promptYesOrNo(alert(ci.getPromptText(), ci.getPromptText()))));
	}
}
