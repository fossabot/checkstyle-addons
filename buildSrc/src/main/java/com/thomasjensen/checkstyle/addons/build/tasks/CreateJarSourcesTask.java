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

import javax.annotation.Nonnull;

import org.gradle.api.tasks.SourceSet;

import com.thomasjensen.checkstyle.addons.build.BuildUtil;
import com.thomasjensen.checkstyle.addons.build.DependencyConfig;


/**
 * Gradle task to create a sources JAR.
 *
 * @author Thomas Jensen
 */
public class CreateJarSourcesTask
    extends AbstractAddonsJarTask
{
    public CreateJarSourcesTask()
    {
        super();
        setClassifier("sources");
    }



    @Override
    public void configureFor(@Nonnull final DependencyConfig pDepConfig)
    {
        // set appendix for archive name
        if (!pDepConfig.isDefaultConfig()) {
            final String appendix = pDepConfig.getName();
            setAppendix(appendix);
        }
        setDescription(
            getBuildUtil().getLongName() + ": Build the source JAR for dependency configuration '" + pDepConfig
                .getName() + "'");

        // SourceSet that fits the dependency configuration
        final SourceSet mainSourceSet = getBuildUtil().getSourceSet(SourceSet.MAIN_SOURCE_SET_NAME);
        final SourceSet sqSourceSet = getBuildUtil().getSourceSet(BuildUtil.SONARQUBE_SOURCE_SET_NAME);

        // Configuration of JAR file contents
        from(mainSourceSet.getAllJava());
        from(sqSourceSet.getAllJava());
        intoFrom("META-INF", "LICENSE");

        // Manifest
        getBuildUtil().inheritManifest(this, pDepConfig);
    }
}
