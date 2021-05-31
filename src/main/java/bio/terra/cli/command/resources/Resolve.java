package bio.terra.cli.command.resources;

import bio.terra.cli.command.helperclasses.BaseCommand;
import bio.terra.cli.command.helperclasses.options.Format;
import bio.terra.cli.command.helperclasses.options.ResourceName;
import bio.terra.cli.context.GlobalContext;
import bio.terra.cli.context.Resource;
import bio.terra.cli.context.resources.BqDataset;
import bio.terra.cli.context.resources.GcsBucket;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/** This class corresponds to the third-level "terra resources resolve" command. */
@Command(name = "resolve", description = "Resolve a resource to its cloud id or path.")
public class Resolve extends BaseCommand {
  @CommandLine.Mixin ResourceName resourceNameOption;

  @CommandLine.Option(
      names = "--exclude-bucket-prefix",
      description = "[For GCS_BUCKET] Exclude the 'gs://' prefix.")
  private boolean excludeBucketPrefix;

  @CommandLine.Option(
      names = "--bq-path",
      showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
      description =
          "[For BIG_QUERY_DATASET] Cloud id format: FULL_PATH=[project id].[dataset id], DATASET_ID_ONLY=[dataset id], PROJECT_ID_ONLY=[project id]")
  private BqDataset.ResolveOptions bqPathFormat = BqDataset.ResolveOptions.FULL_PATH;

  @CommandLine.Mixin Format formatOption;

  /** Resolve a resource in the workspace to its cloud identifier. */
  @Override
  protected void execute() {
    Resource resource =
        GlobalContext.get().requireCurrentWorkspace().getResource(resourceNameOption.name);

    String cloudId;
    switch (resource.resourceType) {
      case GCS_BUCKET:
        cloudId = ((GcsBucket) resource).resolve(!excludeBucketPrefix);
        break;
      case BIG_QUERY_DATASET:
        cloudId = ((BqDataset) resource).resolve(bqPathFormat);
        break;
      default:
        cloudId = resource.resolve();
    }
    formatOption.printReturnValue(cloudId);
  }
}
