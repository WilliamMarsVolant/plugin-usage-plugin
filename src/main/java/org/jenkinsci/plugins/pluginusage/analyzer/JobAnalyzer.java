package org.jenkinsci.plugins.pluginusage.analyzer;

import hudson.PluginWrapper;
import hudson.model.Job;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.pluginusage.JobsPerPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


public abstract class JobAnalyzer{

	private static final java.util.logging.Logger LOGGER = Logger.getLogger(JobCollector.class.getName());

	protected List<PluginWrapper> plugins = new ArrayList<>();

	protected PluginWrapper getUsedPlugin(Class clazz) {
		return Jenkins.get().getPluginManager().whichPlugin(clazz);
	}

	protected void doJobAnalyze(Job item, Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin)
	{
		for(PluginWrapper plugin: plugins)
		{
			if(plugin!=null)
			{
				synchronized (JobCollector.MAP_LOCK) {
					long startTime = System.currentTimeMillis();
					JobsPerPlugin jobsPerPlugin = mapJobsPerPlugin.get( plugin );
					if ( jobsPerPlugin == null ) {
						JobsPerPlugin jobsPerPlugin2 = new JobsPerPlugin( plugin );
						mapJobsPerPlugin.put( plugin, jobsPerPlugin2 );
					}
					long endTime = System.currentTimeMillis();
					long analyzeTime = endTime - startTime;
					String message = "[JOB_ANALYZER] plugin: " + plugin.getShortName() + " time: " + analyzeTime + "ms";
					LOGGER.warning(message);
				}
			}
		}
	}

	public List<PluginWrapper> getPlugins() {
		return plugins;
	}

	protected void 	addItem(Job item, Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin, PluginWrapper usedPlugin) {
		if (usedPlugin != null) {
			synchronized (JobCollector.MAP_LOCK) {
				JobsPerPlugin jobsPerPlugin = mapJobsPerPlugin.get( usedPlugin );
				if ( jobsPerPlugin != null ) {
					jobsPerPlugin.addProject( item );
				} else {
					JobsPerPlugin jobsPerPlugin2 = new JobsPerPlugin( usedPlugin );
					jobsPerPlugin2.addProject( item );
					mapJobsPerPlugin.put( usedPlugin, jobsPerPlugin2 );
				}
			}
		}
	}

}
