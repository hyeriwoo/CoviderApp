package com.example.project2android.object;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.project2android.globalDB;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;

/*
NOTE: Testing the 'Building' object assumes the 'User' object is implemented
    properly
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(globalDB.class)
public class BuildingTest {
    private static final String TAG = "BuildingTest";

    @Mock
    private User user;

    @Mock
    FirebaseFirestore db;

    private Building building;

    @Before
    public void setup() {
        PowerMockito.mockStatic(globalDB.class);

        building = new Building();
    }

    // user who tested for symptoms cannot check in
    @Test
    public void testCheckIn_False_Symptoms() {
        when(user.getRecentHealth()).thenReturn("Symptoms");
        assertEquals(false, building.checkIn(user));
        verify(user, atLeastOnce()).getRecentHealth();
    }

    // user who tested positive cannot check in
    @Test
    public void testCheckIn_False_Positive() {
        when(user.getRecentHealth()).thenReturn("Positive");
        assertEquals(false, building.checkIn(user));
        verify(user, atLeastOnce()).getRecentHealth();
    }

    // user can check in
    @Test
    public void testCheckIn_True() {
        when(user.getRecentHealth()).thenReturn("Negative");
        assertEquals(true, building.checkIn(user));
        verify(user, atLeastOnce()).getRecentHealth();
    }

    // increment risk threshold for every risky visitor
    @Test
    public void testCheckIn_updateRiskThreshold_Min() {
        final int RISKY_VISITORS = 1;

        HashSet<User> temp = mock(HashSet.class);
        Whitebox.setInternalState(building,
                "positiveOrSymptoms", temp);

        when(temp.size()).thenReturn(RISKY_VISITORS);
        when(user.getRecentHealth()).thenReturn("Negative");

        int oldRiskThreshold = Whitebox.getInternalState(building,
                "riskThreshold");

        building.checkIn(user);
        try {
            int newRiskThreshold = Whitebox.getInternalState(building,
                    "riskThreshold");

            assertEquals(oldRiskThreshold + 5*RISKY_VISITORS,
                    newRiskThreshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // maximize risk threshold to 100
    @Test
    public void testCheckIn_updateRiskThreshold_Max() {
        final int RISKY_VISITORS = 21;
        final int MAX_RISKTHRESHOLD = 100;

        HashSet<User> temp = mock(HashSet.class);
        Whitebox.setInternalState(building,
                "positiveOrSymptoms", temp);

        when(temp.size()).thenReturn(RISKY_VISITORS);
        when(user.getRecentHealth()).thenReturn("Negative");

        building.checkIn(user);
        try {
            int newRiskThreshold = Whitebox.getInternalState(building,
                    "riskThreshold");

            assertEquals(MAX_RISKTHRESHOLD, newRiskThreshold);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // increment risk level after 25 visitors
    @Test
    public void testCheckIn_updateRiskLevel_Min() {
        final int MIN_VISITORS = 25;
        HashSet<User> temp = mock(HashSet.class);
        Whitebox.setInternalState(building,
                "visitor", temp);

        when(temp.size()).thenReturn(MIN_VISITORS);
        when(user.getRecentHealth()).thenReturn("Negative");

        int oldRiskLevel = Whitebox.getInternalState(building,
                "riskLevel");

        building.checkIn(user);
        try {
            int newRiskLevel = Whitebox.getInternalState(building,
                    "riskLevel");

            assertEquals(oldRiskLevel+1, newRiskLevel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // maximize risk level
    @Test
    public void testCheckIn_updateRiskLevel_Max() {
        final int MAX_VISITORS = 25*101;
        final int MAX_RISKLEVEL = 100;
        HashSet<User> temp = mock(HashSet.class);
        Whitebox.setInternalState(building,
                "visitor", temp);
        Whitebox.setInternalState(building,
                "positiveOrSymptoms", temp);

        when(temp.size()).thenReturn(MAX_VISITORS);
        when(user.getRecentHealth()).thenReturn("Negative");

        building.checkIn(user);
        try {
            int newRiskLevel = Whitebox.getInternalState(building,
                    "riskLevel");

            assertEquals(MAX_RISKLEVEL, newRiskLevel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // risk level should decrease after three 'AllYes' surveys
    @Test
    public void testCheckIn_getNumOfSurveyYes_3Survey() {
        ArrayList<Boolean> answers = mock(ArrayList.class);
        when(answers.size()).thenReturn(1);
        when(answers.get(0)).thenReturn(true);

        building.addSurveyAnswers(answers);
        building.addSurveyAnswers(answers);
        building.addSurveyAnswers(answers);

        HashSet<User> temp = mock(HashSet.class);
        Whitebox.setInternalState(building,
                "visitor", temp);

        when(temp.size()).thenReturn(3);
        when(user.getRecentHealth()).thenReturn("Negative");

        building.checkIn(user);

        int newRiskLevel = Whitebox.getInternalState(building,
                "riskLevel");

        assertEquals(0, newRiskLevel);
    }

    // when one of the survey's answer is false
    @Test
    public void testCheckIn_getNumOfSurveyYes_NotAllYes() {
        ArrayList<Boolean> answers = mock(ArrayList.class);
        when(answers.size()).thenReturn(1);
        when(answers.get(0)).thenReturn(false);

        building.addSurveyAnswers(answers);

        HashSet<User> temp = mock(HashSet.class);
        Whitebox.setInternalState(building,
                "visitor", temp);

        when(temp.size()).thenReturn(1);
        when(user.getRecentHealth()).thenReturn("Negative");

        int oldRiskLevel = Whitebox.getInternalState(building,
                "riskLevel");
        building.checkIn(user);
        int newRiskLevel = Whitebox.getInternalState(building,
                "riskLevel");

        assertEquals(oldRiskLevel, newRiskLevel);
    }

    // makes sure a survey is added to building
    @Test
    public void testAddSurveyAnswers_AddSurvey() {
        ArrayList<Boolean> answers = mock(ArrayList.class);
        int oldSize = 0;

        building.addSurveyAnswers(answers);

        HashSet<Survey> surveyAnswers = Whitebox.getInternalState(building,
                "surveyAnswers");
        int newSize = surveyAnswers.size();
        assertEquals(oldSize+1, newSize);
    }
}