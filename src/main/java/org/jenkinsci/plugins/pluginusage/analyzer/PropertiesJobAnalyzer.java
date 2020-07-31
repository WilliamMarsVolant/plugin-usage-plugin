package org.jenkinsci.plugins.pluginusage.analyzer;

import hudson.PluginWrapper;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import org.jenkinsci.plugins.pluginusage.JobsPerPlugin;

import java.util.Map;


public class PropertiesJobAnalyzer extends JobAnalyzer{


	@Override
	protected void doJobAnalyze(Job item, Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin)
	{
		if (item != null){
			Map<JobPropertyDescriptor,JobProperty> properties = item.getProperties();
			for (Map.Entry<JobPropertyDescriptor,JobProperty> entry : properties.entrySet())
			{
				PluginWrapper usedPlugin = getUsedPlugin(entry.getKey().clazz);
				addItem(item, mapJobsPerPlugin, usedPlugin);
			}
		}
	}
}
