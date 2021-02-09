package bio.terra.cli.app.supported;

import bio.terra.cli.app.DockerToolsManager;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class contains logic related to running the supported tool nextflow. */
public class NextflowHelper extends SupportedAppHelper {
  private static final Logger logger = LoggerFactory.getLogger(NextflowHelper.class);

  // Mount point for the nextflow sub-directory on the Docker container.
  public static final String NEXTFLOW_MOUNT_POINT = "/usr/local/etc/nextflow";

  // Default nextflow sub-directory relative to the directory where the Terra CLI is running.
  private static final Path DEFAULT_NEXTFLOW_DIR = Paths.get(".", "nextflow");

  /** Do any command-specific setup. */
  public void enable() {
    // create the nextflow sub-directory on the host, if it does not already exist
    File nextflowDir = DEFAULT_NEXTFLOW_DIR.toFile();
    if (!nextflowDir.exists()) {
      boolean nextflowDirCreated = nextflowDir.mkdirs();
      if (!nextflowDirCreated) {
        throw new RuntimeException("Error creating nextflow sub-directory.");
      }
    }
    // other things we could do here: start a Nextflow Tower instance, sync code from a repository
    // in the nextflow sub-directory?
  }

  /** Run nextflow in the Docker container. */
  public void run(String[] cmdArgs) {
    // mount the nextflow sub-directory of the current directory
    File nextflowDir = DEFAULT_NEXTFLOW_DIR.toFile();
    logger.info("nextflowDir: {}, exists: {}", nextflowDir.getAbsolutePath(), nextflowDir.exists());
    if (!nextflowDir.exists() || !nextflowDir.isDirectory()) {
      throw new RuntimeException(
          "Nextflow sub-directory not found. Run terra app enable nextflow.");
    }
    Map<String, File> bindMounts = new HashMap<>();
    bindMounts.put(NEXTFLOW_MOUNT_POINT, nextflowDir);

    String fullCommand = buildFullCommand("nextflow", cmdArgs);
    new DockerToolsManager(globalContext, workspaceContext)
        .runToolCommand(fullCommand, NEXTFLOW_MOUNT_POINT, new HashMap<>(), bindMounts);
  }

  /** Do any command-specific teardown. */
  public void stop() {
    // things we could do here: stop a Nextflow Tower instance, sync code with a repository and then
    // delete the nextflow sub-directory?
  }
}
