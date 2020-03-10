// Author: Svidro - //https://groups.google.com/forum/#!topic/qupath-users/WlxDfgjqrCU
// Date: 08/08/2019
	// Successfully runs in QuPath v0.2.0-m2
	
	/* Description: This script subtracts annotations of a specified class
	* Users do NOT need to change/modify any lines in this script.
	*/


import qupath.lib.roi.*
import qupath.lib.objects.*

classToSubtract = "Vessels or artifact"
    
def topLevel = getObjects{return it.getLevel()==1 && it.isAnnotation()}

for (parent in topLevel){

    def total = []
    def polygons = []
    subtractions = parent.getChildObjects().findAll{it.isAnnotation() && it.getPathClass() == getPathClass(classToSubtract)}
    for (subtractyBit in subtractions){
        if (subtractyBit instanceof AreaROI){
           subtractionROIs = PathROIToolsAwt.splitAreaToPolygons(subtractyBit.getROI())
           total.addAll(subtractionROIs[1])
        } else {total.addAll(subtractyBit.getROI())}              
                
    }     
    if (parent instanceof AreaROI){
        polygons = PathROIToolsAwt.splitAreaToPolygons(parent.getROI())
        total.addAll(polygons[0])
    } else { polygons[1] = parent.getROI()}

            
    def newPolygons = polygons[1].collect {
    updated = it
    for (hole in total)
         updated = PathROIToolsAwt.combineROIs(updated, hole, PathROIToolsAwt.CombineOp.SUBTRACT)
         return updated
    }
                // Remove original annotation, add new ones
    annotations = newPolygons.collect {new PathAnnotationObject(updated, parent.getPathClass())}


    addObjects(annotations)

    removeObjects(subtractions, true)
    removeObject(parent, true)
}
