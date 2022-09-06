package bio.terra.cli.serialization.userfacing;

import bio.terra.cli.businessobject.Workspace;
import bio.terra.cli.utils.UserIO;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.PrintStream;
import java.util.Map;

public class UFWorkspaceLight {
  // "id" instead of "userFacingId" because user sees this with "terra workspace describe
  // --format=json"
  public String id;
  public String name;
  public String description;
  public String googleProjectId;
  public String tenantId;
  public String subscriptionId;
  public String managedResourceGroupName;
  public Map<String, String> properties;
  public String serverName;
  public String userEmail;

  /**
   * It's expected that the workspace passed into this constructor does not have its resources
   * populated. If it does, then one should create a UFWorkspace instead.
   *
   * @param internalObj
   */
  public UFWorkspaceLight(Workspace internalObj) {
    this.id = internalObj.getUserFacingId();
    this.name = internalObj.getName();
    this.description = internalObj.getDescription();
    this.googleProjectId = internalObj.getGoogleProjectId();
    this.tenantId = internalObj.getTenantId();
    this.subscriptionId = internalObj.getSubscriptionId();
    this.managedResourceGroupName = internalObj.getManagedResourceGroupName();
    this.properties = internalObj.getProperties();
    this.serverName = internalObj.getServerName();
    this.userEmail = internalObj.getUserEmail();
  }

  /** Constructor for Jackson deserialization during testing. */
  private UFWorkspaceLight(UFWorkspaceLight.Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.googleProjectId = builder.googleProjectId;
    this.tenantId = builder.tenantId;
    this.subscriptionId = builder.subscriptionId;
    this.managedResourceGroupName = builder.managedResourceGroupName;
    this.properties = builder.properties;
    this.serverName = builder.serverName;
    this.userEmail = builder.userEmail;
  }

  /** Default constructor for subclass Builder constructor */
  protected UFWorkspaceLight() {
    this.id = null;
    this.name = null;
    this.description = null;
    this.googleProjectId = null;
    this.tenantId = null;
    this.subscriptionId = null;
    this.managedResourceGroupName = null;
    this.properties = null;
    this.serverName = null;
    this.userEmail = null;
  }

  /** Print out a workspace object in text format. */
  public void print() {
    PrintStream OUT = UserIO.getOut();
    // "id" instead of "userFacingId" because user sees this with "terra workspace describe
    // --format=json"
    OUT.println("ID:                       " + id);
    OUT.println("Name:                     " + name);
    OUT.println("Description:              " + description);
    if (googleProjectId != null) {
      OUT.println("Google project:           " + googleProjectId);
      OUT.println(
          "Cloud console:            https://console.cloud.google.com/home/dashboard?project="
              + googleProjectId);
    }
    if (tenantId != null) {
      OUT.println("Tenant ID:                " + tenantId);
      OUT.println("Subscription ID:          " + subscriptionId);
      OUT.println("Managed Resource Group:   " + managedResourceGroupName);
    }

    if (properties == null) {
      return;
    }
    OUT.println("Properties:");
    properties.forEach((key, value) -> OUT.println("  " + key + ": " + value));
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
  public static class Builder {
    // "id" instead of "userFacingId" because user sees this with "terra workspace describe
    // --format=json"
    private String id;
    private String name;
    private String description;
    private String googleProjectId;
    private String tenantId;
    private String subscriptionId;
    private String managedResourceGroupName;
    private Map<String, String> properties;
    private String serverName;
    private String userEmail;

    /** Default constructor for Jackson. */
    public Builder() {}

    public UFWorkspaceLight.Builder id(String id) {
      this.id = id;
      return this;
    }

    public UFWorkspaceLight.Builder name(String name) {
      this.name = name;
      return this;
    }

    public UFWorkspaceLight.Builder description(String description) {
      this.description = description;
      return this;
    }

    public UFWorkspaceLight.Builder googleProjectId(String googleProjectId) {
      this.googleProjectId = googleProjectId;
      return this;
    }

    public UFWorkspaceLight.Builder tenantId(String tenantId) {
      this.tenantId = tenantId;
      return this;
    }

    public UFWorkspaceLight.Builder subscriptionId(String subscriptionId) {
      this.subscriptionId = subscriptionId;
      return this;
    }

    public UFWorkspaceLight.Builder managedResourceGroupName(String managedResourceGroupName) {
      this.managedResourceGroupName = managedResourceGroupName;
      return this;
    }

    public UFWorkspaceLight.Builder properties(Map<String, String> properties) {
      this.properties = properties;
      return this;
    }

    public UFWorkspaceLight.Builder serverName(String serverName) {
      this.serverName = serverName;
      return this;
    }

    public UFWorkspaceLight.Builder userEmail(String userEmail) {
      this.userEmail = userEmail;
      return this;
    }

    /** Call the private constructor. */
    public UFWorkspaceLight build() {
      return new UFWorkspaceLight(this);
    }
  }
}
