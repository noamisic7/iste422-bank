import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckingAccountTestFixture {
    public static Logger logger = LogManager.getLogger(CheckingAccountTestFixture.class);
    // We could read the file from classpath instead of hardcoding the pathname too
    //static final String TEST_FILE = "src/test/resources/CheckingAccountTest.csv";

    record TestScenario(double initBalance,
            List<Double> checks,
            List<Double> withdrawals,
            List<Double> deposits,
            boolean runMonthEnd,
            double endBalance) {
    }

    private static List<TestScenario> testScenarios;

    @Test
    public static void runTestScenarios(String filename) throws Exception {
        if (testScenarios == null) {
            System.err.println("Starting tests for file: " + filename);
            System.err.println("\n\n");
            System.err.println("************************************");
            System.err.println("************************************");
            System.err.println();
            System.err.println("Note: NOT running any Test Scenarios");
            System.err.println("Run main() method to run scenarios!!");
            System.err.println();
            System.err.println("************************************");
            System.err.println("************************************");
            System.err.println("\n\n");
            return;
        }

        // iterate over all test scenarios
        for (int testNum = 0; testNum < testScenarios.size(); testNum++) {
            TestScenario scenario = testScenarios.get(testNum);
            logger.info("**** Running test for {}", scenario);

            // set up account with specified starting balance
            CheckingAccount ca = new CheckingAccount(
                    "test " + testNum, -1, scenario.initBalance, 0, -1);

            // now process checks, withdrawals, deposits
            for (double checkAmount : scenario.checks) {
                ca.writeCheck("CHECK", checkAmount, new Date());
            }
            for (double withdrawalAmount : scenario.withdrawals) {
                ca.withdraw(withdrawalAmount);
            }
            for (double depositAmount : scenario.deposits) {
                ca.deposit(depositAmount);
            }

            // run month-end if desired and output register
            if (scenario.runMonthEnd) {
                ca.monthEnd();
                for (RegisterEntry entry : ca.getRegisterEntries()) {
                    logger.info("Register Entry {} -- {}: {}", entry.id(), entry.entryName(), entry.amount());

                }
            }

            // make sure the balance is correct
            assertThat("Test #" + testNum + ":" + scenario, ca.getBalance(), is(scenario.endBalance));
        }
    }

    private static void runJunitTests() {
        JUnitCore jc = new JUnitCore();
        jc.addListener(new TextListener(System.out));
        Result r = jc.run(CheckingAccountTestFixture.class);
        System.out.printf("Tests run: %d Passed: %d Failed: %d\n",
                r.getRunCount(), r.getRunCount() - r.getFailureCount(), r.getFailureCount());
        System.out.println("Failures:");
        for (Failure f : r.getFailures()) {
            System.out.println("\t" + f);
        }
    }

    // TODO this could be added to TestScenario class
    private static List<Double> parseListOfAmounts(String amounts) {
        if (amounts.trim().isEmpty()) {
            return List.of();
        }
        List<Double> ret = new ArrayList<>();
        logger.debug("Amounts to split: {}", amounts);
        for (String amtStr : amounts.trim().split("\\|")) {
            logger.debug("An Amount: {}", amtStr);
            ret.add(Double.parseDouble(amtStr));
        }
        return ret;
    }

    // TODO this could be added to TestScenario class
    private static TestScenario parseScenarioString(String scenarioAsString) {
        String[] scenarioValues = scenarioAsString.split(",");
        // should probably validate length here
        double initialBalance = Double.parseDouble(scenarioValues[0]);
        List<Double> checks = parseListOfAmounts(scenarioValues[1]);
        List<Double> wds = parseListOfAmounts(scenarioValues[2]);
        List<Double> deps = parseListOfAmounts(scenarioValues[3]);
        double finalBalance = Double.parseDouble(scenarioValues[4]);
        TestScenario scenario = new TestScenario(
                initialBalance, checks, wds, deps, false, finalBalance);
        return scenario;
    }

    private static List<TestScenario> parseScenarioStrings(List<String> scenarioStrings) {
        logger.info("Running test scenarios...");
        List<TestScenario> scenarios = new ArrayList<>();
        for (String scenarioAsString : scenarioStrings) {
            if (scenarioAsString.trim().isEmpty()) {
                continue;
            }
            TestScenario scenario = parseScenarioString(scenarioAsString);
            scenarios.add(scenario);
        }
        return scenarios;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("START");

        if (args.length < 1) {
            System.err.println("Please provide the filename as a command-line argument.");
            System.exit(1);
        }

        String filename = args[0];
        System.out.println("Running tests with data from: " + filename);

        // Logic to read the file and use it for testing
        try {
            // Assuming you have logic to load data from the file
            runTestScenarios(filename);
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(2);
        }
        System.out.println("DONE");
    }
}