package org.jenkinsci.plugins.pluginusage.analyzer;

import hudson.PluginWrapper;
import hudson.maven.MavenModuleSet;
import hudson.model.Job;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.pluginusage.JobsPerPlugin;

import java.util.Map;

public class MavenJobAnalyzer  extends JobAnalyzer {

    @Override
    protected void doJobAnalyze(Job item, Map<PluginWrapper, JobsPerPlugin> mapJobsPerPlugin) {
        super.doJobAnalyze(null, mapJobsPerPlugin);
        if (Jenkins.get().getPlugin("maven-plugin") != null){
            if (item instanceof MavenModuleSet) {
                PluginWrapper usedPlugin = getUsedPlugin(MavenModuleSet.DescriptorImpl.class);
                addItem(item, mapJobsPerPlugin, usedPlugin);
            }
        }
    }
}
