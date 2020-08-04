package org.jenkinsci.plugins.pluginusage;

import hudson.Extension;
import hudson.model.RootAction;
import jenkins.model.Jenkins;

@Extension
public class PluginUsageView implements RootAction {


    public String getDisplayName() {
        return "Plugin Usage";
    }

    public String getIconFileName() {
        return (Jenkins.get().hasPermission(Jenkins.ADMINISTER)) ? "plugin.png" : null;
    }

    public String getUrlName() {
        return (Jenkins.get().hasPermission(Jenkins.ADMINISTER)) ? "pluginusage" : null;
    }

    public PluginUsageModel getData() {
    	if(Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
    	    return new PluginUsageModel();
		}
        return null;
    }


}
