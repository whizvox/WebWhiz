## WebWhiz

Welcome to the WebWhiz Github repository! WebWhiz is a small tool that
allows you to create your own web server without resorting to copy-
pasting code that you found online, paying for a poorly-designed,
inflexible service, or programming the whole thing by yourself.

### Our Goal

The goal of WebWhiz is to allow easy setup, configuration, and
management over your web server by using community-made modules. The
webmaster, you, should have full control over how these modules
function. These modules should be easy to access, install, setup, and
manage. If you want to be a developer, however, you have full access
to the Spark web framework.

### Running

Requirement: [Maven 3.0](https://maven.apache.org/) must be installed.

Make sure you're in the root directory of the project. Execute `mvn
package` to build the project. Next, execute `mvn exec:exec` to run the
newly compiled project. A new directory that's covered by the
.gitignore (`./RUN`) will be created.

//TODO Add a way to easily stop the Spark server. As of now, the only
way to stop it is to terminate the Java process.

### Documentation

Nothing yet. Documentation will be written once certain parts of the
project are finalized and ready for stable releases.

### Special Thanks

Special thanks to the guys of the Minecraft Forge and Bukkit teams for
both the inspiration behind this project and for getting me interested
in programming in the first place.
