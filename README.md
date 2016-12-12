# UVMS-FileUploader-PLUGIN
File Uploader plugin is designed to serve as yet another messages input mechanism 


If you dont have access to a public nexus/mvn repo with this archetype you can release it locally and create your modules from this archetype

1. In the archetyperoot ( Where this readme is located ), open cmd and type [ mvn clean archetype:create-from-project ]
2. cd target/generated-sources/archetype ( from the archetype root )
3. Type [ mvn install ]

Now your archetype is released to your local .m2 repository (eu.europa.ec.fisheries.uvms.component.component-archetype)

To create a project from archetype do as follows

1. Create a new folder where you want the project to be
2. open cmd and cd to that folder
3. type [ mvn archetype:generate -DarchetypeCatalog=local ]
4. You will be presented with options from your local artifact repo. Chose the one that have the namespace  "eu.europa.ec.fisheries.uvms.component:component-archetype"
5. Define value for property 'groupId': : eu.europa.ec.fisheries.uvms.plugins.flux.uploaders.YOUR_COMPONENT_NAME
6. Define value for property 'artifactId': : YOUR_COMPONENT_NAME
7. Define value for property 'version':  1.0-SNAPSHOT: : 1.0.0-SNAPSHOT
8. Define value for property 'package':  eu.europa.ec.fisheries.uvms.plugins.flux.uploaders.YOUR_COMPONENT_NAME: : eu.europa.ec.fisheries.uvms.plugins.flux.uploader.YOUR_COMPONENT_NAME
9. Select Y and Enter and you're done!
10. Rename the folder YOUR_COMPONENT_NAME to APP.

Open the generated component in your ide and mvn clean build to ensure that the component is correctly configured
Contact GitHub API Training Shop Blog About
© 2016 GitHub, Inc. Terms Privacy Security Status Help
