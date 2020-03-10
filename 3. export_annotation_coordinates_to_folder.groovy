// Author: Claire S. Peterson
// Date: 08/08/2019
	// Successfully runs in QuPath v0.2.0-m2
	
	/* Description: This script exports annotation coordinates into newly created folder named, "annotations".
	* Users do NOT need to change/modify any lines in this script. sets the name of the generated annotation and generates a text file where *coordinates of the annotation will be stored
	*/

def name = 'annotations-' + getProjectEntry().getImageName() + '.txt'
def path = buildFilePath(PROJECT_BASE_DIR, 'annotations')
def annotations = getAnnotationObjects()
mkdirs(path)
path = buildFilePath(path, name)
new File(path).withObjectOutputStream {
    it.writeObject(annotations)
}
print 'Done!' + ' - Annotations exported to ' + path
