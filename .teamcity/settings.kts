import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudImage
import jetbrains.buildServer.configs.kotlin.amazonEC2CloudProfile
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetMsBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.NUnitStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetMsBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.03"

project {

    buildType(Build)

    params {
        password("password", "credentialsJSON:bf8a6afe-cfb9-485f-b640-33b16dbaaaf5")
    }

    features {
        amazonEC2CloudImage {
            id = "PROJECT_EXT_53"
            profileId = "amazon-14"
            agentPoolId = "-2"
            name = "Windows (.Net) Image"
            vpcSubnetId = "subnet-0c23f411b0800b216"
            keyPairName = "daria.krupkina"
            instanceType = "t2.medium"
            securityGroups = listOf("sg-072d8bfa0626ea2a6")
            instanceTags = mapOf(
                "Owner" to "daria.krupkina@jetbrains.com"
            )
            source = Source("ami-02761680ebacdaa95")
        }
        dockerRegistry {
            id = "PROJECT_EXT_57"
            name = "Docker Registry (Local)"
            userName = "dariakrup"
            password = "credentialsJSON:380671e3-5d7b-4f1f-9c32-f11d40eedd27"
        }
        amazonEC2CloudProfile {
            id = "amazon-14"
            name = "AWS EC2 Cloud Profile"
            serverURL = "http://10.128.93.57:8211/"
            terminateIdleMinutes = 30
            region = AmazonEC2CloudProfile.Regions.EU_WEST_DUBLIN
            authType = accessKey {
                keyId = "credentialsJSON:5956c87f-9f8f-4ec4-8c89-2874bed09e35"
                secretKey = "credentialsJSON:b8284969-a0a5-4b40-8e3c-5e024c68c682"
            }
        }
    }
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        nuGetInstaller {
            id = "jb_nuget_installer"
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "TestApp.sln"
            updatePackages = updateParams {
            }
        }
        dotnetMsBuild {
            id = "dotnet"
            projects = "TestApp.sln"
            version = DotnetMsBuildStep.MSBuildVersion.V17
            args = "-restore -noLogo"
        }
        nunit {
            name = "Deprecated NUnit tests"
            id = "Deprecated_NUnit_tests"
            nunitVersion = NUnitStep.NUnitVersion.NUnit_2_6_4
            nunitPath = "%teamcity.tool.NUnit.Console.3.15.0%"
            runtimeVersion = NUnitStep.RuntimeVersion.v4_0
            includeTests = """TestApp\bin\Debug\Test*.dll"""
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})
