package bio.terra.cli.command.notebooks;

import bio.terra.cli.app.AuthenticationManager;
import bio.terra.cli.app.DockerToolsManager;
import bio.terra.cli.model.GlobalContext;
import bio.terra.cli.model.WorkspaceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import picocli.CommandLine;

/** This class corresponds to the third-level "terra notebooks stop" command. */
@CommandLine.Command(
    name = "stop",
    description = "Stop a running AI Notebook instance within your workspace.")
public class Stop implements Callable<Integer> {

  @CommandLine.Parameters(index = "0", description = "The name of the notebook instance.")
  private String instanceName;

  @CommandLine.Option(
      names = "location",
      defaultValue = "us-central1-a",
      description = "The Google Cloud location of the instance, by default '${DEFAULT-VALUE}'.")
  private String location;

  @Override
  public Integer call() {
    GlobalContext globalContext = GlobalContext.readFromFile();
    WorkspaceContext workspaceContext = WorkspaceContext.readFromFile();
    workspaceContext.requireCurrentWorkspace();

    AuthenticationManager authenticationManager =
        new AuthenticationManager(globalContext, workspaceContext);
    authenticationManager.loginTerraUser();

    String command = "gcloud notebooks instances stop $INSTANCE_NAME --location=$LOCATION";
    Map<String, String> envVars = new HashMap<>();
    envVars.put("INSTANCE_NAME", instanceName);
    envVars.put("LOCATION", location);

    new DockerToolsManager(globalContext, workspaceContext)
        .runToolCommand(
            command, /* workingDir =*/ null, envVars, /* bindMounts =*/ new HashMap<>());

    return 0;
  }
}
