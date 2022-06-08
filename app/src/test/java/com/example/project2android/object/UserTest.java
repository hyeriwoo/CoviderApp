package com.example.project2android.object;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;

public class UserTest {
    private ArrayList<DailyCheck> record;
    LocalDateTime now;
    User user;

    private Date dateConverter(LocalDateTime ldt) {
        ZoneOffset zone = ZoneOffset.UTC;
        return Date.from(ldt.atZone(zone.systemDefault()).toInstant());
    }

    @Before
    public void setUp() {
        record = new ArrayList<>();
        ZoneOffset zone = ZoneOffset.UTC;
        now = LocalDateTime.now(zone);
        user = new User();
    }

    /* test getRecentHealth() */
    // check when the record is null
    @Test
    public void getRecentHealth_null() {
        String expected = "No Test";
        String output = user.getRecentHealth();
        assertEquals("recent health status", expected, output);
    }

    // check when record size == 0
    @Test
    public void getRecentHealth_noTest() {
        user.setRecordTest(record);
        String expected = "No Test";
        String output = user.getRecentHealth();
        assertEquals("recent health status", expected, output);
    }

    // check when the most recent result is Negative
    @Test
    public void getRecentHealth_negative() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        user.setRecordTest(record);

        String expected = "Negative";
        String output = user.getRecentHealth();
        assertEquals("recent health status", expected, output);
    }

    // check when the most recent result is symptom
    @Test
    public void getRecentHealth_symptom() {
        record.add(new DailyCheck(dateConverter(now), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 0));
        user.setRecordTest(record);

        String expected = "Symptoms";
        String output = user.getRecentHealth();
        assertEquals("recent health status", expected, output);
    }

    // check when the most recent result is Positive
    @Test
    public void getRecentHealth_positive() {
        record.add(new DailyCheck(dateConverter(now), 2));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 0));
        user.setRecordTest(record);

        String expected = "Positive";
        String output = user.getRecentHealth();
        assertEquals("recent health status", expected, output);
    }

    // check when the most recent result is Invalid
    @Test
    public void getRecentHealth_invalid() {
        record.add(new DailyCheck(dateConverter(now), 3));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 0));
        user.setRecordTest(record);

        String expected = "Invalid";
        String output = user.getRecentHealth();
        assertEquals("recent health status", expected, output);
    }

    /* test getHealthByTime = required implementation */
    // check when filter by daily and result is negative
    @Test
    public void getHealthByTime_dailyNegative() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        user.setRecordTest(record);

        int expected = 0;
        int output = user.getHealthByTime(0);
        assertEquals("get health by daily", expected, output);
    }

    // check when filter by daily and result is risk
    @Test
    public void getHealthByTime_dailyRisk() {
        record.add(new DailyCheck(dateConverter(now), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 2));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 2));
        user.setRecordTest(record);

        int expected = 1;
        int output = user.getHealthByTime(0);
        assertEquals("get health by daily", expected, output);
    }

    // check when filter by daily and result is Positive
    @Test
    public void getHealthByTime_dailyPositive() {
        record.add(new DailyCheck(dateConverter(now), 2));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        user.setRecordTest(record);

        int expected = 2;
        int output = user.getHealthByTime(0);
        assertEquals("get health by daily", expected, output);
    }

    // check when filter by daily and result is invalid
    @Test
    public void getHealthByTime_dailyInvalid() {
        record.add(new DailyCheck(dateConverter(now), 3));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        user.setRecordTest(record);

        int expected = 3;
        int output = user.getHealthByTime(0);
        assertEquals("get health by daily", expected, output);
    }

    // check when filter by weekly and result is Negative
    @Test
    public void getHealthByTime_weeklyNegative() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        user.setRecordTest(record);

        int expected = 0;
        int output = user.getHealthByTime(1);
        assertEquals("get health by weekly", expected, output);
    }

    // check when filter by weekly and result is risk
    @Test
    public void getHealthByTime_weeklyRisk() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 1));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 2));
        user.setRecordTest(record);

        int expected = 1;
        int output = user.getHealthByTime(1);
        assertEquals("get health by weekly", expected, output);
    }

    // check when filter by weekly and result is positive
    @Test
    public void getHealthByTime_weeklyPositive() {

        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 2));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 0));
        user.setRecordTest(record);

        int expected = 2;
        int output = user.getHealthByTime(1);
        assertEquals("get health by weekly", expected, output);
    }

    // check when filter by weekly and result is invalid
    @Test
    public void getHealthByTime_weeklyInvalid() {
        record.add(new DailyCheck(dateConverter(now), 3));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 3));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        user.setRecordTest(record);

        int expected = 3;
        int output = user.getHealthByTime(1);
        assertEquals("get health by weekly", expected, output);
    }

    // check when filter by monthly and result is negative
    @Test
    public void getHealthByTime_monthlyNegative() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 0));
        record.add(new DailyCheck(dateConverter(now.plusMonths(-2)), 2));
        user.setRecordTest(record);

        int expected = 0;
        int output = user.getHealthByTime(2);
        assertEquals("get health by monthly", expected, output);
    }

    // check when filter by monthly and result is risk
    @Test
    public void getHealthByTime_monthlyRisk() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 1));
        record.add(new DailyCheck(dateConverter(now.plusMonths(-2)), 2));
        user.setRecordTest(record);

        int expected = 1;
        int output = user.getHealthByTime(2);
        assertEquals("get health by monthly", expected, output);
    }

    // check when filter by monthly and result is positive
    @Test
    public void getHealthByTime_monthlyPositive() {
        record.add(new DailyCheck(dateConverter(now), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 0));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 2));
        record.add(new DailyCheck(dateConverter(now.plusMonths(-2)), 1));
        user.setRecordTest(record);

        int expected = 2;
        int output = user.getHealthByTime(2);
        assertEquals("get health by monthly", expected, output);
    }

    // check when filter by weekly and result is invalid
    @Test
    public void getHealthByTime_monthlyInvalid() {
        record.add(new DailyCheck(dateConverter(now), 3));
        record.add(new DailyCheck(dateConverter(now.plusDays(-2)), 3));
        record.add(new DailyCheck(dateConverter(now.plusDays(-10)), 3));
        record.add(new DailyCheck(dateConverter(now.plusMonths(-2)), 1));
        user.setRecordTest(record);

        int expected = 3;
        int output = user.getHealthByTime(1);
        assertEquals("get health by weekly", expected, output);
    }

    // check when check in building is not in visitedBuilding
    @Test
    public void checkInBuilding_notVisited() {
        user.setVisitedBuildings(3);

        Building testBuilding = new Building("test");
        ArrayList<Building> expected = new ArrayList<>();
        expected.add(testBuilding);

        user.checkInBuilding(testBuilding);
        ArrayList<Building> output = user.getVisitedBuildings();

        assertEquals("visitedBuilding size ", expected.size(), output.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals("visitedBuilding element", expected.get(i).getName(), output.get(i).getName());
        }
    }

    // check when checking building is already in visitedBuilding
    @Test
    public void checkInBuilding_visited() {
        user.setVisitedBuildings(3);

        Building testBuilding = new Building("test");
        ArrayList<Building> expectedBuilding = new ArrayList<>();
        expectedBuilding.add(testBuilding);

        user.checkInBuilding(testBuilding);
        ArrayList<Building> outputBuilding = user.getVisitedBuildings();

        int expectedTimeVisited[] = new int[3];
        expectedTimeVisited[expectedBuilding.indexOf(testBuilding)] = 2;

        user.checkInBuilding(testBuilding);
        int outputTimeVisited[] = user.getTimesBuildingVisited();

        assertEquals("visitedBuilding size ", expectedBuilding.size(), outputBuilding.size());
        for (int i = 0; i < expectedBuilding.size(); i++) {
            assertEquals("visitedBuilding element", expectedBuilding.get(i).getName(), outputBuilding.get(i).getName());
        }

        assertEquals("timesBuildingVisited size", expectedTimeVisited.length, outputTimeVisited.length);
        for (int i = 0; i < expectedTimeVisited.length; i++) {
            assertEquals("timesBuildingVisted element", expectedTimeVisited[i], outputTimeVisited[i]);
        }
    }

    //  check whether isFrequentVisited return false if building is not in visited
    @Test
    public void isFrequentlyVisited_notVisited() {
        user.setVisitedBuildings(3);
        Building testBuilding = new Building("test");

        boolean expected = false;
        boolean output = user.isFrequentlyVisited(testBuilding);

        assertEquals("isFrequentVisited return ", expected, output);
    }

    //  check whether isFrequentVisited return false if building is visited, not not frequent
    @Test
    public void isFrequentlyVisited_notFrequent() {
        user.setVisitedBuildings(3);
        Building testBuilding = new Building("test");

        user.checkInBuilding(testBuilding);

        boolean expected = false;
        boolean output = user.isFrequentlyVisited(testBuilding);

        assertEquals("isFrequentVisited return ", expected, output);
    }

    //  check whether isFrequentVisited return false if building is frequent visited (more than 2)
    @Test
    public void isFrequentlyVisited_frequent() {
        user.setVisitedBuildings(3);
        Building testBuilding = new Building("test");

        user.checkInBuilding(testBuilding);
        user.checkInBuilding(testBuilding);
        user.checkInBuilding(testBuilding);

        boolean expected = true;
        boolean output = user.isFrequentlyVisited(testBuilding);

        assertEquals("isFrequentVisited return ", expected, output);
    }

    @After
    public void tearDown() {
        record.clear();
        user = null;
    }
}