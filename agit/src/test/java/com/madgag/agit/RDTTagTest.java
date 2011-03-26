package com.madgag.agit;

import static com.google.common.collect.Iterables.find;
import static com.madgag.compress.CompressUtil.unzip;
import static java.lang.System.currentTimeMillis;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.madgag.agit.RDTTag.TagSummary;

public class RDTTagTest {
	@Test
	public void shouldNotThrowNPEDueToUnparsedObjectDataEspeciallyForRepoWithJustOneAnnotatedTag() throws Exception {
		RDTTag rdtTag = new RDTTag(unpackRepo("repo-with-just-an-annotated-tag-of-a-commit.zip"));
		List<TagSummary> listOfTagsInRepo = rdtTag.getAll();
		assertThat(listOfTagsInRepo, hasSize(1));
		TagSummary loneTag = listOfTagsInRepo.get(0);
		assertThat(rdtTag.shortDescriptionOf(loneTag).toString(), notNullValue());
	}
	
	@Test
	public void shouldDescribeThingsProperly() throws Exception {
		RDTTag rdtTag = new RDTTag(unpackRepo("small-repo.with-tags.zip"));
		List<TagSummary> tags = rdtTag.getAll();
		
		TagSummary tag = find(tags, tagNamed("annotated-tag-of-2nd-commit"));
		assertThat(rdtTag.shortDescriptionOf(tag).toString(), startsWith("Commit"));
	}

	private Predicate<TagSummary> tagNamed(final String tagName) {
		return new Predicate<TagSummary>() {
			public boolean apply(TagSummary tag) { return tag.getName().equals(tagName); }
		};
	}	
	
	private Repository unpackRepo(String fileName) throws IOException, ArchiveException {
		File repoParentFolder = new File(FileUtils.getTempDirectory(),"unpacked-"+fileName+"-"+currentTimeMillis());
		InputStream rawZipFileInputStream = getClass().getResourceAsStream("/"+fileName);
		assertThat(rawZipFileInputStream, notNullValue());
		return unzipRepoFromStreamToFolder(rawZipFileInputStream, repoParentFolder);
	}

	private Repository unzipRepoFromStreamToFolder(
			InputStream rawZipFileInputStream, File destinationFolder)
			throws IOException, ArchiveException {
		unzip(rawZipFileInputStream, destinationFolder);
		rawZipFileInputStream.close();
		File gitDir = new File(destinationFolder,".git");
		assertThat("gitdir "+gitDir+" exists",gitDir.exists(), is(true));
		return new FileRepository(gitDir);
	}
}
