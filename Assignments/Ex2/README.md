## Ex2 OOP_2021 - Directed Graph Algorithm & GUI

## Graph viewer and editor
You can read details about the program below.
Ready to install?
Just download the latest version from the releases :)

# How to run:
### The Program allows you to run with a JSON file as an argument:
```java -jar Ex2.jar <Graph.json>```
### Or with no graph loaded at first, you can load from the GUI using:
```java -jar Ex2.jar```
### Functionallity:

As for now the program have few functions:
* Load a graph from .JSON file
* View the graph
* Find shortest path and it's distance
* Find TSP and view it in graph mode
* Find graph center
* Create new graph
 
Using the "show graph" panel you can:
* Add or remove nodes
* Add or remove edges
* Get details on each node
* Export graph as image
* Save(-as) edited graph
* 
#### Usage in graph mode:
While using in graph mode (in show graph mode, TSP or shortest path) you may click on the buttons below the graph to change the mouse modes:
Add Node allows you to click a spot where a node doens't exist and add a new one there.
Remove node allows you to remove a selected node.
Select for info allows you to select a node to get details such as weight, connected adjs. etc.
To save the changes you made for your graph you can use the save button.

In addition you have few other options, you can use the Export to image button to save the graph as .PNG image format.


# Analysis:
|  Graph Size   | Center Finding  | Shortest Path Time | Shortest Path Distance Time |  TSP Time  | 
| ------------- | --------------- | ------------------ | --------------------------- |  --------- |
|   1000        |                 |                    |                             |            |
|   10000       |                 |                    |                             |            |
|   100000      |                 |                    |                             |            |
|   1000000     |                 |                    |                             |            |


Extreme cases:

** Createa and save is a combo of creating a graph of x size and connecting y edges ( like 'Add nodes + conncet nodes' row but also saves)
*** Graph loaded is the same as saved.

|   Graph Size   | Graph Load Time | Create and save **      |  Save time  |     Add nodes  |       Add nodes  + connect nodes          |
| -------------- | --------------- | ----------------------- | ----------- |   ------------ |-------------------------------------------|
|  100000        | 7111.0 ms ***   |16687.6 ms (500000 edges)| 14017.3 ms  |    138.7 ms    |        4651.4 ms  - 1000000 edges         |
|  1000000 (1M)  |                 |21516.0 ms (300000 edges)|             |    726.5 ms    |         7566.4 ms  - 900000 edges         |
|  2000000 (2M)  |                 |                         |             |    1537.0 ms   |                                           |
