/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Test

import java.io.File
import java.io.IOException
import java.io.BufferedReader
import java.io.FileReader

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

import static org.gradle.testkit.runner.TaskOutcome.*

public class BoostTestJaxrs20 extends AbstractBoostTest {

    static File resourceDir = new File("build/resources/test/j2eeApp")
    static File testProjectDir = new File(integTestDir, "testjaxrs20")
    static String buildFilename = "jaxrs20.gradle"

    private static final String JAX_RS_20_FEATURE = "<feature>jaxrs-2.0</feature>"
    private static String SERVER_XML = "build/wlp/usr/servers/BoostServer/server.xml"

    @Before
    public void setup() {
        createDir(testProjectDir)
        createTestProject(testProjectDir, resourceDir, buildFilename)
    }

    @Test
    public void testPackageSuccess() throws IOException {
       System.out.println("Testing Package")
        BuildResult result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("boostPackage")
            .build()

          assertTrue("Build Result is null", result != null)
          assertEquals(SUCCESS, result.task(":installLiberty").getOutcome())
          assertEquals(SUCCESS, result.task(":libertyCreate").getOutcome())
          assertEquals(SUCCESS, result.task(":boostPackage").getOutcome())
        
    }

    @Test //Testing that JAXRS-2.0 feature was added to the packaged server.xml
    public void testPackageContents() throws IOException {
        File targetFile = new File(testProjectDir, SERVER_XML)
        assertTrue(targetFile.getCanonicalFile().toString() + "does not exist.", targetFile.exists())
        
        // Check contents of file for jaxrs-2.0 feature
        boolean found = false
        BufferedReader br = null
        
        try {
            br = new BufferedReader(new FileReader(targetFile));
            String line
            while ((line = br.readLine()) != null) {
                if (line.contains(JAX_RS_20_FEATURE)) {
                    found = true
                    break
                }
            }
        } finally {
            if (br != null) {
                br.close()
            }
        }
        
        assertTrue("The "+JAX_RS_20_FEATURE+" feature was not found in the server configuration", found);    
    }
}
