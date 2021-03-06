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

        session.update(maxReading, Motor.State.STOPPED);
        session.update(minReading, Motor.State.STOPPED);

        Assert.assertEquals(maxReading.angle, session.getHighAngle().intValue());
        Assert.assertEquals(minReading.angle, session.getLowAngle().intValue());
    }

    @Test
    public void testHold() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(100);
        firstReading.timestamp = LocalDateTime.now();
        PatientSession session = new PatientSession();
        session.update(firstReading, Motor.State.UP_FAST);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.update(firstReading, Motor.State.STOPPED);
        Assert.assertEquals(true, session.getHoldStopwatch().isRunning());
        session.update(firstReading, Motor.State.UP_SLOW);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.update(firstReading, Motor.State.STOPPED);
        session.update(firstReading, Motor.State.DOWN_SLOW);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.update(firstReading, Motor.State.DOWN_FAST);
        Assert.assertEquals(false, session.getHoldStopwatch().isRunning());
        session.update(firstReading, Motor.State.STOPPED);
        Assert.assertEquals(true, session.getHoldStopwatch().isRunning());
    }

    @Test
    public void testRepetition() throws Exception {
        Config.init(null, "therapypi.properties");

        AngleReading firstReading = new AngleReading(110);
        firstReading.timestamp = LocalDateTime.now();
        AngleReading secondReading = new AngleReading(101);
        secondReading.timestamp = firstReading.timestamp.plusSeconds(5);
        AngleReading thirdReading = new AngleReading(100);
        thirdReading.timestamp = secondReading.timestamp.plusSeconds(15);

        AngleReading fourthReading = new AngleReading(100);
        fourthReading.timestamp = thirdReading.timestamp.plusSeconds(5);
        AngleReading fifthReading = new AngleReading(101);
        fifthReading.timestamp = fourthReading.timestamp.plusSeconds(5);
        AngleReading sixthReading = new AngleReading(110);
        sixthReading.timestamp = fifthReading.timestamp.plusSeconds(5);

        PatientSession session = new PatientSession();
        session.update(firstReading, Motor.State.STOPPED);
        session.update(secondReading, Motor.State.STOPPED);
        session.update(thirdReading, Motor.State.STOPPED);
        session.update(fourthReading, Motor.State.STOPPED);
        session.update(fifthReading, Motor.State.STOPPED);
        session.update(sixthReading, Motor.State.STOPPED);
        Assert.assertEquals(1, session.getRepetitions());
    }

    @Test
    public void testWildRepetition() throws Exception {
        Config.init(null, "therapypi.properties");

        PatientSession session = new PatientSession();
        session.update(new AngleReading(110, LocalDateTime.now()), Motor.State.STOPPED);
        session.update(new AngleReading(105, LocalDateTime.now().plusSeconds(5)), Motor.State.STOPPED);
        session.update(new AngleReading(100, LocalDateTime.now().plusSeconds(10)), Motor.State.STOPPED);
        session.update(new AngleReading(95, LocalDateTime.now().plusSeconds(15)), Motor.State.STOPPED);

        session.update(new AngleReading(100, LocalDateTime.now().plusSeconds(20)), Motor.State.STOPPED);
        session.update(new AngleReading(105, LocalDateTime.now().plusSeconds(21)), Motor.State.STOPPED);
        session.update(new AngleReading(100, LocalDateTime.now().plusSeconds(22)), Motor.State.STOPPED);
        session.update(new AngleReading(105, LocalDateTime.now().plusSeconds(23)), Motor.State.STOPPED);
        session.update(new AngleReading(110, LocalDateTime.now().plusSeconds(24)), Motor.State.STOPPED);
        session.update(new AngleReading(115, LocalDateTime.now().plusSeconds(25)), Motor.State.STOPPED);
        Assert.assertEquals(1, session.getRepetitions());
        session.update(new AngleReading(105, LocalDateTime.now().plusSeconds(26)), Motor.State.STOPPED);
        session.update(new AngleReading(115, LocalDateTime.now().plusSeconds(27)), Motor.State.STOPPED);
        Assert.assertEquals(2, session.getRepetitions());
    }

}
