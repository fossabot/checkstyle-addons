package com.thomasjensen.checkstyle.addons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import org.junit.Assert;

// @formatter:off
/**
 * This class was copied from the Checkstyle project. The original source is <a
 * href="https://github.com/checkstyle/checkstyle/blob/checkstyle-5.8/src/test/java/com/puppycrawl/tools/checkstyle/BaseCheckTestSupport.java"
 * target="_blank">on GitHub</a>. <p/>Credit goes to Oliver Burn, Ivan Sopov, et al.
 * <p/>
 * Used under the terms of the GNU LESSER GENERAL PUBLIC LICENSE, Version 2.1.
 *
 * @author Oliver Burn, Ivan Sopov, et al.
 */
// @formatter:on
public abstract class BaseCheckTestSupport
{
    /** A brief logger that only display info about errors. */
    protected static class BriefLogger
        extends DefaultLogger
    {
        public BriefLogger(final OutputStream pOut) throws UnsupportedEncodingException
        {
            super(pOut, true);
        }



        @Override
        public void auditStarted(final AuditEvent pEvent)
        {
            // empty
        }



        @Override
        public void fileFinished(final AuditEvent pEvent)
        {
            // empty
        }



        @Override
        public void fileStarted(final AuditEvent pEvent)
        {
            // empty
        }
    }



    protected final ByteArrayOutputStream mBAOS = new ByteArrayOutputStream();

    protected final PrintStream mStream = new PrintStream(mBAOS);

    protected final Properties mProps = new Properties();



    public static DefaultConfiguration createCheckConfig(final Class<?> pClazz)
    {
        return new DefaultConfiguration(pClazz.getName());
    }



    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    protected Checker createChecker(final Configuration pCheckConfig)
        throws Exception
    {
        final DefaultConfiguration dc = createCheckerConfig(pCheckConfig);
        final Checker c = new Checker();
        // make sure the tests always run with english error messages
        // so the tests don't fail in supported locales like german
        final Locale locale = Locale.ENGLISH;
        c.setLocaleCountry(locale.getCountry());
        c.setLocaleLanguage(locale.getLanguage());
        c.setModuleClassLoader(Thread.currentThread().getContextClassLoader());
        c.configure(dc);
        c.addListener(new BriefLogger(mStream));
        return c;
    }



    protected DefaultConfiguration createCheckerConfig(final Configuration pConfig)
    {
        final DefaultConfiguration dc = new DefaultConfiguration("configuration");
        final DefaultConfiguration twConf = createCheckConfig(TreeWalker.class);
        // make sure that the tests always run with this charset
        dc.addAttribute("charset", "UTF-8");
        dc.addChild(twConf);
        twConf.addChild(pConfig);
        return dc;
    }



    protected static String getPath(final String pFilename)
        throws IOException
    {
        return new File("src/test/resources/com/thomasjensen/checkstyle/addons/checks/" + pFilename).getCanonicalPath();
    }



    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    protected void verify(final Configuration pConfig, final String pFileName, final String[] pExpected)
        throws Exception
    {
        verify(createChecker(pConfig), pFileName, pFileName, pExpected);
    }



    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    protected void verify(final Checker pChecker, final String pFileName, final String[] pExpected)
        throws Exception
    {
        verify(pChecker, pFileName, pFileName, pExpected);
    }



    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    protected void verify(final Checker pChecker, final String pProcessedFilename, final String pMessageFileName,
        final String[] pExpected)
        throws Exception
    {
        verify(pChecker, new File[]{new File(pProcessedFilename)}, pMessageFileName, pExpected);
    }



    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    protected void verify(final Checker pChecker, final File[] pProcessedFiles, final String pMessageFileName,
        final String[] pExpected)
        throws Exception
    {
        mStream.flush();
        final List<File> theFiles = Lists.newArrayList();
        Collections.addAll(theFiles, pProcessedFiles);
        final int errs = pChecker.process(theFiles);

        // process each of the lines
        final ByteArrayInputStream bais = new ByteArrayInputStream(mBAOS.toByteArray());
        final LineNumberReader lnr = new LineNumberReader(new InputStreamReader(bais));

        for (int i = 0; i < pExpected.length; i++) {
            final String expected = pMessageFileName + ":" + pExpected[i];
            final String actual = lnr.readLine();
            Assert.assertEquals("error message " + i, expected, actual);
        }

        Assert.assertEquals("unexpected output: " + lnr.readLine(), pExpected.length, errs);
        pChecker.destroy();
    }



    /**
     * Gets the check message 'as is' from appropriate 'messages.properties' file.
     *
     * @param pMessageKey the key of message in 'messages.properties' file.
     */
    public String getCheckMessage(final String pMessageKey)
    {
        Properties pr = new Properties();
        try {
            pr.load(getClass().getResourceAsStream("messages.properties"));
        }
        catch (IOException e) {
            return null;
        }
        return pr.getProperty(pMessageKey);
    }
}
