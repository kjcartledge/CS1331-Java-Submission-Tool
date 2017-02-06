# CS1331 Java Submission Tool
This is a program used to submit homework assignments to private student
repositories on GitHub. An executable jar file should be distributed to
students.

## Dependencies

### Java
There are no dependencies on the student's end except for the most recent
version of Java. Testing has revealed that for the jar to work in its current
state, you need to be running Oracle JRE 1.8.0\_101+ on windows or Ubuntu, or
1.8.0\_91+ of the OpenJDK on Ubuntu. If you're Java is not up to date you will
get a really, really scary stacktrace. The executable jar should run cross
platform. The code works entirely through the GitHub web api, so students need
not even install git.

### gradle
gradle is used as the build tool for this project. The default task is to
generate an executable jar that can be distributed to students for purposes of
submitting their homework assignements.

## Features
* Creates a new, private repository if it does not exist.
* Adds a head TA as a collaborator so he/she can clone submissions to a
submissions repo at the due date.
* Supports resubmission so that students can push their files as much as they
please.
* Prints out error and help messages in the event that something goes wrong.

## Known Bugs
* View open issues for known bugs that are in the process of being fixed.
* The method of getting the password from the user involves using some weird
System method, and this just so happens to not work when using terminal
emulators such as GitBash. So, if you're on Windows, you will need to run the
submission jar through cmd or PowerShell.

## Creating a homework submission tool

### 1. Edit gitHubSubmitter.properties
All modifications for new homework assignments should be possible by altering
the properties file, gitHubSubmitter.properties in src/main/resources. At the
time of authorship, the GitHub Web API only supports file transfers of up to
1MB.

* The properties file should be of this format:

```
+----------------------------+-----------------------------+
| submissionTool.properties  |                             |
+----------------------------------------------------------+
| #Sun Jan 29 12:25:37 EST 2017                            |
| prefix=hw                                                |
| className=CSXXXX                                         |
| assignmentName=test                                      |
| fileNames=Test.txt TestDir/Test.txt TestDir/Test2.txt    |
| helpEmails=email@email.com email2@email.com              |
| headTA=headTAUsername                                    |
| hostURL=https\://github.gatech.edu/api/v3                |
+----------------------------------------------------------+
```

* prefix is the prefix for the repository name (The repository will be named as
prefix-assignmentName-studentUsername).

* className is the name of the class that this tool is for.

* assignmentName is the name of the assignment.

* fileNames is a space delimited list of the files to be submitted to the
homework repository.

* helpEmails is a space delimited list of email addresses that will be
presented to students in the event that an unpredicted Exception is thrown
during the program.

* headTA is the username of the person who will be cloning these
repositores at the due date.

* hostURL is the url to the root directory of the gatech github API. The :
after https needs to be escaped.

### 2. Build
Run
```
gradle build
```

* This will produce github-submit.jar in the directory build/lib.
* The produced jar can be distributed to students so they may submit their
assignments.

## Using the jar
Simply place the jar in the directory where the homework files are located and
run

```
java -jar github-submit.jar
```

* This will prompt the user for credentials, and attempt to submit their
homework.
