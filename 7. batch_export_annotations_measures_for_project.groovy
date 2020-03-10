// Author: Claire S. Peterson
// Date: 08/08/2019
	// Successfully runs in QuPath v0.2.0-m2
	
	/* Description: EXPORTS ANNOTATION AND DETECTION MEASURMENTS
	 *This is a QuPath Script which exports the 
	 *annotations and detections and places them 
	 *into a NEW subdirectory called "annotation results" 
	 * NOTE: Users should modify line 14 to name the subdirectory.
	*/
 
 
def name = getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'STAIN PARAMETERS LASTNAME')
mkdirs(path)
path = buildFilePath(path, name)
saveAnnotationMeasurements(path)
print 'Results exported to ' + path


