//Perform the positive pixel detection on the tiles, but not the larger annotation
setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "DAB AT8 HALO Validation ", "Stain 1" : "Hematoxylin", "Values 1" : "0.625, 0.733, 0.269   ", "Stain 2" : "DAB", "Values 2" : "0.373, 0.714, 0.592 ", "Background" : " 255 255 255 "}');

//Select tiles only
tiles = getAnnotationObjects().findAll {it.getDisplayedName().toString().contains('Tile') == true}
getCurrentHierarchy().getSelectionModel().setSelectedObjects(tiles, null)

// THIS LINE CHANGES BASED ON THE STAIN 
runPlugin('qupath.imagej.detect.tissue.PositivePixelCounterIJ', '{"downsampleFactor": 2,  "gaussianSigmaMicrons": 1.0,  "thresholdStain1": 0.085,  "thresholdStain2": 0.146,  "addSummaryMeasurements": true,  "clearParentMeasurements": true,  "appendDetectionParameters": true,  "legacyMeasurements0.1.2": false}');


print 'Analysis for ' + getProjectEntry().getImageName() + ' Complete!' 
