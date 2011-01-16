package com.madgag.agit;

import android.app.ListActivity;
import android.os.Bundle;

public class RDTypeListActivity extends ListActivity {
	private static final String TAG = "RDTL";
	private RepositoryContext rc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		rc = new RepositoryContext(this, TAG);
		RepoDomainType<?> rdt = extractRDTFromIntent();
		setListAdapter(new RDTypeListAdapter(this, rdt));
	}
	
	private RepoDomainType<?> extractRDTFromIntent() {
		String rdtName = getIntent().getAction().split("\\.")[1];
		if (rdtName.equals("remote")) {
			return new RDTRemote(rc.repo());
		} else if (rdtName.equals("branch")) {
			return new RDTBranch(rc.repo());
		}
		return new RDTTag(rc.repo());
	}

	@Override
	protected void onResume() {
		super.onResume();
		rc.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		rc.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		rc.onDestroy();
	}
}
