package com.netscape.cmstools.authority;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.dogtagpki.cli.CommandCLI;

import com.netscape.certsrv.authority.AuthorityClient;
import com.netscape.certsrv.authority.AuthorityData;
import com.netscape.certsrv.ca.AuthorityID;
import com.netscape.cmstools.cli.MainCLI;

public class AuthorityCreateCLI extends CommandCLI {

    public AuthorityCLI authorityCLI;

    public AuthorityCreateCLI(AuthorityCLI authorityCLI) {
        super("create", "Create CAs", authorityCLI);
        this.authorityCLI = authorityCLI;
    }

    public void createOptions() {
        Option optParent = new Option(null, "parent", true, "ID of parent CA");
        optParent.setArgName("id");
        options.addOption(optParent);

        Option optDesc = new Option(null, "desc", true, "Optional description");
        optDesc.setArgName("string");
        options.addOption(optDesc);
    }

    public void printHelp() {
        formatter.printHelp(getFullName() + " <dn>", options);
    }

    public void execute(CommandLine cmd) throws Exception {

        String[] cmdArgs = cmd.getArgs();

        if (cmdArgs.length < 1) {
            throw new Exception("No DN specified.");

        } else if (cmdArgs.length > 1) {
            throw new Exception("Too many arguments.");
        }

        String parentAIDString = null;
        if (cmd.hasOption("parent")) {
            parentAIDString = cmd.getOptionValue("parent");
            try {
                new AuthorityID(parentAIDString);
            } catch (IllegalArgumentException e) {
                throw new Exception("Bad CA ID: " + parentAIDString, e);
            }
        } else {
            throw new Exception("Must specify parent authority");
        }

        String desc = null;
        if (cmd.hasOption("desc"))
            desc = cmd.getOptionValue("desc");

        String dn = cmdArgs[0];

        MainCLI mainCLI = (MainCLI) getRoot();
        mainCLI.init();

        AuthorityData data = new AuthorityData(
            null, dn, null, parentAIDString, null, null, true /* enabled */, desc, null);

        AuthorityClient authorityClient = authorityCLI.getAuthorityClient();
        AuthorityData newData = authorityClient.createCA(data);
        AuthorityCLI.printAuthorityData(newData);
    }
}
