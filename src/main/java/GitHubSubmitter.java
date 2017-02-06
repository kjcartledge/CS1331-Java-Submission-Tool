import java.net.UnknownHostException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.Base64;

/**
 * GitHub submitter for CS1331 homework assignments. See the README.md for
 * information on tailoring the submission tool for specific classes, homeworks,
 * or quizzes.
 *
 * @author Jim Harris (jamesharris9456@gmail.com)
 * @version 1.0 1/29/17
 */
public class GitHubSubmitter {
    private HttpsService https;
    private String repositoryName;
    private String username;
    private String headTA;
    private String[] fileNames;

    /**
     * Public constructor.
     *
     * @param propertiesFile the properties file.
     * @param hostURL the url to the github web API.
     * @param repositoryName what to name the repository.
     * @param headTA the GT ID of the headTA cloning the submissions.
     * @param username the username of the student.
     * @param password the password of the student.
     * @param fileNames the files for this assignment.
     */
    public GitHubSubmitter(String hostURL, String repositoryName,
        String headTA, String username, String password, String... fileNames) {

        this.https = new GitHubHttpsService(hostURL, username, password);
        this.repositoryName = repositoryName;
        this.username = username;
        this.headTA = headTA;
        this.fileNames = fileNames;
    }

    /**
     * @return the name of the repository.
     */
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * Attempts to create the user's repository for the homework assignment.
     * The repository will be made private. In the event that the repository
     * was created new, collaborators will be added.
     *
     * @return false in the event of authentication failure.
     * @throws IOException if there was a connection error, the repository
     * already exists, or there was an authentication error.
     */
    public boolean createRepository() throws IOException {
        https.post("/user/repos",
            new String[][]{
                {"name", this.repositoryName},
                {"private", "true"}
            });
        return true;
    }

    /**
     * Attempts to add a collaborator to the user's repository. Assumes the
     * repository already exists.
     *
     * @return false in the event of authentication failure or the repository
     * does not exist.
     * @throws IOException if there was a connection error or there was an
     * authentication error.
     */
    public boolean addCollaborators() throws IOException {
        https.put(String.format("/repos/%s/%s/collaborators/%s",
            this.username, this.repositoryName, this.headTA),
            new String[][]{
                {"permission", "push"},
            });
        return true;
    }

    /**
     * Attempts to create a new file in the Git repository.
     *
     * @param fileName the name of the file to create.
     * @param encodedContent the new file contents encoded in base 64.
     * @throws IOException if there was a connection issue, an authentication
     * issue, or the file exists.
     */
    public void createFile(String fileName, String encodedContent)
        throws IOException {
        https.put(String.format("/repos/%s/%s/contents/%s",
            this.username, this.repositoryName, fileName),
            new String[][]{
                {"path", fileName},
                {"message", "Initial add"},
                {"content", encodedContent}
            });
    }

    /**
     * Attempts to update an existing file in the Git repository.
     *
     * @param fileName the name of the file to update.
     * @param encodedContent the new file contents encoded in base 64.
     * @throws IOException if there was a connection issue, an authentication
     * issue, or the file does not exist.
     */
    public void updateFile(String fileName, String encodedContent)
        throws IOException {
        String response = https.get(String.format("/repos/%s/%s/contents/%s",
            this.username, this.repositoryName, fileName));
        int start = response.indexOf("\"sha\"") + 7;
        int end = response.indexOf("\"", start + 1);
        String sha = response.substring(start, end);
        https.put(String.format("/repos/%s/%s/contents/%s",
            this.username, this.repositoryName, fileName),
            new String[][]{
                {"path", fileName},
                {"message", "Updating"},
                {"content", encodedContent},
                {"sha", sha}
            });
    }

    /**
     * Attempts to submit files to the repository. Assumes the repository
     * already exists.
     *
     * @return false in the event of authentication failure or not all files
     * were able to be submitted.
     */
    public boolean addFiles() throws IOException {
        for (int i = 0; i < fileNames.length; i++) {
            Scanner file = new Scanner(new File(fileNames[i]));
            String fileData = "";
            while (file.hasNextLine()) {
                fileData += file.nextLine() + "\n";
            }
            String encodedContent = new String(Base64.getEncoder()
                .encode(fileData.getBytes()));
            try {
                createFile(fileNames[i], encodedContent);
            } catch (IOException e) {
                if (e.getMessage().contains("422")) {
                    updateFile(fileNames[i], encodedContent);
                } else {
                    throw e;
                }
            }
        }
        return true;
    }
}
