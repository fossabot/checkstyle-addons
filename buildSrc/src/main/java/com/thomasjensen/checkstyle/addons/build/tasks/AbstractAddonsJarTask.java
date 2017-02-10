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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import groovy.lang.Closure;
import org.apache.tools.ant.filters.ReplaceTokens;
import org.gradle.api.file.CopySpec;
import org.gradle.api.tasks.bundling.Jar;


/**
 * Base class for our JAR creating tasks. Provides common functionality.
 *
 * @author Thomas Jensen
 */
public abstract class AbstractAddonsJarTask
    extends Jar
{
    protected AbstractAddonsJarTask()
    {
        super();
    }



    @SuppressWarnings("MethodDoesntCallSuperMethod")
    protected final void intoFrom(@Nonnull final Object pDestPath, @Nonnull final Object... pSourceObjects)
    {
        into(pDestPath, new Closure<Void>(this)
        {
            @Override
            public Void call(final Object... pArgs)
            {
                final CopySpec spec = (CopySpec) getDelegate();
                spec.from(pSourceObjects);
                return null;
            }
        });
    }



    protected final void filterVersion(@Nonnull final CopySpec pSpec, @Nonnull final String pVersion)
    {
        Map<String, String> placeHolders = new HashMap<>();
        placeHolders.put("version", pVersion);
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put("tokens", placeHolders);
        pSpec.filter(propsMap, ReplaceTokens.class);
    }
}
