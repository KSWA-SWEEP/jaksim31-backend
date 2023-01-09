package com.sweep.jaksim31;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AuthServiceImplTest.class, MemberServiceImplTest.class, QboxServiceTest.class, SurveyServiceTest.class, SurveyRespServiceTest.class})
public class IntegrationTest {
}
