package org.jenkinsci.plugins.pluginusage.analyzer;

import hudson.DescriptorExtensionList;
import hudson.PluginWrapper;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import org.jenkinsci.plugins.pluginusage.JobsPerPlugin;

import java.util.Map;

public class SCMJobAnalyzer extends JobAnalyzer{
	
	public SCMJobAnalyzer() {
		DescriptorExtensionList<SCM, SCMDescriptor<?>> all = SCM.all();
		for(SCMDescriptor<?> b: all)
		{
			PluginWrapper usedPlugin = getUsedPlugin(b.clazz);
			plugins.add(usedPlugin);
		}
	}

	@Override
	protected void doJobAnalyze(Job item, Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin)
	{		
		super.doJobAnalyze(null, mapJobsPerPlugin);
		if(item instanceof AbstractProject){
			PluginWrapper scmPlugin = getUsedPlugin(((AbstractProject)item).getScm().getDescriptor().clazz);
			addItem(item, mapJobsPerPlugin, scmPlugin);
		}
	}

}
