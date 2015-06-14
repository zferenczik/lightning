package uk.co.automatictester.lightning;

import com.beust.jcommander.JCommander;
import uk.co.automatictester.lightning.params.CmdLineParams;

public class Runner {

    public static void main(String[] args) {

        CmdLineParams params = new CmdLineParams();
        new JCommander(params, args);

        if (!params.skipSchemaValidation()) {
            XMLSchemaValidator.validate(params.getXmlFile());
        }

        TestSet.load(params.getXmlFile());
        TransactionData.load(params.getCsvFile());
        TestSet.execute();
        TestSet.reportResults();

    }

}
