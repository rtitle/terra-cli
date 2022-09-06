package bio.terra.cli.businessobject.resource;

import bio.terra.cli.businessobject.Context;
import bio.terra.cli.businessobject.Resource;
import bio.terra.cli.serialization.persisted.resource.PDAzureStorageContainer;
import bio.terra.cli.serialization.userfacing.resource.UFAzureStorageContainer;
import bio.terra.cli.service.WorkspaceManagerService;
import bio.terra.workspace.model.AzureStorageContainerResource;
import bio.terra.workspace.model.ResourceDescription;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal representation of an Azure storage container workspace resource. Instances of this class
 * are part of the current context or state.
 */
public class AzureStorageContainer extends Resource {
  private static final Logger logger = LoggerFactory.getLogger(BqDataset.class);

  private final UUID storageAccountId;
  private final String storageContainerName;

  /** Deserialize an instance of the disk format to the internal object. */
  public AzureStorageContainer(PDAzureStorageContainer configFromDisk) {
    super(configFromDisk);
    this.storageAccountId = configFromDisk.storageAccountId;
    this.storageContainerName = configFromDisk.storageContainerName;
  }

  /** Deserialize an instance of the WSM client library object to the internal object. */
  public AzureStorageContainer(ResourceDescription wsmObject) {
    super(wsmObject.getMetadata());
    this.resourceType = Type.AZURE_STORAGE_CONTAINER;
    this.storageAccountId =
        wsmObject.getResourceAttributes().getAzureStorageContainer().getStorageAccountId();
    this.storageContainerName =
        wsmObject.getResourceAttributes().getAzureStorageContainer().getStorageContainerName();
  }

  /** Deserialize an instance of the WSM client library create object to the internal object. */
  public AzureStorageContainer(AzureStorageContainerResource wsmObject) {
    super(wsmObject.getMetadata());
    this.resourceType = Type.AZURE_STORAGE_CONTAINER;
    this.storageAccountId = wsmObject.getAttributes().getStorageAccountId();
    this.storageContainerName = wsmObject.getAttributes().getStorageContainerName();
  }

  //    /**
  //     * Add a BigQuery dataset as a referenced resource in the workspace.
  //     *
  //     * @return the resource that was added
  //     */
  //    public static BqDataset addReferenced(CreateBqDatasetParams createParams) {
  //        validateResourceName(createParams.resourceFields.name);
  //
  //        GcpBigQueryDatasetResource addedResource =
  //                WorkspaceManagerService.fromContext()
  //                        .createReferencedBigQueryDataset(Context.requireWorkspace().getUuid(),
  // createParams);
  //        logger.info("Created BQ dataset: {}", addedResource);
  //
  //        // convert the WSM object to a CLI object
  //        Context.requireWorkspace().listResourcesAndSync();
  //        return new BqDataset(addedResource);
  //    }

  //    /**
  //     * Create a BigQuery dataset as a controlled resource in the workspace.
  //     *
  //     * @return the resource that was created
  //     */
  //    public static BqDataset createControlled(CreateBqDatasetParams createParams) {
  //        validateResourceName(createParams.resourceFields.name);
  //
  //        // call WSM to create the resource
  //        GcpBigQueryDatasetResource createdResource =
  //                WorkspaceManagerService.fromContext()
  //                        .createControlledBigQueryDataset(Context.requireWorkspace().getUuid(),
  // createParams);
  //        logger.info("Created BQ dataset: {}", createdResource);
  //
  //        // convert the WSM object to a CLI object
  //        Context.requireWorkspace().listResourcesAndSync();
  //        return new BqDataset(createdResource);
  //    }

  /**
   * Serialize the internal representation of the resource to the format for command input/output.
   */
  public UFAzureStorageContainer serializeToCommand() {
    return new UFAzureStorageContainer(this);
  }

  /** Serialize the internal representation of the resource to the format for writing to disk. */
  public PDAzureStorageContainer serializeToDisk() {
    return new PDAzureStorageContainer(this);
  }

  /** Update a BigQuery dataset referenced resource in the workspace. */
  //    public void updateReferenced(UpdateReferencedBqDatasetParams updateParams) {
  //        if (updateParams.resourceParams.name != null) {
  //            validateResourceName(updateParams.resourceParams.name);
  //        }
  //        if (updateParams.projectId != null) {
  //            this.projectId = updateParams.projectId;
  //        }
  //        if (updateParams.datasetId != null) {
  //            this.datasetId = updateParams.datasetId;
  //        }
  //        if (updateParams.cloningInstructions != null) {
  //            this.cloningInstructions = updateParams.cloningInstructions;
  //        }
  //        WorkspaceManagerService.fromContext()
  //                .updateReferencedBigQueryDataset(Context.requireWorkspace().getUuid(), id,
  // updateParams);
  //        super.updatePropertiesAndSync(updateParams.resourceParams);
  //    }

  /** Update a BigQuery dataset controlled resource in the workspace. */
  //    public void updateControlled(UpdateControlledBqDatasetParams updateParams) {
  //        if (updateParams.resourceFields.name != null) {
  //            validateResourceName(updateParams.resourceFields.name);
  //        }
  //        WorkspaceManagerService.fromContext()
  //                .updateControlledBigQueryDataset(Context.requireWorkspace().getUuid(), id,
  // updateParams);
  //        super.updatePropertiesAndSync(updateParams.resourceFields);
  //    }

  /** Delete a BigQuery dataset referenced resource in the workspace. */
  protected void deleteReferenced() {
    //        // call WSM to delete the reference
    //        WorkspaceManagerService.fromContext()
    //                .deleteReferencedBigQueryDataset(Context.requireWorkspace().getUuid(), id);
  }

  /** Delete a BigQuery dataset controlled resource in the workspace. */
  protected void deleteControlled() {
    //        // call WSM to delete the resource
    //        WorkspaceManagerService.fromContext()
    //                .deleteControlledBigQueryDataset(Context.requireWorkspace().getUuid(), id);
  }

  /** Resolve an Azure storage contaiber resource to a SAS token. */
  public String resolve() {
    return WorkspaceManagerService.fromContext()
        .getAzureStorageContainerSasToken(Context.getWorkspace().get().getUuid(), id);
  }

  /**
   * Resolve a BigQuery dataset resource to its cloud identifier. Returns the SQL path to the
   * dataset: [GCP project id].[BQ dataset id]
   */
  //    public String resolve(BqResolvedOptions resolveOption) {
  //        switch (resolveOption) {
  //            case FULL_PATH:
  //                return projectId + BQ_PROJECT_DATASET_DELIMITER + datasetId;
  //            case DATASET_ID_ONLY:
  //                return datasetId;
  //            case PROJECT_ID_ONLY:
  //                return projectId;
  //            default:
  //                throw new IllegalArgumentException("Unknown BigQuery dataset resolve option.");
  //        }
  //    }

  /** Query the cloud for information about the dataset. */
  //    public Optional<Dataset> getDataset() {
  //        try {
  //            BigQueryCow bigQueryCow =
  //                    CrlUtils.createBigQueryCow(Context.requireUser().getPetSACredentials());
  //            return Optional.of(bigQueryCow.datasets().get(projectId, datasetId).execute());
  //        } catch (Exception ex) {
  //            logger.error("Caught exception looking up dataset", ex);
  //            return Optional.empty();
  //        }
  //    }

  // ====================================================
  // Property getters.

  public UUID getStorageAccountId() {
    return storageAccountId;
  }

  public String getStorageContainerName() {
    return storageContainerName;
  }
}
