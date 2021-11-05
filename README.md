# SocialUnrestMAS

This model was built as part of the research on anticipating the spread of social unrest events at the University of Nebraska-Lincoln and Citadel University. This research is funded by the National Geospatial-Intelligence Agency.

In our framework, each region acts as an agent that can communicate with other agents within its predefined neighborhood. The software is built upon the Repast Symphony (RS) 2.6 framework. It is an open-source agent-based modeling and simulation platform. It is an interactive Java-based modeling system designed for use on workstations and small computing clusters. A getting started guide is can be found at: https://repast.github.io/docs/RepastJavaGettingStarted.pdf. It is recommended that the user goes through this guide before starting a new project or modifying existing ones. However, I will still try to cover the necessary details in this document.

## Brief introdction to Repast Symphony
In the Repast Symphony framework, the environment is initialized to extend the Repast class ContextBuilder. In our project, we named this initializing class UnrestBuilder. Thus, we can define our main agents as normal classes. Inside the UnrestBuilder, we create an environment (Context). We then add the agents to this context and return it. In our case, we defined an Event class to define the event-type agents and the Observer class to create an observer agent.

### Scheduling
We can create a method inside the agent class definition and add a scheduler to it. These methods will be executed for each object of that agent class during simulation.
~~~~
@ScheduledMethod(start=0, interval=1, priority = 4)
public void makeAlive() {
    ...
}
~~~~
context.xml
There is a context.xml file in the ./<ProjectName>.rs directory. We need to provide the projection types in this file, and give them unique ids. In our project, we are using the "geography" type context, so we add that to the context.xml file. It is possible to add multiple projection types.
~~~~
<projection id="Geography" type="geography"/>
~~~~
We will be working with Spatio-temporal data, and this means the data has geospatial components (longitude, latitude) and a time element, among other variables. To make calculations in time, I am using the Joda-Time API. The spatial distances will be calculated using the Haversine formula. We also created an Observer object due to restrictions of the Repast Framework to store data (please see class description below for more details).

### Event Class 
Each region(administrative-district) is an agent, an object of the Region class. Each event in general has the following information.

Parameters	Description
Location	longitude and latitude
Region Name
Region Index
SCEIGE variables

The start parameter indicates which tick should the method start executing at, interval parameter should be self-explanatory, higher priority means it gets executed first.

### Distance Class
The Distance class simply implements the distance formula for spatiotemporal, socioeconomic and infrastructural distances. Details regarding the distance formula can be found HERE. Note that we use Haversine formula here to calculate spatial distances in meters or kilometers. Some static values for weights and thresholds are defined during class instantiation. We are assigning equal weights to all distances and even in each sub-distance type calculation. The infrastructure weights are divided by 12 at the end because there are 12 variables in total, so imagine assigning a weight of 1/12 to each infrastructure variable. Changes should be made in distance weighting based on social theories or after extensive data-analysis.

### UnrestBuilder Class
As introduced earlier, the UnrestBuilder implements the Repast Symphony class ContextBuilder, it must contain a build() method that returns a Context object. I have described many constant/static variables in this class.

Variables NEIGHBORHOOD_SIZE, RUN_INDEX are all assigned values through the Repast GUI. The descriptions of these paameters are written in the parameters.xml file in the SocialUnrest.rs folder. For example, the "Death Intensity" and "Recovery Rate" are written as:

<parameters>
    <parameter name="neighborhood" displayName="Neighborhood Size" type="double" 
						defaultValue="0.5" 
						isReadOnly="false" 
						converter="repast.simphony.parameter.StringConverterFactory$DoubleConverter"					
		/>
</parameters>
After giving the input, the parameters are pulled into the context

Parameters params = RunEnvironment.getInstance().getParameters();

<context id="SocialUnrest" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="http://repast.org/scenario/context">
  	<projection id="Geography" type="geography"/>
</context>
context.setId("SocialUnrest");
GeographyParameters<Object> geoparams = new GeographyParameters<Object>();
GeographyFactory factory = GeographyFactoryFinder.createGeographyFactory(null);
Geography<Object> geography = factory.createGeography("Geography", context, geoparams);
GeometryFactory fac = new GeometryFactory();
In our project we import preprocessed datasets and create events out of it. This is also done in the UnrestBuilder using the readData(String filePath) method. The file is then read and all the events in the given file is added to the context as agents. The agents all have the alive property set to false and markSize set to 1 in the beginning.

### Run a simple simulation
1. Install the Repast Symphony software for Eclipse IDE. Installation Docs Here.
2. Open project SocialUnrest with Eclipse IDE.
3. Press the drop-down menu in the run button and choose "SocialUnrest Model", this will open up the RepastMain display.
4. The project includes the xml files for the simulation setup. If not found, setup your own as follows:
  * Right-click Data Loaders, click on Set Data Loader.
  * Choose Custom ContextBuilder Implementation, press Next and select the UnrestBuilder class (or your own class that you might have built).
5. Click the initialize button, this will load the data from the filePath, given in the UnrestBuilder.
6. Change different simuation parameters and click Play. The change in the intensties is shown as change in the color of the events. If the color styling is not working, create the display:
  * Right-click Displays, click Add Display.
  * Choose the projection type. In our case, it is GIS. You can add multiple projections types. Click Next.
  * In the Agent Selection panel, Choose the agent classes you want to display. (In our case, it's the Event class only). Click Next.
  * In the Agent Style panel, you can assign color themes and shapes to the agents. Click on the Edit... button, this will further open the styling control panel.
  * Click on the drop-down menu next to the Mark Size option and select markSize. Then click on the Range Style tab, set Attribute to intensity, change Classes to 8.
You can add GIS layers from the display panel. Again, reading the getting-started document is reccomended.

## How to run this model in batch mode on a HPC server
Batch running this model on local or even on a remote machine is pretty straight-forward and you can just refer to the online guide to do so. But I struggled for a while to get this running on HCC servers because of duo authentication and the fact that it was still my computer sending instructions to the remote server. I wanted to just run everything on the HCC and not use my pc at all. I considered converting the project to C++ and using another version of Repast that is especially designed for high performance computing. But I discovered that several packages required were missing or there were version conflicts with the HCC. So we will continue to use the Java version, and do the following.

- set up the batch parameters.
- click on the Create Model Archive for Batch Runs button on the top (shown in figure below). This will create a complete_model.jar file. 
-  create a file local_batch_run.properties and put the info below. The information should mostly be self-explanatory, you can change the instance count and other directories. I put this file in the same folder as the archive jar far that I get from the 2nd step.
~~~~
unrolled.batch.parameter.file=./unrolledParamFile.txt
scenario.directory=./scenario.rs
working.directory=./
repast.lib.directory=./lib
instance.count = 4
batch.parameter.file = ./scenario.rs/batch_params.xml
vm.arguments = -Xmx512M
~~~~
now you can create and run a .slurm file such as repastJob.slurm
~~~~
#!/bin/sh
#SBATCH --time=10:00:00          # Run time in hh:mm:ss
#SBATCH --ntasks-per-node=4
#SBATCH --nodes=4
#SBATCH --mem-per-cpu=16000       # Maximum memory required per CPU (in megabytes)
#SBATCH --job-name=RepastJob
#SBATCH --error=./batch.%J.err
#SBATCH --output=./batch.%J.out

module load java
jar xf complete_model.jar
java -cp "./lib/*" repast.simphony.batch.LocalDriver local_batch_run.properties
~~~~

Simulations can be done for three different admin-states of India - the process of changing 
from one to another admin-state is not automated therfore following things should be considered while changing
1. Change default path in the CsvParser.java 
2. Change number of regions in UnrestBuilder.java
3. Change number of regions in Determinestate.java
4. Change districts name string and latlong path in Region.java
5. Change latlong path and max distance values in Neighbor.java
