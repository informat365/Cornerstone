/*
 * ====================================================================
 * Copyright (c) 2004-2008 TMate Software Ltd.  All rights reserved.
 *
 * This software is licensed as described in the file COPYING, which
 * you should have received as part of this distribution.  The terms
 * are also available at http://svnkit.com/license.html
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
package cornerstone.biz.domain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;


public class WorkingCopy {
	private static Logger logger = LoggerFactory.getLogger(WorkingCopy.class);

	//
	public static interface OutputListener {
		void onOutput(String s);
	}

	//
	private SVNClientManager ourClientManager;
	private ISVNEventHandler myCommitEventHandler;
	private ISVNEventHandler myUpdateEventHandler;
	private ISVNEventHandler myWCEventHandler;
	SVNURL repositoryURL;
	SVNRepository repository;
	File destPath;
	private OutputListener outputListener;
	static {
		setupLibrary();
	}
	//

	//
	public WorkingCopy(String name, String password, String svnPath, String localPath) {
		try {
			repositoryURL = SVNURL.parseURIEncoded(svnPath);
			repository = SVNRepositoryFactory.create(repositoryURL);
			repository.setAuthenticationManager(
					SVNWCUtil.createDefaultAuthenticationManager(name, password.toCharArray()));
		} catch (SVNException e) {
			logger.error(e.getMessage(),e);
		}
		myUpdateEventHandler = new UpdateEventHandler(this);
		myWCEventHandler = new WCEventHandler(this);
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ourClientManager = SVNClientManager.newInstance(options, name, password);
		ourClientManager.getCommitClient().setEventHandler(myCommitEventHandler);
		ourClientManager.getUpdateClient().setEventHandler(myUpdateEventHandler);
		ourClientManager.getWCClient().setEventHandler(myWCEventHandler);
		//
		if (localPath != null) {
			destPath = new File(localPath);
		}
	}

	/**
	 * @return the outputListener
	 */
	public OutputListener getOutputListener() {
		return outputListener;
	}

	/**
	 * @param outputListener
	 *            the outputListener to set
	 */
	public void setOutputListener(OutputListener outputListener) {
		this.outputListener = outputListener;
	}

	//
	public void println(String s) {
		if (outputListener != null) {
			outputListener.onOutput(s + "\n");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(s);
		}
	}

	//
	/*
	 * Initializes the library to work with a repository via different protocols.
	 */
	private static void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}

	/*
	 * 
	 */
	public long checkout(SVNDepth depth) throws SVNException {
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		return updateClient.doCheckout(repositoryURL, destPath, SVNRevision.HEAD, SVNRevision.HEAD, depth,
				true);
	}

	public long checkout() throws SVNException {
		return checkout(SVNDepth.INFINITY);
	}
	//
	public void cleanup()  {
		try {
			SVNWCClient wcClient = ourClientManager.getWCClient();
			wcClient.doCleanup(destPath);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/*
	 *   
	 */
	@SuppressWarnings("deprecation")
	public long update() throws SVNException {
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		return updateClient.doUpdate(destPath, SVNRevision.HEAD, true);
	}

	//
	public List<String> logs(long startRevision, long endRevision, int pageSize) {
		List<String> histories = new ArrayList<>();
		try {
			repository.log(new String[] { "" }, startRevision, endRevision, true, true, pageSize, (logEntry) -> {
				histories.add(logEntry.getRevision() + "");
			});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return histories;
	}

	//
	public void copy(String targetDirUrl, String commit) {
		try {
			SVNURL repositoryTrgtUrl = SVNURL.parseURIEncoded(targetDirUrl);
			SVNCopyClient client = ourClientManager.getCopyClient();
			SVNCopySource[] copySources = new SVNCopySource[1];
			copySources[0] = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, repositoryURL);
			client.doCopy(copySources, repositoryTrgtUrl, false, true, true, commit, null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//
	public void delete(String sourceDirUrl, String commit) {
		try {
			SVNURL repositorySrcUrl = SVNURL.parseURIEncoded(sourceDirUrl);
			SVNCommitClient client = ourClientManager.getCommitClient();
			client.doDelete(new SVNURL[] { repositorySrcUrl }, commit);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	//
	public void commit(File[] paths, String commitMessage) {
		try {
			for (File file : paths) {
				checkVersiondDirectory(file);
			}
			SVNCommitClient client = ourClientManager.getCommitClient();
			client.doCommit(paths, false, commitMessage, null, null, false, false,
					SVNDepth.fromRecurse(true));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AppException(e.getMessage());
		}
	}

	/**
	 * Puts directories and files under version control
	 * 
	 * @param clientManager
	 *            SVNClientManager
	 * @param wcPath
	 *            work copy path
	 */
	public void addEntry(File wcPath) {
		try {
			ourClientManager.getWCClient().doAdd(new File[] { wcPath }, 
					true, false, false, SVNDepth.INFINITY, false,
					false, true);
		} catch (SVNException e) {
			logger.error(e.getMessage(), e);
		}
	}

	//
	/**
	 * 递归检查不在版本控制的文件，并add到svn
	 * 
	 * @param clientManager
	 * @param wc
	 */
	private void checkVersiondDirectory(File wc) {
		if (!SVNWCUtil.isVersionedDirectory(wc)) {
			addEntry(wc);
		}
		if (wc.isDirectory()) {
			for (File sub : wc.listFiles()) {
				if (sub.isDirectory() && ".svn".equals(sub.getName())) {
					continue;
				}
				checkVersiondDirectory(sub);
			}
		}
	}
}