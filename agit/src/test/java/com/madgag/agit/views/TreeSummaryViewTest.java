package com.madgag.agit.views;

import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static com.madgag.agit.OracleJVMTestEnvironment.helper;
import static org.eclipse.jgit.lib.FileMode.TREE;

public class TreeSummaryViewTest {
    private static Repository repo;

    @BeforeClass
    public static void setUp() throws Exception {
        repo = helper().unpackRepo("small-repo.with-tags.zip");
    }

    @Test
    public void shouldListRootTreeWithoutRecursing() throws Exception {
        ObjectId id = ObjectId.fromString("ee09c23bf8aa2ea0106067a4eef456cd2a77baac");
        System.out.println("The root tree...");
        useTreeWalk(id);
        useCanonicalTreeParser(id);
    }

    @Test
    public void shouldListASubtree() throws Exception {
        ObjectId subTreeId = ObjectId.fromString("13256318930219e661306560b74090896ec133c2");
        System.out.println("A subtree...");
        useTreeWalk(subTreeId);
        useCanonicalTreeParser(subTreeId);
    }

    private void useTreeWalk(AnyObjectId id) throws IOException {
        TreeWalk tw = new TreeWalk(repo);
        int nth = tw.addTree(id);

        System.out.println("TreeWalk");

        while (tw.next()) {
            ObjectId newObjectId = tw.getObjectId(nth);
            String rawPath= new String(tw.getRawPath());
            System.out.println(newObjectId+" rawPath="+rawPath+" subTree="+ tw.isSubtree());
        }
    }


    private void useCanonicalTreeParser(ObjectId id) throws IOException {
        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        RevTree tree=new RevWalk(repo).lookupTree(id);
        treeParser.reset(repo.getObjectDatabase().newReader(), tree);
        System.out.println("CanonicalTreeParser");
        for (;!treeParser.eof();treeParser=treeParser.next()) {
            ObjectId newObjectId = treeParser.getEntryObjectId();
            String entryPath= treeParser.getEntryPathString();
            System.out.println(newObjectId + " entryPath=" + entryPath + " subTree=" + TREE.equals(treeParser.getEntryFileMode()));
        }
    }

}
