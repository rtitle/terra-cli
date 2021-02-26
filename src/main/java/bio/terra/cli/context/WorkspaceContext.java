package bio.terra.cli.context;

import bio.terra.cli.context.utils.FileUtils;
import bio.terra.workspace.model.WorkspaceDescription;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This POJO class represents an instance of the Terra CLI workspace context. This is intended
 * primarily for project and resource-related context values that will be particular to a single
 * workspace.
 */
public class WorkspaceContext {
  private static final Logger logger = LoggerFactory.getLogger(WorkspaceContext.class);

  // workspace description object returned by WSM
  public WorkspaceDescription terraWorkspaceModel;

  // map of cloud resources for this workspace (name -> object)
  public Map<String, CloudResource> cloudResources;

  // file paths related to persisting the workspace context on disk
  private static final String WORKSPACE_CONTEXT_DIRNAME = ".terra";
  private static final String WORKSPACE_CONTEXT_FILENAME = "workspace-context.json";

  private WorkspaceContext() {
    this.terraWorkspaceModel = null;
    this.cloudResources = new HashMap<>();
  }

  // ====================================================
  // Persisting on disk

  /**
   * Read in an instance of this class from a JSON-formatted file in the current directory. If there
   * is no existing file, this method returns an object populated with default values.
   *
   * @return an instance of this class
   */
  public static WorkspaceContext readFromFile() {
    // try to read in an instance of the workspace context file
    try {
      return FileUtils.readFileIntoJavaObject(getWorkspaceContextFile(), WorkspaceContext.class);
    } catch (IOException ioEx) {
      logger.warn("Workspace context file not found or error reading it.", ioEx);
    }

    // if the workspace context file does not exist or there is an error reading it, return an
    // object populated with default values
    return new WorkspaceContext();
  }

  /**
   * Write an instance of this class to a JSON-formatted file in the workspace context directory.
   */
  private void writeToFile() {
    try {
      FileUtils.writeJavaObjectToFile(getWorkspaceContextFile(), this);
    } catch (IOException ioEx) {
      logger.error("Error persisting workspace context.", ioEx);
    }
  }

  // ====================================================
  // Workspace

  /**
   * Setter for the current Terra workspace. Persists on disk.
   *
   * @param terraWorkspaceModel the workspace description object
   */
  public void updateWorkspace(WorkspaceDescription terraWorkspaceModel) {
    logger.debug(
        "Updating workspace from {} to {}.",
        getWorkspaceId(),
        terraWorkspaceModel == null ? null : terraWorkspaceModel.getId());
    this.terraWorkspaceModel = terraWorkspaceModel;

    writeToFile();
  }

  /**
   * Getter for the Terra workspace id.
   *
   * @return the Terra workspace id
   */
  @JsonIgnore
  public UUID getWorkspaceId() {
    return terraWorkspaceModel == null ? null : terraWorkspaceModel.getId();
  }

  /**
   * Getter for the Google project backing the current Terra workspace.
   *
   * @return the Google project id
   */
  @JsonIgnore
  public String getGoogleProject() {
    return terraWorkspaceModel == null || terraWorkspaceModel.getGoogleContext() == null
        ? null
        : terraWorkspaceModel.getGoogleContext().getProjectId();
  }

  /** Utility method to test whether a workspace is set in the current context. */
  @JsonIgnore
  public boolean isEmpty() {
    return terraWorkspaceModel == null;
  }

  /**
   * Utility method that throws an exception if there is no workspace set in the current context.
   */
  public void requireCurrentWorkspace() {
    if (isEmpty()) {
      throw new RuntimeException("There is no Terra workspace mounted to the current directory.");
    }
  }

  // ====================================================
  // Cloud resources

  /**
   * Lookup a cloud resource by its name. Names are unique within a workspace.
   *
   * @param name cloud resource name
   * @return cloud resource object
   */
  public CloudResource getCloudResource(String name) {
    return cloudResources.get(name);
  }

  /**
   * Add a cloud resource to the list for this workspace. Persists on disk.
   *
   * @param cloudResource cloud resource to add
   */
  public void addCloudResource(CloudResource cloudResource) {
    cloudResources.put(cloudResource.name, cloudResource);

    writeToFile();
  }

  /**
   * Remove a cloud resource from the list of cloud resources for this workspace. Persists on disk.
   *
   * @param name cloud resource name
   */
  public void removeCloudResource(String name) {
    cloudResources.remove(name);

    writeToFile();
  }

  /**
   * List all cloud resources in the workspace.
   *
   * @return list of cloud resources in the workspace
   */
  public List<CloudResource> listCloudResources() {
    return new ArrayList<>(cloudResources.values());
  }

  /**
   * List all controlled cloud resources for the workspace. This is a utility wrapper around {@link
   * #listControlledResources()} that filters for just the controlled ones.
   *
   * @return list of controlled resources in the workspace
   */
  public List<CloudResource> listControlledResources() {
    return cloudResources.values().stream()
        .filter(dataReference -> dataReference.isControlled)
        .collect(Collectors.toList());
  }

  // ====================================================
  // Resolving file and directory paths

  /**
   * Get a handle for the workspace directory. (i.e. the parent of the .terra directory)
   *
   * @return a handle for the workspace directory
   */
  @JsonIgnore
  public static File getWorkspaceDir() {
    return getWorkspaceContextFile().getParentFile().getParentFile();
  }

  /**
   * Get the current working directory relative to the workspace directory.
   *
   * @return a relative path for the current working directory
   */
  @JsonIgnore
  public static Path getCurrentDirRelativeToWorkspaceDir() {
    Path currentDir = Paths.get("").toAbsolutePath();
    return getWorkspaceDir().toPath().toAbsolutePath().relativize(currentDir);
  }

  /**
   * Get a handle for the workspace context file.
   *
   * <p>This method first searches for an existing workspace context file in the current directory
   * hierarchy. If it finds one, then it returns a handle to that file.
   *
   * <p>If it does not find one, then it returns a handle to the file where a new workspace context
   * can be written. This handle will be relative to the current directory (i.e. current directory =
   * workspace top-level directory)
   *
   * @return a handle for the workspace context file
   */
  @JsonIgnore
  private static File getWorkspaceContextFile() {
    Path currentDir = Path.of("").toAbsolutePath();
    try {
      return getExistingWorkspaceContextFile(currentDir);
    } catch (FileNotFoundException fnfEx) {
      return currentDir
          .resolve(WORKSPACE_CONTEXT_DIRNAME)
          .resolve(WORKSPACE_CONTEXT_FILENAME)
          .toFile();
    }
  }

  /**
   * Get a handle for an existing workspace context file in the current directory hierarchy.
   *
   * <p>For each directory, it checks for the existence of a workspace context file (i.e.
   * ./.terra/workspace-context.json).
   *
   * <p>-If it finds one, then it returns a handle to that file.
   *
   * <p>-Otherwise, it recursively checks the parent directory, until it hits the root directory.
   *
   * <p>-Once it hits the root directory, and still doesn't find a workspace context file, it throws
   * an exception.
   *
   * @param currentDir the directory to search
   * @return the file handle for an existing workspace context file
   * @throws FileNotFoundException if no existing workspace context file is found
   */
  @JsonIgnore
  private static File getExistingWorkspaceContextFile(Path currentDir)
      throws FileNotFoundException {
    // get the workspace context sub-directory relative to the current directory and check if it
    // exists
    Path workspaceContextDir = currentDir.resolve(WORKSPACE_CONTEXT_DIRNAME);
    File workspaceContextDirHandle = workspaceContextDir.toFile();
    if (workspaceContextDirHandle.exists() && workspaceContextDirHandle.isDirectory()) {

      // get the workspace context file relative to the sub-directory and check if it exists
      File workspaceContextFileHandle =
          workspaceContextDir.resolve(WORKSPACE_CONTEXT_FILENAME).toFile();
      if (workspaceContextFileHandle.exists() && workspaceContextFileHandle.isFile()) {
        return workspaceContextFileHandle;
      }
    }

    // if we've reached the root directory, then no existing workspace context file is found
    Path parentDir = currentDir.toAbsolutePath().getParent();
    if (currentDir.getNameCount() == 0 || parentDir == null) {
      throw new FileNotFoundException(
          "No workspace context file found in the current directory hierarchy");
    }

    // recursively check the parent directory
    return getExistingWorkspaceContextFile(parentDir);
  }
}
