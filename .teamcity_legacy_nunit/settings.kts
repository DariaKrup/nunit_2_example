import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetMsBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.NUnitStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetMsBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.buildSteps.nunitConsole
import jetbrains.buildServer.configs.kotlin.buildSteps.script
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

version = "2024.12"

project {

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        nuGetInstaller {
            id = "jb_nuget_installer"
            enabled = false
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "TestApp.sln"
            updatePackages = updateParams {
            }
        }
        script {
            id = "simpleRunner"
            scriptContent = "msbuild TestApp.sln"
        }
        dotnetMsBuild {
            id = "dotnet"
            enabled = false
            projects = "TestApp.sln"
            version = DotnetMsBuildStep.MSBuildVersion.CrossPlatform
            args = "-restore -noLogo"
            dockerImagePlatform = DotnetMsBuildStep.ImagePlatform.Windows
        }
        nunit {
            name = "NUnit: deprecated, dll"
            id = "NUnit_deprecated_dll"
            nunitPath = "%teamcity.tool.NUnit.Console.DEFAULT%"
            runtimeVersion = NUnitStep.RuntimeVersion.v4_0
            includeTests = """TestApp\bin\Debug\TestApp.dll"""
            reduceTestFeedback = true
        }
        nunitConsole {
            name = "sdfsd"
            id = "sdfsd"
            nunitPath = "%teamcity.tool.NUnit.Console.DEFAULT%"
            includeTests = "dd"
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
