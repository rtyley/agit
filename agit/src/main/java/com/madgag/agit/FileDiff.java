package com.madgag.agit;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;

import com.madgag.agit.LineContextDiffer.Hunk;

public class FileDiff {
	private final LineContextDiffer lineContextDiffer;
	private final DiffEntry diffEntry;
	private List<Hunk> hunks;

	public FileDiff(LineContextDiffer lineContextDiffer,DiffEntry diffEntry) {
		this.lineContextDiffer = lineContextDiffer;
		this.diffEntry = diffEntry;
	}
	
	public List<Hunk> getHunks() {
		if (hunks==null) {
			try {
				hunks = lineContextDiffer.format(diffEntry);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return hunks;
	}

	public DiffEntry getDiffEntry() {
		return diffEntry;
	}
}
