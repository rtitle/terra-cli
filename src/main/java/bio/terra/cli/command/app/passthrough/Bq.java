package bio.terra.cli.command.app.passthrough;

import bio.terra.cli.apps.AppsRunner;
import bio.terra.cli.command.helperclasses.BaseCommand;
import java.util.List;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/** This class corresponds to the second-level "terra bq" command. */
@Command(name = "bq", description = "Use the bq tool in the Terra workspace.", hidden = true)
public class Bq extends BaseCommand {

  @CommandLine.Unmatched private List<String> cmdArgs;

  /** Pass the command through to the CLI Docker image. */
  @Override
  protected void execute() {
    String fullCommand = AppsRunner.buildFullCommand("bq", cmdArgs);

    // no need for any special setup or teardown logic since bq is already initialized when the
    // container starts
    new AppsRunner(globalContext, workspaceContext).runToolCommand(fullCommand);
  }
}
