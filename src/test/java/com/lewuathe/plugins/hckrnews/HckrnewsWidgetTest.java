package com.lewuathe.plugins.hckrnews;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.Test;
import org.jvnet.hudson.test.HudsonTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HckrnewsWidgetTest extends HudsonTestCase {
    @Test
	public void testDownloader() throws Exception {
		HckrnewsWidget.NewsDownloader  ndownloader = null;
		ndownloader = new HckrnewsWidget.NewsDownloader();

		assertNotNull(ndownloader);
	}

	@Test
	public void testNewsLoad() throws Exception {
		HckrnewsWidget.NewsDownloader ndownloader = null;
		ndownloader = new HckrnewsWidget.NewsDownloader();

		String ret = ndownloader.loadNews();
		assertNotNull(ret);
	}
}
