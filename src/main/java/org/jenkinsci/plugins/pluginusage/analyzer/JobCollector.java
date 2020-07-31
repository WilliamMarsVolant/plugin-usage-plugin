package org.jenkinsci.plugins.pluginusage.analyzer;

import hudson.PluginWrapper;

import hudson.model.AbstractProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import hudson.model.Job;
import jenkins.model.Jenkins;

import org.jenkinsci.plugins.pluginusage.JobsPerPlugin;

public class JobCollector {
	public static final Object MAP_LOCK = new Object();
	private static final Logger LOGGER = Logger.getLogger(JobCollector.class.getName());
	private ArrayList<JobAnalyzer> analysers = new ArrayList<>();
	
	public JobCollector() {
		analysers.add(new BuilderJobAnalyzer());
		analysers.add(new BuildWrapperJobAnalyzer());
		analysers.add(new PropertiesJobAnalyzer());
		analysers.add(new PublisherJobAnalyzer());
		analysers.add(new SCMJobAnalyzer());
		analysers.add(new TriggerJobAnalyzer());
		analysers.add(new StepAnalyser());
		analysers.add(new MavenJobAnalyzer());
	}

	public Map<PluginWrapper, JobsPerPlugin> getJobsPerPlugin()
	{
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 10);

		final Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin = new HashMap<>();

		// bootstrap map with all job related plugins
		for(JobAnalyzer analyser: analysers)
		{
			analyser.doJobAnalyze(null, mapJobsPerPlugin);
		}

		List<Job> allItems = Jenkins.get().getAllItems(Job.class);
		Collection<Future> futures = new ArrayList<Future>();

		for(final Job item: allItems)
		{
			futures.add( executor.submit( new Runnable() {
				@Override
				public void run() {
					analyzeItem( item, mapJobsPerPlugin );
				}
			} ) );
		}

		for (Future future : futures) {
			try {
				future.get();
			} catch ( InterruptedException | ExecutionException e ) {
				LOGGER.log( Level.INFO, "Plugin Usage Analysis did not complete", e );
			}
		}
		executor.shutdown();

		return mapJobsPerPlugin;
	}

	private void analyzeItem(Job item, Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin) {
		for(JobAnalyzer analyser: analysers)
		{
			try{
				analyser.doJobAnalyze(item, mapJobsPerPlugin);
			} catch(Exception e){
				LOGGER.warning("Exception catched: " + e );
			}
		}
	}
	
	public int getNumberOfJobs() {
		List<AbstractProject> allItems = Jenkins.get().getAllItems(AbstractProject.class);
		return allItems.size();	
	}


    public List<PluginWrapper> getOtherPlugins() {
		List<PluginWrapper> allPlugins = Jenkins.get().getPluginManager().getPlugins();
		List<PluginWrapper> others = new ArrayList<>(allPlugins);

		for(JobAnalyzer analyser: analysers)
		{
			others.removeAll(analyser.getPlugins());
		}

		return others;
    }
}
