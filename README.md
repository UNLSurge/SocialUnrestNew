# SocialUnrestMAS

## TO RUN 

Press the drop-down menu in the run button and choose "UnrestScoical Model", 
this will open up the RepastMain display.

NOTE: This should be already present by default but may not be exported so:
If the data loader already has UnrestBuilder class selected than skip:

Right-click Data Loaders, click on Set Data Loader.
Choose Custom ContextBuilder Implementation, press Next and select the UnrestBuilder
class.

Click the initialize button, this will load the data.

If the display is not present:
If the display section already has some display you can skip or you can try creating new display;
To Create the display:

Right-click Displays, click Add Display.
Choose the projection type. In our case, it is GIS

In the Agent Selection panel, Choose the agent classes you want to display.
(In our case, it's the Region class only). Click Next.

In the Agent Style panel, you can assign color themes and shapes to the agents.
Click on the Edit... button, this will further open the styling control panel.

Choose the attribute next state.

Simulations can be done for three different admin-states of India - the process of changing 
from one to another admin-state is not automated therfore following things should be considered while changing
	1. Change default path in the CsvParser.java 
	2. Change number of regions in UnrestBuilder.java
	3. Change number of regions in Determinestate.java
	4. Change districts name string and latlong path in Region.java
	5. Change latlong path and max distance values in Neighbor.java
