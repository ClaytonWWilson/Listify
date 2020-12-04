# Client-Facing Lambdas
Each subfolder (except `target`) is a module.

NOTE: `src` contains the Core module

## Building
- The pom.xml has dependency information for all the Modules combined and all these dependencies are currently built into the Core module.
- Other build configurations are done in IntelliJ with each module having a build definition.
  - Each module relies the (extracted) output of Core.jar being included in its build.
  - Other modules may be included as applicable.
  - Some modules specifically include their own dependencies, but this is not strictly necessary since everything is (inefficiently) bundled into Core.
  
## Inclusion in Git
- Compiled output should never be included in git.
- IntelliJ's build files may not be properly included (or may just be hard to import.) Copying the folder directly between machines works.
