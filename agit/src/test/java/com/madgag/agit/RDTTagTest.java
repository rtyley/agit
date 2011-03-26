package com.madgag.agit;

import static com.madgag.compress.CompressUtil.unzip;
import static java.lang.System.currentTimeMillis;
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
		assertThat(gitDir.exists(), is(true));
		return new FileRepository(gitDir);
	}
}
