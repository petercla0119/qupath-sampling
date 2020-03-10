//Takes all non-lines and modifies the names of those annotations
        // https://groups.google.com/forum/#!topic/qupath-users/rKHqWQHhaEE
// Script only works if there are ONLY LINES AND POLYGONS/AREA/RECTANGLES on images
// Get all annotations with Line ROIs
def annotations = getAnnotationObjects()
def lines = annotations.findAll {it.getROI() instanceof qupath.lib.roi.LineROI}


// Removes lines
annotations.removeAll(lines)
// Assign names to all other annotations
annotations.eachWithIndex {annotation, i -> annotation.setName('WM')}

fireHierarchyUpdate()