package com.spriton.therapypi;


import com.spriton.therapypi.components.AngleReading;
import com.spriton.therapypi.components.Motor;
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

        session.addAngleReading(maxReading, Motor.State.STOPPED);
        session.addAngleReading(minReading, Motor.State.STOPPED);

        Assert.assertEquals(maxReading.angle, session.getHighAngle().intValue());
        Assert.assertEquals(minReading.angle, session.getLowAngle().intValue());
    }

    @Test
    public void testHold() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(100);
        firstReading.timestamp = LocalDateTime.now();
        PatientSession session = new PatientSession();
        session.addAngleReading(firstReading, Motor.State.UP_FAST);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.addAngleReading(firstReading, Motor.State.STOPPED);
        Assert.assertEquals(true, session.getHoldStopwatch().isRunning());
        session.addAngleReading(firstReading, Motor.State.UP_SLOW);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.addAngleReading(firstReading, Motor.State.STOPPED);
        session.addAngleReading(firstReading, Motor.State.DOWN_SLOW);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.addAngleReading(firstReading, Motor.State.DOWN_FAST);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.addAngleReading(firstReading, Motor.State.STOPPED);
        Assert.assertEquals(true, session.getHoldStopwatch().isRunning());
    }

    @Test
    public void testRepetition() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(103);
        firstReading.timestamp = LocalDateTime.now();
        AngleReading secondReading = new AngleReading(101);
        secondReading.timestamp = firstReading.timestamp.plusSeconds(1);
        AngleReading thirdReading = new AngleReading(100);
        thirdReading.timestamp = secondReading.timestamp.plusSeconds(1);

        AngleReading fourthReading = new AngleReading(100);
        fourthReading.timestamp = thirdReading.timestamp.plusSeconds(1);
        AngleReading fifthReading = new AngleReading(101);
        fifthReading.timestamp = fourthReading.timestamp.plusSeconds(1);
        AngleReading sixthReading = new AngleReading(103);
        sixthReading.timestamp = fifthReading.timestamp.plusSeconds(1);

        PatientSession session = new PatientSession();
        session.addAngleReading(firstReading, Motor.State.STOPPED);
        session.addAngleReading(secondReading, Motor.State.STOPPED);
        session.addAngleReading(thirdReading, Motor.State.STOPPED);
        session.addAngleReading(fourthReading, Motor.State.STOPPED);
        session.addAngleReading(fifthReading, Motor.State.STOPPED);
        session.addAngleReading(sixthReading, Motor.State.STOPPED);
        Assert.assertEquals(1, session.getRepetitions());
    }

}
