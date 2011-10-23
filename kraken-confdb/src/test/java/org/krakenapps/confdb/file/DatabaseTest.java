package org.krakenapps.confdb.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.krakenapps.confdb.CommitLog;
import org.krakenapps.confdb.Config;
import org.krakenapps.confdb.ConfigIterator;
import org.krakenapps.confdb.ConfigTransaction;
import org.krakenapps.confdb.Predicates;

import static org.junit.Assert.*;

public class DatabaseTest {

	private FileConfigDatabase db;
	private FileConfigCollection col;

	@Before
	public void setup() throws IOException {
		File workingDir = new File(System.getProperty("user.dir"));

		db = new FileConfigDatabase(workingDir, "testdb2");
		col = (FileConfigCollection) db.ensureCollection("testcol2");
	}

	@After
	public void teardown() throws IOException {
		db.purge();
	}

	@Test
	public void testDbRollback() throws IOException {
		assertEquals(0, col.count());

		col.add("xeraph");
		col.add("8con");
		assertEquals(2, col.count());

		db.rollback(1, "xeraph", "back to the initial state");
		assertEquals(0, col.count());
	}

	@Test
	public void testTransRollback() throws IOException {
		assertEquals(0, col.count());

		ConfigTransaction xact = db.beginTransaction();
		col.add(xact, "xeraph");
		col.add(xact, "8con");
		assertEquals(0, col.count());
		assertEquals(2, col.count(xact));

		xact.rollback();
		assertEquals(0, col.count());

		col.add("xeraph");
		assertEquals(1, col.count());
	}

	@Test
	public void testTransaction() throws IOException {
		assertEquals(0, col.count());

		ConfigTransaction xact = db.beginTransaction();
		col.add(xact, "xeraph");
		col.add(xact, "8con");
		xact.commit("xeraph", "added members");

		assertEquals(2, col.count());

		assertEquals(2, db.getCommitLogs().size());
		CommitLog log = db.getCommitLogs().get(1);
		assertEquals("xeraph", log.getCommitter());
		assertEquals("added members", log.getMessage());
		assertEquals(1, log.getChangeSet().get(0).getDocId());
		assertEquals(2, log.getChangeSet().get(1).getDocId());
	}

	@Test
	public void testFlashback() throws IOException {
		List<CommitLog> logs = db.getCommitLogs();
		assertEquals(1, logs.size());

		col.add("hello world", "xeraph", "first commit");
		col.add("goodbye world", "xeraph", "second commit");

		assertNotNull(col.findOne(Predicates.eq("hello world")));
		assertNotNull(col.findOne(Predicates.eq("goodbye world")));

		// back to the revision 2
		File workingDir = new File(System.getProperty("user.dir"));
		db = new FileConfigDatabase(workingDir, "testdb2", 2);
		col = (FileConfigCollection) db.ensureCollection("testcol2");

		assertNotNull(col.findOne(Predicates.eq("hello world")));
		assertNull(col.findOne(Predicates.eq("goodbye world")));

		// back to the revision 1 (just created)
		db = new FileConfigDatabase(workingDir, "testdb2", 1);
		col = (FileConfigCollection) db.ensureCollection("testcol2");

		assertNull(col.findOne(Predicates.eq("hello world")));
		assertNull(col.findOne(Predicates.eq("goodbye world")));
	}

	@Test
	public void testCommitLog() {
		// first commit log is "create collection"
		List<CommitLog> logs = db.getCommitLogs();
		assertEquals(1, logs.size());

		col.add("hello world", "xeraph", "first commit");
		col.add("goodbye world", "xeraph", "second commit");

		logs = db.getCommitLogs();
		assertEquals(3, logs.size());
		CommitLog log1 = logs.get(1);
		CommitLog log2 = logs.get(2);

		// TODO: test rev id and created timestamp
		assertEquals("xeraph", log1.getCommitter());
		assertEquals("first commit", log1.getMessage());

		assertEquals("xeraph", log2.getCommitter());
		assertEquals("second commit", log2.getMessage());

		// test doc content
		ConfigIterator it = col.findAll();
		Config c1 = it.next();
		Config c2 = it.next();

		assertEquals("hello world", c1.getDocument());
		assertEquals("goodbye world", c2.getDocument());

		it.close();
	}

	@Test
	public void testUpdate() {
		List<CommitLog> logs = db.getCommitLogs();
		assertEquals(1, logs.size());

		Config c = col.add("hello world", "xeraph", "first commit");
		c.setDocument("hello, world");
		col.update(c, false, "stania", "added missing comma");

		logs = db.getCommitLogs();
		assertEquals(3, logs.size());
		CommitLog log1 = logs.get(1);
		CommitLog log2 = logs.get(2);

		// TODO: test rev id and created timestamp
		assertEquals("xeraph", log1.getCommitter());
		assertEquals("first commit", log1.getMessage());

		assertEquals("stania", log2.getCommitter());
		assertEquals("added missing comma", log2.getMessage());

		// test doc content
		ConfigIterator it = col.findAll();
		Config c1 = it.next();

		assertEquals("hello, world", c1.getDocument());
		assertFalse(it.hasNext());

		it.close();
	}

	@Test
	public void testDelete() {
		List<CommitLog> logs = db.getCommitLogs();
		assertEquals(1, logs.size());

		Config c = col.add("hello world", "xeraph", "first commit");
		col.remove(c, false, "stania", "removed hello world");

		logs = db.getCommitLogs();
		assertEquals(3, logs.size());
		CommitLog log1 = logs.get(1);
		CommitLog log2 = logs.get(2);

		// TODO: test rev id and created timestamp
		assertEquals("xeraph", log1.getCommitter());
		assertEquals("first commit", log1.getMessage());

		assertEquals("stania", log2.getCommitter());
		assertEquals("removed hello world", log2.getMessage());

		// test doc content
		ConfigIterator it = col.findAll();
		assertFalse(it.hasNext());

		it.close();
	}
}