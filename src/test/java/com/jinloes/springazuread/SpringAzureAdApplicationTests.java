package com.jinloes.springazuread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.ContainerService;
import com.microsoft.azure.management.graphrbac.ActiveDirectoryGroup;
import com.microsoft.azure.management.graphrbac.ActiveDirectoryUser;
import com.microsoft.azure.management.graphrbac.RoleDefinition;
import com.microsoft.azure.management.graphrbac.implementation.GraphRbacManager;
import com.microsoft.azure.management.graphrbac.implementation.PermissionInner;
import com.microsoft.azure.management.resources.Subscription;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.management.storage.StorageAccountKey;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.rest.LogLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAzureAdApplicationTests {
	public static final String storageConnectionString = System.getenv("STORAGE_CONNECTION_STRING");
	private static final String storageConnectionString2 = System.getenv("STORAGE_CONNECTION_STRING2");
	private String clientId = System.getenv("CLIENT_ID");
	private String clientSecret = System.getenv("CLIENT_SECRET");
	private String subscription = System.getenv("SUBSCRIPTION");
	private String tenantId = "4b09494a-46dc-4076-867e-ad388128e5d9";

	@Test
	public void contextLoads() {
	}

	@Test
	public void testDownloadFlowLog() {
		try {
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			// Retrieve reference to a previously created container.
			CloudBlobContainer container = blobClient.getContainerReference(
					"insights-logs-networksecuritygroupflowevent");
			CloudBlockBlob blockBlob = container.getDirectoryReference("resourceId=")
					.getDirectoryReference("SUBSCRIPTIONS")
					.getDirectoryReference(subscription)
					.getDirectoryReference("RESOURCEGROUPS")
					.getDirectoryReference("JON_RESOURCES")
					.getDirectoryReference("PROVIDERS")
					.getDirectoryReference("MICROSOFT.NETWORK")
					.getDirectoryReference("NETWORKSECURITYGROUPS")
					.getDirectoryReference("JON-TEST-NSG")
					.getDirectoryReference("y=2017")
					.getDirectoryReference("m=06")
					.getDirectoryReference("d=26")
					.getDirectoryReference("h=18")
					.getDirectoryReference("m=00")
					.getBlockBlobReference("PT1H.json");
			Path tmpFile = Files.createTempFile("nflowlog-", ".json");
			blockBlob.download(Files.newOutputStream(tmpFile, StandardOpenOption.TRUNCATE_EXISTING));
			/*// Loop through each blob item in the container.
			for (ListBlobItem blobItem : container.listBlobs()) {
				// If the item is a blob, not a virtual directory.
				if (blobItem instanceof CloudBlob) {
					// Download the item and save it to a file with the same name.
					CloudBlob blob = (CloudBlob) blobItem;
					blob.download(new FileOutputStream("C:\\mydownloads\\" + blob.getName()));
				}
			}*/
			System.out.println(new String(Files.readAllBytes(tmpFile)));
			Files.deleteIfExists(tmpFile);
		} catch (Exception e) {
			// Output the stack trace.
			e.printStackTrace();
		}
	}


	@Test
	public void testGetUsers() {
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.withLogLevel(LogLevel.NONE)
				.authenticate(tokenCredentials);

		List<ActiveDirectoryUser> userList = authenticated.activeDirectoryUsers().list();
		for (ActiveDirectoryUser user : userList) {
			printUser(user, authenticated);
			System.out.println();
		}

		System.out.println("\nCLASSIC ADMINS");
		GraphRbacManager graphRbacManager = GraphRbacManager.configure().authenticate(tokenCredentials);
		graphRbacManager.roleInner()
				.withSubscriptionId(subscription)
				.classicAdministrators().list("2015-06-01")
				.forEach(admin -> {
					System.out.println("ID " + admin.id()
							+ " NAME " + admin.name()
							+ " EMAIL " + admin.properties().emailAddress());
				});

		System.out.println();
		ActiveDirectoryUser user = authenticated.activeDirectoryUsers().getByName("tom@igniteinc.net");
		printUser(user, authenticated);

		System.out.println("\nSubscriptions");
		PagedList<Subscription> subscriptions = authenticated.subscriptions().list();
		subscriptions.forEach(sub -> {
			System.out.println("Name: " + sub.displayName() + " Sub: " + sub.subscriptionId());
		});
	}

	private void printUser(ActiveDirectoryUser user, Azure.Authenticated authenticated) {
		System.out.println(Joiner.on(" ").skipNulls()
				.join(user.userPrincipalName(), user.signInName(), "Email: " + user.mail(), user.mailNickname(),
						user.name(), user.id()));
	}

	@Test
	public void getAdmins() {
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.withLogLevel(LogLevel.NONE)
				.authenticate(tokenCredentials);
		Azure azure = authenticated.withSubscription(subscription);
		authenticated.servicePrincipals().list().forEach(servicePrincipal -> {
			//System.out.println(servicePrincipal.servicePrincipalNames());
			System.out.println(servicePrincipal.id());
		});
	}

	@Test
	public void getRoleAssignments() {
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.withLogLevel(LogLevel.NONE)
				.authenticate(tokenCredentials);

		authenticated.roleAssignments().listByScope("Admin").forEach(roleAssignment -> {
			System.out.println(roleAssignment.scope());
		});
		Azure azure = authenticated.withSubscription(subscription);

		authenticated.roleAssignments().listByScope("/subscriptions/" + subscription)
				.forEach(roleAssignment -> {
					String principalId = roleAssignment.principalId();
					ActiveDirectoryUser user = authenticated.activeDirectoryUsers().getById(principalId);
					if (user != null) {
						System.out.println("ASSIGNMENT " + roleAssignment.principalId() + " " + user.name());
						RoleDefinition roleDefinition = authenticated.roleDefinitions().getById(
								roleAssignment.roleDefinitionId());
						System.out.println("ROLE NAME " + roleDefinition.roleName());
						System.out.println("SCOPES " + roleDefinition.assignableScopes());
						System.out.println("Permissions " + Joiner.on(", ").join(roleDefinition.permissions()
								.stream()
								.map(PermissionInner::actions)
								.collect(Collectors.toSet())));
					}
				});
	}

	@Test
	public void testGetGroups() throws IOException {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://graph.microsoft.com/")
				.addConverterFactory(JacksonConverterFactory.create())
				.build();
		AzureAdClient client = retrofit.create(AzureAdClient.class);

		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.authenticate(tokenCredentials);
		Azure azure = authenticated.withSubscription(subscription);

		authenticated.activeDirectoryGroups().list().forEach(group -> {
			System.out.println("GROUP " + group.name() + " " + group.id());
			try {
				String token = tokenCredentials.getToken("https://graph.microsoft.com/");
				Map<String, Object> members = client.getGroupMembers("Bearer " + token, group.id())
						.execute()
						.body();
				System.out.println("Members: " + new ObjectMapper().writeValueAsString(members));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		GraphRbacManager graphRbacManager = GraphRbacManager.configure().authenticate(tokenCredentials);
		graphRbacManager.groups().inner().list().forEach(group -> {
			System.out.println("Group name: " + group.displayName());
			graphRbacManager.groups().inner().getGroupMembers(group.objectId()).forEach(groupMember -> {
				System.out.println("User id: " + groupMember.objectId());
				System.out.println("User name: " + groupMember.displayName());
				System.out.println("User email: " + groupMember.mail());
				System.out.println("User principal: " + groupMember.userPrincipalName());
			});
			System.out.println();
		});
		graphRbacManager.roleInner()
				.withSubscriptionId(subscription)
				.classicAdministrators().list("2015-06-01")
				.forEach(admin -> {
					System.out.println("ID " + admin.id() + " NAME " + admin.name() + " EMAIL " +
							admin.properties().emailAddress());
				});
	}

	@Test
	public void getRoleAssignmentsStorageAccount() {
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.authenticate(tokenCredentials);

		authenticated.roleAssignments().listByScope("Admin").forEach(roleAssignment -> {
			System.out.println(roleAssignment.scope());
		});
		Azure azure = authenticated.withSubscription(subscription);
		authenticated.roleAssignments().listByScope("/subscriptions/" + subscription
				+ "/resourceGroups/jon_resources/providers/Microsoft.Storage/storageAccounts/jonstorageaccount")
				.forEach(roleAssignment -> {
					String principalId = roleAssignment.principalId();
					ActiveDirectoryUser user = authenticated.activeDirectoryUsers().getById(principalId);
					System.out.println("ID: " + principalId);
					if (user != null) {
						System.out.println("ASSIGNMENT " + roleAssignment.principalId() + " " + user.name());
						RoleDefinition roleDefinition = authenticated.roleDefinitions()
								.getById(roleAssignment.roleDefinitionId());
						System.out.println("ROLE NAME " + roleDefinition.roleName());
						System.out.println("SCOPES " + roleDefinition.assignableScopes());
						System.out.println("Permissions " + Joiner.on(", ").join(roleDefinition.permissions()
								.stream()
								.map(PermissionInner::actions)
								.collect(Collectors.toSet())));
					} else {
						ActiveDirectoryGroup group = authenticated.activeDirectoryGroups().getById(principalId);
						if (group != null) {
							RoleDefinition roleDefinition = authenticated.roleDefinitions()
									.getById(roleAssignment.roleDefinitionId());
							System.out.println("GROUP " + group.name());
							System.out.println("ROLE: " + roleDefinition.roleName());
						}
					}
				});
	}

	@Test
	public void testBlobs() {
		String cloudStorageAccountFmt = "DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s";
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.withLogLevel(LogLevel.NONE)
				.authenticate(tokenCredentials);
		Azure azure = authenticated.withSubscription(subscription);
		PagedList<StorageAccount> storageAccounts = azure.storageAccounts().list();

		storageAccounts.forEach(account -> {
			System.out.println("ACCOUNT " + account.id());
			System.out.println("RESOURCE GROUP: " + account.resourceGroupName());
			List<StorageAccountKey> keys = account.getKeys();
			//System.out.println("TYPE: " + account.type());
			//System.out.println("Kind: " + account.kind());
			//System.out.println("Statuses: " + account.accountStatuses().primary());
			StorageAccountKey key = keys.get(0);
			try {
				CloudStorageAccount storageAccount = CloudStorageAccount.parse(
						String.format(cloudStorageAccountFmt, account.name(), key.value()));
				CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
				Iterable<CloudBlobContainer> containers = cloudBlobClient.listContainers();
				containers.forEach(container -> {
					System.out.println("CONTAINER " + container.getName());
					container.listBlobs().forEach(item -> {
						if (item instanceof CloudBlob) {
							CloudBlob cloudBlob = (CloudBlob) item;
							System.out.println("BLOB Type: " + cloudBlob.getProperties().getBlobType());
							System.out.println("BLOB " + cloudBlob.getName());
							//System.out.println("Last modified" + cloudBlob.getProperties().getLastModified());
							//System.out.println("URI: " + cloudBlob.getUri());
							System.out.println("NAME: " + cloudBlob.getName());
						} else if (item instanceof CloudBlobDirectory) {
							CloudBlobDirectory directory = (CloudBlobDirectory) item;
							System.out.println("DIR " + ((CloudBlobDirectory) item).getPrefix());
							try {
								directory.listBlobs().forEach(nestedItem -> {
									if (nestedItem instanceof CloudBlob) {
										System.out.println("NESTED NAME: " + ((CloudBlob) nestedItem).getName());
									}
								});
							} catch (StorageException e) {
								e.printStackTrace();
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
						} else {
							System.out.println("OTHER " + item.toString());
						}
					});
				});
			} catch (URISyntaxException | InvalidKeyException e) {
				e.printStackTrace();
			}
		});
		PagedList<ContainerService> containerServices = azure.containerServices().listByResourceGroup("Jon_Resources");
	}

	@Test
	public void testGetContainer() throws URISyntaxException, StorageException, InvalidKeyException, IOException {
		String cloudStorageAccountFmt = "DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s";
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.withLogLevel(LogLevel.NONE)
				.authenticate(tokenCredentials);
		Azure azure = authenticated.withSubscription(subscription);
		StorageAccount account = azure.storageAccounts().getById(
				"/subscriptions/" + subscription + "/resourceGroups/jon_resources/providers/Microsoft"
						+ ".Storage/storageAccounts/jonstorageaccount");
		StorageAccountKey key = account.getKeys().get(0);
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(String.format(cloudStorageAccountFmt,
				account.name(), key.value()));
		CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
		CloudBlobContainer containerRef = cloudBlobClient.getContainerReference("jon-cont");
		containerRef.listBlobs().forEach(item -> {
			if (item instanceof CloudBlob) {
				CloudBlob cloudBlob = (CloudBlob) item;
				System.out.println("BLOB Type: " + cloudBlob.getProperties().getBlobType());
				//Path tmpFile = Files.createTempFile("test", "file");
				System.out.println("BLOB " + cloudBlob.getName() + " SIZE " + cloudBlob.getProperties().getLength() +
						" CONTENT TYPE " + cloudBlob.getProperties().getContentType() + " URL " + cloudBlob.getUri());
				/*((CloudBlob) item).downloadToFile(tmpFile.toString());
				System.out.println("File size: " + Files.size(tmpFile));*/
			} else if (item instanceof CloudBlobDirectory) {
				CloudBlobDirectory directory = (CloudBlobDirectory) item;
				System.out.println("DIR " + ((CloudBlobDirectory) item).getPrefix());
			} else {
				System.out.println("OTHER " + item.toString());
			}
		});
	}

	@Test
	public void testUpload() throws URISyntaxException, StorageException, InvalidKeyException, IOException {
		String cloudStorageAccountFmt = "DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s";
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		Azure.Authenticated authenticated = Azure.configure()
				.withLogLevel(LogLevel.NONE)
				.authenticate(tokenCredentials);
		Azure azure = authenticated.withSubscription(subscription);
		StorageAccount account = azure.storageAccounts().getById(
				"/subscriptions/" + subscription + "/resourceGroups/jon_resources/providers/Microsoft"
						+ ".Storage/storageAccounts/jonresourcesdisks834");
		StorageAccountKey key = account.getKeys().get(0);
		CloudStorageAccount storageAccount = CloudStorageAccount.parse(String.format(cloudStorageAccountFmt,
				account.name(), key.value()));
		CloudBlobClient cloudBlobClient = storageAccount.createCloudBlobClient();
		CloudBlobContainer containerRef = cloudBlobClient.getContainerReference("jon-cont");
		containerRef.getDirectoryReference("dir")
				.getPageBlobReference("test")
				.upload(new StringBufferInputStream("test"), "test".length());
	}

	@Test
	public void testMonitor() throws IOException {
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		String token = tokenCredentials.getToken("https://management.azure.com/");

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://management.azure.com/")
				.addConverterFactory(JacksonConverterFactory.create())
				.build();
		AzureClient client = retrofit.create(AzureClient.class);

		System.out.println("TOKEN: " + token);

		Response<Map<String, Object>> response = client.list("Bearer " + token, subscription, "2015-04-01",
				"eventTimestamp ge '2017-06-01T04:36:37.6407898Z'", null)
				.execute();
		System.out.println("CODE: " + response.code());
		if (response.isSuccessful()) {
			System.out.println(new ObjectMapper().writeValueAsString(response.body()));
		} else {
			System.out.println(response.errorBody().string());
		}

	}

	@Test
	public void testGetToken() throws IOException {
		ApplicationTokenCredentials tokenCredentials = new ApplicationTokenCredentials(clientId, tenantId,
				clientSecret, null);
		System.out.println(tokenCredentials.getToken("https://graph.microsoft.com/"));
	}

	@Test
	public void testDownloadBlob() throws URISyntaxException, InvalidKeyException, StorageException, IOException {
		// Retrieve storage account from connection-string.
		Path tmpFile = Files.createTempFile("test-", ".json");
		try {
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString2);
			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			// Retrieve reference to a previously created container.
			CloudBlobContainer container = blobClient.getContainerReference("jon-cont");
			CloudBlockBlob blockBlob = container.getBlockBlobReference("jon-folder/jon_sensitive.docx");
			blockBlob.download(Files.newOutputStream(tmpFile, StandardOpenOption.TRUNCATE_EXISTING));
		} finally {
			Files.deleteIfExists(tmpFile);
		}
	}
}
