package com.thomasjensen.checkstyle.addons.build.tasks;
/*
 * Checkstyle-Addons - Additional Checkstyle checks
 * Copyright (C) 2015 Thomas Jensen
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License, version 3, as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this
 * program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.TaskInputs;

import com.thomasjensen.checkstyle.addons.build.BuildUtil;


/**
 * Gradle task to generate a 'pom.properties' file.
 *
 * @author Thomas Jensen
 */
public class GeneratePomPropsTask
    extends DefaultTask
{
    private final File pluginPomProps = new File(getTemporaryDir(), "pom.properties");

    private String appendix = null;

    private final BuildUtil buildUtil;



    /**
     * Constructor.
     */
    public GeneratePomPropsTask()
    {
        super();
        setGroup(BasePlugin.BUILD_GROUP);

        final Project project = getProject();
        buildUtil = new BuildUtil(project);

        // Task Inputs: the property values identifying the artifact
        final TaskInputs inputs = getInputs();
        inputs.property(BuildUtil.GROUP_ID, project.getGroup());
        inputs.property(BuildUtil.ARTIFACT_ID, project.getName());
        inputs.property(BuildUtil.VERSION, project.getVersion().toString());
        if (appendix != null) {
            inputs.property("appendix", getAppendix());
        }

        // Task Outputs: the 'pom.properties' file
        getOutputs().file(pluginPomProps);

        //noinspection MethodDoesntCallSuperMethod
        doLast(new Closure<Void>(this)
        {
            @Override
            public Void call()
            {
                String effectiveArtifactId = (String) inputs.getProperties().get(BuildUtil.ARTIFACT_ID);
                if (getAppendix() != null) {
                    effectiveArtifactId += '-' + getAppendix();
                }

                List<String> entries = new ArrayList<>();
                entries.add("#Generated by Maven");
                entries.add("#@buildTimestamp@");
                entries.add(BuildUtil.GROUP_ID + "=" + inputs.getProperties().get(BuildUtil.GROUP_ID));
                entries.add(BuildUtil.ARTIFACT_ID + "=" + effectiveArtifactId);
                entries.add(BuildUtil.VERSION + "=" + inputs.getProperties().get(BuildUtil.VERSION));

                //noinspection ResultOfMethodCallIgnored
                pluginPomProps.getParentFile().mkdirs();
                try {
                    Files.write(pluginPomProps.toPath(), entries, StandardCharsets.UTF_8);
                }
                catch (IOException e) {
                    throw new GradleException("Failed to create file: " + pluginPomProps.getAbsolutePath(), e);
                }
                return null;
            }
        });
    }



    public String getAppendix()
    {
        return appendix;
    }



    /**
     * Setter.
     *
     * @param pAppendix the appendix of the artifact to which the generated 'pom.properties' belongs
     */
    public void setAppendix(final String pAppendix)
    {
        appendix = pAppendix;
        final String longName = buildUtil.getLongName();
        if (pAppendix != null && pAppendix.length() > 0) {
            getInputs().property("appendix", pAppendix);
            setDescription(longName + ": Create file '" + pluginPomProps.getName() //
                + "' for use in JAR (appendix: " + pAppendix + ")");
        }
        else {
            getInputs().getProperties().remove("appendix");
            setDescription(longName + ": Create file '" + pluginPomProps.getName() + "' for use in JAR (no appendix)");
        }
    }



    public File getPluginPomProps()
    {
        return pluginPomProps;
    }
}
