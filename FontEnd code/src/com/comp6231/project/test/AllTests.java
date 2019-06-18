package com.comp6231.project.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ Methodtest.class, MultithreadTestcases.class, UserClientTest.class })
public class AllTests {

}
