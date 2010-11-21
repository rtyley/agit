package com.madgag.agit;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.emptyList;
import static org.eclipse.jgit.lib.Constants.encode;
import static org.eclipse.jgit.lib.Constants.encodeASCII;
import static org.eclipse.jgit.lib.FileMode.GITLINK;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.JGitText;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.MyersDiff;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextIgnoreAllWhitespace;
import org.eclipse.jgit.diff.RawTextIgnoreLeadingWhitespace;
import org.eclipse.jgit.diff.RawTextIgnoreTrailingWhitespace;
import org.eclipse.jgit.diff.RawTextIgnoreWhitespaceChange;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AbbreviatedObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.CoreConfig;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.patch.FileHeader.PatchType;
import org.eclipse.jgit.util.QuotedString;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import android.util.Log;

import com.google.common.collect.Lists;

/**
 * Format an {@link EditList} as a Git style unified patch script.
 */
public class LineContextDiffer {

	public static class Hunk {
		public final String before, after;

		public Hunk(String before, String after) {
			this.before=before;this.after=after;
		}

	}

	private static final byte[] noNewLine = encodeASCII("\\ No newline at end of file\n");

	private int context;

	private int abbreviationLength;

	private RawText.Factory rawTextFactory = RawText.FACTORY;

	private int bigFileThreshold = 50 * 1024 * 1024;

	private final Repository repository;

	/**
	 * Create a new formatter with a default level of context.
	 */
	public LineContextDiffer(Repository repository) {
		this.repository = repository;
		CoreConfig cfg = repository.getConfig().get(CoreConfig.KEY);
		bigFileThreshold = cfg.getStreamFileThreshold();
		setContext(3);
		setAbbreviationLength(7);
	}

	/**
	 * Change the number of lines of context to display.
	 * 
	 * @param lineCount
	 *            number of lines of context to see before the first
	 *            modification and after the last modification within a hunk of
	 *            the modified file.
	 */
	public void setContext(final int lineCount) {
		if (lineCount < 0)
			throw new IllegalArgumentException(
					JGitText.get().contextMustBeNonNegative);
		context = lineCount;
	}

	/**
	 * Change the number of digits to show in an ObjectId.
	 * 
	 * @param count
	 *            number of digits to show in an ObjectId.
	 */
	public void setAbbreviationLength(final int count) {
		if (count < 0)
			throw new IllegalArgumentException(
					JGitText.get().abbreviationLengthMustBeNonNegative);
		abbreviationLength = count;
	}

	/**
	 * Set the helper that constructs difference output.
	 * 
	 * @param type
	 *            the factory to create different output. Different types of
	 *            factories can produce different whitespace behavior, for
	 *            example.
	 * @see RawText#FACTORY
	 * @see RawTextIgnoreAllWhitespace#FACTORY
	 * @see RawTextIgnoreLeadingWhitespace#FACTORY
	 * @see RawTextIgnoreTrailingWhitespace#FACTORY
	 * @see RawTextIgnoreWhitespaceChange#FACTORY
	 */
	public void setRawTextFactory(RawText.Factory type) {
		rawTextFactory = type;
	}

	/**
	 * Set the maximum file size that should be considered for diff output.
	 * <p>
	 * Text files that are larger than this size will not have a difference
	 * generated during output.
	 * 
	 * @param bigFileThreshold
	 *            the limit, in bytes.
	 */
	public void setBigFileThreshold(int bigFileThreshold) {
		this.bigFileThreshold = bigFileThreshold;
	}
	
	/**
	 * Format a patch script from a list of difference entries.
	 * 
	 * @param entries
	 *            entries describing the affected files.
	 * @throws IOException
	 *             a file's content cannot be read, or the output stream cannot
	 *             be written to.
	 */
	public void format(List<? extends DiffEntry> entries) throws IOException {
		for (DiffEntry ent : entries)
			format(ent);
	}

	/**
	 * Format a patch script for one file entry.
	 * 
	 * @param ent
	 *            the entry to be formatted.
	 * @throws IOException
	 *             a file's content cannot be read, or the output stream cannot
	 *             be written to.
	 */
	public List<Hunk> format(DiffEntry ent) throws IOException {
		//writeDiffHeader(out, ent);

		if (ent.getOldMode() == GITLINK || ent.getNewMode() == GITLINK) {
			// writeGitLinkDiffText(out, ent);
			return emptyList();
		} else {
			if (repository == null)
				throw new IllegalStateException(
						JGitText.get().repositoryIsRequired);

			ObjectReader reader = repository.newObjectReader();
			byte[] aRaw, bRaw;
			try {
				aRaw = open(reader, ent.getOldMode(), ent.getOldId());
				bRaw = open(reader, ent.getNewMode(), ent.getNewId());
			} finally {
				reader.release();
			}

			if (RawText.isBinary(aRaw) || RawText.isBinary(bRaw)) {
				//out.write(encodeASCII("Binary files differ\n"));
				return emptyList();
			} else {
				RawText a = rawTextFactory.create(aRaw);
				RawText b = rawTextFactory.create(bRaw);
				return formatEdits(a, b, new MyersDiff(a, b).getEdits());
			}
		}
	}

	private void writeGitLinkDiffText(OutputStream o, DiffEntry ent)
			throws IOException {
		if (ent.getOldMode() == GITLINK) {
			o.write(encodeASCII("-Subproject commit " + ent.getOldId().name()
					+ "\n"));
		}
		if (ent.getNewMode() == GITLINK) {
			o.write(encodeASCII("+Subproject commit " + ent.getNewId().name()
					+ "\n"));
		}
	}

	private void writeDiffHeader(OutputStream o, DiffEntry ent)
			throws IOException {
		String oldName = quotePath("a/" + ent.getOldPath());
		String newName = quotePath("b/" + ent.getNewPath());
		o.write(encode("diff --git " + oldName + " " + newName + "\n"));

		switch (ent.getChangeType()) {
		case ADD:
			o.write(encodeASCII("new file mode "));
			ent.getNewMode().copyTo(o);
			o.write('\n');
			break;

		case DELETE:
			o.write(encodeASCII("deleted file mode "));
			ent.getOldMode().copyTo(o);
			o.write('\n');
			break;

		case RENAME:
			o.write(encodeASCII("similarity index " + ent.getScore() + "%"));
			o.write('\n');

			o.write(encode("rename from " + quotePath(ent.getOldPath())));
			o.write('\n');

			o.write(encode("rename to " + quotePath(ent.getNewPath())));
			o.write('\n');
			break;

		case COPY:
			o.write(encodeASCII("similarity index " + ent.getScore() + "%"));
			o.write('\n');

			o.write(encode("copy from " + quotePath(ent.getOldPath())));
			o.write('\n');

			o.write(encode("copy to " + quotePath(ent.getNewPath())));
			o.write('\n');

			if (!ent.getOldMode().equals(ent.getNewMode())) {
				o.write(encodeASCII("new file mode "));
				ent.getNewMode().copyTo(o);
				o.write('\n');
			}
			break;
		case MODIFY:
			int score = ent.getScore();
			if (0 < score && score <= 100) {
				o.write(encodeASCII("dissimilarity index " + (100 - score)
						+ "%"));
				o.write('\n');
			}
			break;
		}

		switch (ent.getChangeType()) {
		case RENAME:
		case MODIFY:
			if (!ent.getOldMode().equals(ent.getNewMode())) {
				o.write(encodeASCII("old mode "));
				ent.getOldMode().copyTo(o);
				o.write('\n');

				o.write(encodeASCII("new mode "));
				ent.getNewMode().copyTo(o);
				o.write('\n');
			}
		}

		o.write(encodeASCII("index " //
				+ format(ent.getOldId()) //
				+ ".." //
				+ format(ent.getNewId())));
		if (ent.getOldMode().equals(ent.getNewMode())) {
			o.write(' ');
			ent.getNewMode().copyTo(o);
		}
		o.write('\n');
		o.write(encode("--- " + oldName + '\n'));
		o.write(encode("+++ " + newName + '\n'));
	}

	private String format(AbbreviatedObjectId id) {
		if (id.isComplete() && repository != null) {
			ObjectReader reader = repository.newObjectReader();
			try {
				id = reader.abbreviate(id.toObjectId(), abbreviationLength);
			} catch (IOException cannotAbbreviate) {
				// Ignore this. We'll report the full identity.
			} finally {
				reader.release();
			}
		}
		return id.name();
	}

	private static String quotePath(String name) {
		String q = QuotedString.GIT_PATH.quote(name);
		return ('"' + name + '"').equals(q) ? name : q;
	}

	private byte[] open(ObjectReader reader, FileMode mode,
			AbbreviatedObjectId id) throws IOException {
		if (mode == FileMode.MISSING)
			return new byte[] {};

		if (mode.getObjectType() != Constants.OBJ_BLOB)
			return new byte[] {};

		if (!id.isComplete()) {
			Collection<ObjectId> ids = reader.resolve(id);
			if (ids.size() == 1)
				id = AbbreviatedObjectId.fromObjectId(ids.iterator().next());
			else if (ids.size() == 0)
				throw new MissingObjectException(id, Constants.OBJ_BLOB);
			else
				throw new AmbiguousObjectException(id, ids);
		}

		ObjectLoader ldr = reader.open(id.toObjectId());
		return ldr.getCachedBytes(bigFileThreshold);
	}

	public List<Hunk> formatEdits(final RawText a, final RawText b,	final EditList edits) throws IOException {
		List<Hunk> hunks=newArrayList();
		for (int curIdx = 0; curIdx < edits.size();) {
			Edit curEdit = edits.get(curIdx);
			final int endIdx = findCombinedEnd(edits, curIdx);

			Log.i("BUCK", "Will do edits "+curIdx+" - "+endIdx);
			final Edit endEdit = edits.get(endIdx);

			int aCur = max(0, curEdit.getBeginA() - context);
			int bCur = max(0, curEdit.getBeginB() - context);
			final int aEnd = min(a.size(), endEdit.getEndA() + context);
			final int bEnd = min(b.size(), endEdit.getEndB() + context);
			String before = extractHunk(a, aCur, aEnd), after = extractHunk(b, bCur, bEnd);
			hunks.add(new Hunk(before,after));
			curIdx=endIdx+1;
		}
		return hunks;
	}

	private String extractHunk(RawText rawText, int startLine, int endLine) {
		try {
			ByteArrayOutputStream bas = new ByteArrayOutputStream();
			for (int line = startLine; line < endLine; ++line) {
				rawText.writeLine(bas, line);
			}
			return new String(bas.toByteArray(), "utf-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Creates a {@link FileHeader} representing the given {@link DiffEntry}
	 * <p>
	 * This method does not use the OutputStream associated with this
	 * DiffFormatter instance. It is therefore safe to instantiate this
	 * DiffFormatter instance with a {@link DisabledOutputStream} if this method
	 * is the only one that will be used.
	 * 
	 * @param ent
	 *            the DiffEntry to create the FileHeader for
	 * @return a FileHeader representing the DiffEntry. The FileHeader's buffer
	 *         will contain only the header of the diff output. It will also
	 *         contain one {@link HunkHeader}.
	 * @throws IOException
	 *             the stream threw an exception while writing to it, or one of
	 *             the blobs referenced by the DiffEntry could not be read.
	 * @throws CorruptObjectException
	 *             one of the blobs referenced by the DiffEntry is corrupt.
	 * @throws MissingObjectException
	 *             one of the blobs referenced by the DiffEntry is missing.
	 */
	public FileHeader createFileHeader(DiffEntry ent) throws IOException,
			CorruptObjectException, MissingObjectException {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		final EditList editList;
		final FileHeader.PatchType type;

		writeDiffHeader(buf, ent);

		if (ent.getOldMode() == GITLINK || ent.getNewMode() == GITLINK) {
			writeGitLinkDiffText(buf, ent);
			editList = new EditList();
			type = PatchType.UNIFIED;
		} else {
			if (repository == null)
				throw new IllegalStateException(
						JGitText.get().repositoryIsRequired);
			ObjectReader reader = repository.newObjectReader();
			byte[] aRaw, bRaw;
			try {
				aRaw = open(reader, ent.getOldMode(), ent.getOldId());
				bRaw = open(reader, ent.getNewMode(), ent.getNewId());
			} finally {
				reader.release();
			}

			if (RawText.isBinary(aRaw) || RawText.isBinary(bRaw)) {
				buf.write(encodeASCII("Binary files differ\n"));
				editList = new EditList();
				type = PatchType.BINARY;
			} else {
				RawText a = rawTextFactory.create(aRaw);
				RawText b = rawTextFactory.create(bRaw);
				editList = new MyersDiff(a, b).getEdits();
				type = PatchType.UNIFIED;
			}
		}

		return new FileHeader(buf.toByteArray(), editList, type);
	}

	private int findCombinedEnd(final List<Edit> edits, final int i) {
		int end = i + 1;
		while (end < edits.size()
				&& (combineA(edits, end) || combineB(edits, end)))
			end++;
		return end - 1;
	}

	private boolean combineA(final List<Edit> e, final int i) {
		return e.get(i).getBeginA() - e.get(i - 1).getEndA() <= 2 * context;
	}

	private boolean combineB(final List<Edit> e, final int i) {
		return e.get(i).getBeginB() - e.get(i - 1).getEndB() <= 2 * context;
	}

	private static boolean end(final Edit edit, final int a, final int b) {
		return edit.getEndA() <= a && edit.getEndB() <= b;
	}

}
