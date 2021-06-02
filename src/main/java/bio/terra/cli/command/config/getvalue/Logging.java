package bio.terra.cli.command.config.getvalue;

import bio.terra.cli.Context;
import bio.terra.cli.command.shared.BaseCommand;
import bio.terra.cli.command.shared.options.Format;
import bio.terra.cli.utils.Logger;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/** This class corresponds to the fourth-level "terra config get-value logging" command. */
@Command(name = "logging", description = "Get the logging level.")
public class Logging extends BaseCommand {
  @CommandLine.Mixin Format formatOption;

  /** Return the logging level properties of the global context. */
  @Override
  protected void execute() {
    LoggingReturnValue loggingLevels =
        new LoggingReturnValue(
            Context.getConfig().getConsoleLoggingLevel(),
            Context.getConfig().getFileLoggingLevel());
    formatOption.printReturnValue(loggingLevels, Logging::printText);
  }

  /**
   * POJO class for printing out this command's output. This class is also used by the `terra config
   * list` command, so it needs to be public.
   */
  public static class LoggingReturnValue {
    // global logging context = log levels for file and stdout
    public Logger.LogLevel consoleLoggingLevel;
    public Logger.LogLevel fileLoggingLevel;

    public LoggingReturnValue(
        Logger.LogLevel consoleLoggingLevel, Logger.LogLevel fileLoggingLevel) {
      this.consoleLoggingLevel = consoleLoggingLevel;
      this.fileLoggingLevel = fileLoggingLevel;
    }
  }

  /** Print this command's output in text format. */
  public static void printText(LoggingReturnValue returnValue) {
    OUT.println(
        "[logging, console] logging level for printing directly to the terminal = "
            + returnValue.consoleLoggingLevel);
    OUT.println(
        "[logging, file] logging level for writing to files in "
            + Context.getLogFile().getParent()
            + " = "
            + returnValue.fileLoggingLevel);
  }

  /** This command never requires login. */
  @Override
  protected boolean requiresLogin() {
    return false;
  }
}
