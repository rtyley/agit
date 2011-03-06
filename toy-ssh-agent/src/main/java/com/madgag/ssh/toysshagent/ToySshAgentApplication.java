package com.madgag.ssh.toysshagent;

import java.util.List;

import roboguice.application.RoboApplication;

import com.google.inject.Module;

public class ToySshAgentApplication extends RoboApplication {
	protected void addApplicationModules(List<Module> modules) {
        modules.add(new ToySshAgentModule());
    }
}
