package com.spriton.therapypi;


import com.spriton.therapypi.components.AngleReading;
import com.spriton.therapypi.database.PatientSession;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class PatientSessionTest {

    @Test
    public void testMinMax() throws Exception {
        Config.init(null, "therapypi.properties");

        PatientSession session = new PatientSession();
        AngleReading maxReading = new AngleReading(120);
        AngleReading minReading = new AngleReading(1);

        session.addAngleReading(maxReading);
        session.addAngleReading(minReading);

        Assert.assertEquals(maxReading.angle, session.getHighAngle().intValue());
        Assert.assertEquals(minReading.angle, session.getLowAngle().intValue());
    }

    @Test
    public void testNoHold() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(100);
        firstReading.timestamp = LocalDateTime.now();
        AngleReading secondReading = new AngleReading(101);
        secondReading.timestamp = firstReading.timestamp.plusSeconds(1);
        AngleReading thirdReading = new AngleReading(103);
        thirdReading.timestamp = secondReading.timestamp.plusSeconds(1);
        AngleReading fourthReading = new AngleReading(103);
        fourthReading.timestamp = thirdReading.timestamp.plusSeconds(1);
        AngleReading fifthReading = new AngleReading(103);
        fifthReading.timestamp = fourthReading.timestamp.plusSeconds(1);

        PatientSession session = new PatientSession();
        session.addAngleReading(firstReading);
        session.addAngleReading(secondReading);
        session.addAngleReading(thirdReading);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());

        session.addAngleReading(fourthReading);
        session.addAngleReading(fifthReading);
        Assert.assertEquals(true, session.getHoldStopwatch().isRunning());
    }

    @Test
    public void testHold() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(100);
        firstReading.timestamp = LocalDateTime.now();
        AngleReading secondReading = new AngleReading(101);
        secondReading.timestamp = firstReading.timestamp.plusSeconds(1);
        AngleReading thirdReading = new AngleReading(102);
        thirdReading.timestamp = secondReading.timestamp.plusSeconds(1);

        PatientSession session = new PatientSession();
        session.addAngleReading(firstReading);
        session.addAngleReading(secondReading);
        session.addAngleReading(thirdReading);

        Assert.assertEquals(thirdReading.angle, session.getHighAngle().intValue());
        Assert.assertEquals(firstReading.angle, session.getLowAngle().intValue());

        Assert.assertEquals(101, session.getLastHold().angle);
        Assert.assertEquals(true, session.getHoldStopwatch().isRunning());
    }

    @Test
    public void testRepitition() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(100);
        firstReading.timestamp = LocalDateTime.now();
        AngleReading secondReading = new AngleReading(101);
        secondReading.timestamp = firstReading.timestamp.plusSeconds(1);
        AngleReading thirdReading = new AngleReading(103);
        thirdReading.timestamp = secondReading.timestamp.plusSeconds(1);

        AngleReading fourthReading = new AngleReading(102);
        fourthReading.timestamp = thirdReading.timestamp.plusSeconds(1);
        AngleReading fifthReading = new AngleReading(101);
        fifthReading.timestamp = fourthReading.timestamp.plusSeconds(1);
        AngleReading sixthReading = new AngleReading(100);
        sixthReading.timestamp = fifthReading.timestamp.plusSeconds(1);

        PatientSession session = new PatientSession();
        session.addAngleReading(firstReading);
        session.addAngleReading(secondReading);
        session.addAngleReading(thirdReading);
        session.addAngleReading(fourthReading);
        session.addAngleReading(fifthReading);
        session.addAngleReading(sixthReading);
        Assert.assertEquals(1, session.getRepetitions());
    }

}
