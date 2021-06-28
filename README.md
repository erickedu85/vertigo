# VERTIGo: a Visual Platform for Querying and Exploring Large Multilayer Networks

Erick Cuenca, Arnaud Sallaberry, Dino Ienco, and Pascal Poncelet

![VERTIGo](teaser/teaser.png "VERTIGo")

## Resume
Many real world data can be modeled by a graph with a set of nodes interconnected to each other by multiple relationships. Such a rich graph is called multilayer graph or network. Providing useful visualization tools to support the query process for such graphs is challenging. Although many approaches have addressed the visual query construction, few efforts have been done to provide a contextualized exploration of query results and suggestion strategies to refine the original query. This is due to several issues such as i) the size of the graphs ii) the large number of retrieved results and iii) the way they can be organized to facilitate their exploration. In this paper, we present VERTIGo, a novel visual platform to query, explore and support the analysis of large multilayer graphs. VERTIGo provides coordinated views to navigate and explore the large set of retrieved results at different granularity levels. In addition, the proposed system supports the refinement of the query by visual suggestions to guide the user through the exploration process. Two examples and a user study demonstrate how VERTIGo can be used to perform visual analysis (query, exploration, and suggestion) on real world multilayer networks

**BibTex**
```
@article{cuenca2021vertigo,
  title={VERTIGo: a Visual Platform for Querying and Exploring Large Multilayer Networks},
  author={Cuenca, Erick and Sallaberry, Arnaud and Ienco, Dino and Poncelet, Pascal},
  journal={IEEE Transactions on Visualization and Computer Graphics},
  year={2021},
  publisher={IEEE},
  doi={10.1109/TVCG.2021.3067820}
  note={To appear}
}
```
## Video demonstration
A demonstration video is available at: https://youtu.be/0aC6-8pW66Y

## Keywords
Visual Querying System, Visual Pattern Suggestion, Multilayer Networks

## Programming language
Java

## Getting the runnable jar
The runnable jar is hosted at [runnable_jar](https://github.com/erickedu85/vertigo/runnable_jar/)

**Requeriments:**
* Java installed on your machine

**Run the jar file:**

	$ git clone https://github.com/erickedu85/vertigo/runnable_jar/
	$ cd runnable_jar
	$ java -jar vertigo.jar (or simple double click on it)

## Getting the source code
The source code is hosted at [source_code](https://github.com/erickedu85/vertigo/source_code/)

**Requirements:**
* Java Development Kit (JDK) or Java Runtime Machine (JRE) 1.8 on your machine
* Eclipse IDE 2021
* Maven (It is included in the Eclipse IDE)

**Build the code:**
* Import the source code into the Eclipse IDE workspace
	* Import projects > Maven > Existing Maven Projects
* Add the JDK to the Workspace Default JRE
	* Rigth-click your project > properties > select Java Build Path > JRE System Library > select Workspace Default JRE > click Installed JREs > select JDK path
	* _(optional)_ Right-click your project, then Maven > Update

**Run the code:**
* Right-click on the src/main/java/app/gui/main/Application.java file, then Run As > Java Application

## Licence
[Attribution-NonCommercial-ShareAlike 4.0 International](https://creativecommons.org/licenses/by-nc-sa/4.0/ "Attribution-NonCommercial-ShareAlike 4.0 International")