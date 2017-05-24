package io.smartcat.ranger;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.model.User;

public class RangeRuleDateCornerCasesTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "subzero")
                .withRanges("birthDate", tenDaysAgo, threeDaysAgo).toBeGenerated(3).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : aggregatedObjectGenerator) {
            long timeDiffBeginingOfTheRange = u.getBirthDate().getTime()
                    - tenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange == 0) {
                oneExactlyAtTheBeginingOfTheRange = true;
            }

            long timeDiffEndOfRange = u.getBirthDate().getTime()
                    - threeDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfRange == -1) {
                oneExactlyAtTheEndOfTheRange = true;
            }
        }

        Assert.assertTrue("One user must be born exactly 10 days ago", oneExactlyAtTheBeginingOfTheRange);
        Assert.assertTrue("One user must be born a millisecond before 3 days ago", oneExactlyAtTheEndOfTheRange);
    }

    @Test
    public void should_set_low_and_high_end_values_for_multirange() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysAgo = now.minusDays(10);
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime fiveDaysAgo = now.minusDays(5);
        LocalDateTime threeDaysAgo = now.minusDays(3);

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "subzero")
                .withRanges("birthDate", tenDaysAgo, sevenDaysAgo, fiveDaysAgo, threeDaysAgo).toBeGenerated(10).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        boolean userExactly10DaysAgo = false;
        boolean user7DaysAgoMinusMillisecond = false;
        boolean userExactly5DaysAgo = false;
        boolean user3DaysAgoMinusMillisecond = false;

        for (User u : aggregatedObjectGenerator) {
            long timeDiffBeginingOfTheRange1 = u.getBirthDate().getTime()
                    - tenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange1 == 0) {
                Assert.assertEquals("subzero", u.getUsername());
                userExactly10DaysAgo = true;
            }

            long timeDiffEndOfRange1 = u.getBirthDate().getTime()
                    - sevenDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfRange1 == -1) {
                Assert.assertEquals("subzero", u.getUsername());
                user7DaysAgoMinusMillisecond = true;
            }

            long timeDiffBeginingOfTheRange3 = u.getBirthDate().getTime()
                    - fiveDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffBeginingOfTheRange3 == 0) {
                Assert.assertEquals("subzero", u.getUsername());
                userExactly5DaysAgo = true;
            }

            long timeDiffEndOfTheRange3 = u.getBirthDate().getTime()
                    - threeDaysAgo.toInstant(ZoneOffset.UTC).toEpochMilli();
            if (timeDiffEndOfTheRange3 == -1) {
                Assert.assertEquals("subzero", u.getUsername());
                user3DaysAgoMinusMillisecond = true;
            }
        }

        Assert.assertTrue("One user must be born exactly 10 days ago", userExactly10DaysAgo);
        Assert.assertTrue("One user must be born a millisecond before 7 days ago", user7DaysAgoMinusMillisecond);

        Assert.assertTrue("One user must be born exactly 5 days ago", userExactly5DaysAgo);
        Assert.assertTrue("One user must be born a millisecond before 3 days ago", user3DaysAgoMinusMillisecond);
    }

}