package com.relivethefuture.dkb

import com.relivethefuture.generic.BasicSample
import com.relivethefuture.generic.LoopSettings
import griffon.test.GriffonUnitTestCase

class BasicSampleTest extends GriffonUnitTestCase {
    void testCopyConstructor() {
        BasicSample s1 = new BasicSample(new File("d:/tmp/Kitbuilder/in/Bassdrums/Bassdrum1.wav"))
        BasicSample s2 = new BasicSample(s1)
        assertEquals s1.name, s2.name
        assertEquals s1.exclude, s2.exclude
        assertEquals s1.root, s2.root
        assertEquals s1.file.absolutePath, s2.file.absolutePath
        assertEquals s1.looping, s2.looping
        assertEquals s1.pan, s2.pan
        assertEquals s1.volume, s2.volume
        assertEquals s1.tune, s2.tune
        if(s1.loopSettings) {
            assertNotNull s2.loopSettings
        }
    }
}
