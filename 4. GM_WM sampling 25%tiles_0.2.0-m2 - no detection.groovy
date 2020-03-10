// this script works as of 9/11/2019  - to be used on images which only have WM and GM lines drawn. This script will sample the areas and tile the greatest GM ROI

import qupath.lib.roi.LineROI
import qupath.lib.roi.PolygonROI
import qupath.lib.geom.Point2
import qupath.lib.objects.classes.PathClassFactory
import qupath.lib.objects.PathAnnotationObject
import qupath.lib.objects.classes.PathClass
import qupath.lib.gui.scripting.QPEx
import qupath.lib.common.ColorTools

// IMPORTANT STEP! assigns "WM" class to all objects that are NOT lines!
getAnnotationObjects().each{ if (!it.getROI().isLine()){it.setPathClass(getPathClass("WM"))}}
fireHierarchyUpdate()


def imageData = QPEx.getCurrentImageData()
def server = imageData.getServer()

t=server.getAveragedPixelSizeMicrons() //echelle pixel/micron

X1=[]
X2=[]
Y1=[]
Y2=[]
slope=[]
i=0
def double inf=Double.POSITIVE_INFINITY
def double ninf=Double.NEGATIVE_INFINITY
gmSampName=[]
gmHierar=[]
polysizes=[]
//Setting such that the mean total area can deviate by 50um to be considered within the greatest GM ROI - this is equivlent to 0.2mm in HALO protocol
deviation=250
//Sets increments/layer thickness to 250um
step=250
acc=250

for (pathAnnotation in getCurrentHierarchy().getFlattenedObjectList()[1..-1]){
    if (pathAnnotation.getPathClass()==null && pathAnnotation.getROI().getRoiName()=='Line'){
        X1.add(pathAnnotation.getROI().getX1()*t)
        X2.add(pathAnnotation.getROI().getX2()*t)
        Y1.add(pathAnnotation.getROI().getY1()*t)
        Y2.add(pathAnnotation.getROI().getY2()*t)
        slope.add((Y2[i]-Y1[i])/(X2[i]-X1[i]))
        i+=1
    }   
}

0.step X1.size(),2,{
    x11=X1[it]
    y11=Y1[it]
    x21=X2[it]
    y21=Y2[it]
    s1=slope[it]
    d1=Math.sqrt(Math.pow(x21-x11,2)+Math.pow(y21-y11,2))
    
    x12=X1[it+1]
    y12=Y1[it+1]
    x22=X2[it+1]
    y22=Y2[it+1]
    s2=slope[it+1]
    d2=Math.sqrt(Math.pow(x22-x12,2)+Math.pow(y22-y12,2))
    
    testmax1=1e5
    testmax2=1e5
    if (s1==-(double)0){s1=0}
    if (s2==-(double)0){s2=0}
    switch(s1){
        case 0:
            ysop1=0
	    if (x11<x21){
	        xsop1=250
	    }else{
	        xsop1=-250
	    }
	    break
	case inf:
	    xsop1=0
	    ysop1=250
	    break
	case ninf:   
            xsop1=0.
            ysop1=-250
	    break
	default:   
	    if (s1<0){
	        if (x11>=x21 && y11<=y21){
		    for (int i=1;i<step*acc;i++){
		        xs1=-i/acc
			ys1=Math.sqrt(Math.pow(step,2)-Math.pow(xs1,2))
			test1=Math.abs(ys1/(xs1*s1)-1)
			if(test1<testmax1){
			    testmax1=test1
			    xsop1=xs1
			    ysop1=ys1
			}
		    }
		}else if (x11<=x21 && y11>=y21){
		    for (int i=1;i<step*acc;i++){
			xs1=i/acc
			ys1=-Math.sqrt(Math.pow(step,2)-Math.pow(xs1,2))
			test1=Math.abs(ys1/(xs1*s1)-1)
			if(test1<testmax1){
			    testmax1=test1
			    xsop1=xs1
			    ysop1=ys1
			}
		    }
		}
	    }else if (s1>0){
		if (x11<=x21 && y11<=y21){
		    for (int i=1;i<step*acc;i++){
			xs1=i/acc
		        ys1=Math.sqrt(Math.pow(step,2)-Math.pow(xs1,2))
			test1=Math.abs(ys1/(xs1*s1)-1)
			if(test1<testmax1){
			    testmax1=test1
			    xsop1=xs1
			    ysop1=ys1
			}
		    }
		}else if (x11>=x21 && y11>=y21){
		    for (int i=1;i<step*acc;i++){
			xs1=-i/acc
		        ys1=-Math.sqrt(Math.pow(step,2)-Math.pow(xs1,2))
			test1=Math.abs(ys1/(xs1*s1)-1)
			if(test1<testmax1){
			    testmax1=test1
			    xsop1=xs1
			    ysop1=ys1
			}
		    }
		}
	    }
            break
    }
    
    switch(s2){
        case 0:
            ysop2=0
	    if (x12<x22){
	        xsop2=250
	    }else{
    	        xsop2=-250
    	    }
	    break
	case inf:
            xsop2=0
            ysop2=250
	    break
	case ninf:   
            xsop2=0
            ysop2=-250
	    break
	default: 
	    if (s2<0){
	        if (x12>=x22 && y12<=y22){
		    for (int i=1;i<step*acc;i++){
		        xs2=-i/acc
			ys2=Math.sqrt(Math.pow(step,2)-Math.pow(xs2,2))
			test2=Math.abs(ys2/(xs2*s2)-1)
			if(test2<testmax2){
			    testmax2=test2
			    xsop2=xs2
			    ysop2=ys2
			}
		    }
		}else if (x12<=x22 && y12>=y22){
		    for (int i=1;i<step*acc;i++){
			xs2=i/acc
			ys2=-Math.sqrt(Math.pow(step,2)-Math.pow(xs2,2))
			test2=Math.abs(ys2/(xs2*s2)-1)
			if(test2<testmax2){
			    testmax2=test2
			    xsop2=xs2
			    ysop2=ys2
			}
		    }
		}
	    }else if (s2>0){
		if (x12<=x22 && y12<=y22){
		    for (int i=1;i<step*acc;i++){
			xs2=i/acc
		        ys2=Math.sqrt(Math.pow(step,2)-Math.pow(xs2,2))
			test2=Math.abs(ys2/(xs2*s2)-1)
			if(test2<testmax2){
			    testmax2=test2
			    xsop2=xs2
			    ysop2=ys2
			}
		    }
		}else if (x12>=x22 && y11>=y22){
		    for (int i=1;i<step*acc;i++){
			xs2=-i/acc
		        ys2=-Math.sqrt(Math.pow(step,2)-Math.pow(xs2,2))
			test2=Math.abs(ys2/(xs2*s2)-1)
			if(test2<testmax2){
			    testmax2=test2
			    xsop2=xs2
			    ysop2=ys2
			}
		    }
		}
	    }
            break
    }
    classname='GM sampling '+(it/2+1).toString()
    def newPassClass=new PathClass(classname,ColorTools.makeRGB(18,255,87))
    
    while (d1>=2*step && d2>=2*step){ 
        
        x11+=xsop1
        y11+=ysop1
        x12+=xsop2
        y12+=ysop2
                
        d1=Math.sqrt(Math.pow(x21-x11,2)+Math.pow(y21-y11,2))
        d2=Math.sqrt(Math.pow(x22-x12,2)+Math.pow(y22-y12,2))
        
        def roi = new LineROI(x11/t,y11/t,x12/t,y12/t)
        def newLine=new PathAnnotationObject(roi,newPassClass)
        imageData.getHierarchy().addPathObject(newLine,false)
        
    } 
}

for (pathAnnotation in getCurrentHierarchy().getFlattenedObjectList()[1..-1]){
    if (pathAnnotation.getPathClass()!=null && pathAnnotation.getROI().getRoiName()=='Line'){
        if (!gmSampName.contains(pathAnnotation.getPathClass().getName())){
            gmSampName.add(pathAnnotation.getPathClass().getName())
        }
        gmHierar.add(pathAnnotation)
    }
}

for (lines in gmSampName){
    mean=0.0
    N=0
    poly=[]
    for (gmSample in gmHierar){
        if (gmSample.getPathClass().getName()==lines){
            mean+=gmSample.getROI().getLength()*t
            N+=1
        }
    }
    mean/=N
    
    for (gmSample in gmHierar){
        l=Math.abs(gmSample.getROI().getLength()*t-mean)
        if (gmSample.getPathClass().getName()==lines && l<=deviation){
            poly.add(gmSample)
            //imageData.getHierarchy().removeObject(gmSample,false)
        }
    }
    polysizes.add(poly.size()*step)
    def point1 = new Point2(poly[0].getROI().getX1(),poly[0].getROI().getY1())
    def point2 = new Point2(poly[0].getROI().getX2(),poly[0].getROI().getY2())
    def point3 = new Point2(poly[-1].getROI().getX1(),poly[-1].getROI().getY1())
    def point4 = new Point2(poly[-1].getROI().getX2(),poly[-1].getROI().getY2())
    points=[point1,point3,point4,point2,point1]
    
    def roi = new PolygonROI(points)
    def newPoly=new PathAnnotationObject(roi)
    imageData.getHierarchy().addPathObject(newPoly,false)
    
}

P=[]
imax=polysizes.indexOf(Collections.max(polysizes))
def maxPolyClass=getPathClass('Greatest GM Sampling zone',ColorTools.makeRGB(0,94,32))
for (pA in getAnnotationObjects()){
   if (pA.getROI().getRoiName()=='Polygon' && pA.getPathClass() == null){
       P.add(pA)
   }
}

P[imax].setPathClass(maxPolyClass)


// TILE WM AND GM ROI ONLY 
selectObjects { p -> p.getPathClass() == getPathClass("WM") || p.getPathClass()==getPathClass("Greatest GM Sampling zone") }; //Select only the area we want to subdivide into tiles
// Set tile size to 175um
runPlugin('qupath.lib.algorithms.TilerPlugin', '{"tileSizeMicrons": 175.00,  "trimToROI": true,  "makeAnnotations": true,  "removeParentAnnotation": false}');


def tiles = getAnnotationObjects().findAll {it.getDisplayedName().toString().contains('Tile') == true && it.getROI().getRoiName()!='Rectangle'};
removeObjects(tiles, false)
tiles = getAnnotationObjects().findAll {it.getDisplayedName().toString().contains('Tile') == true};
numTiles = tiles.size();

ind2Remove = [];
for (int i=0; i<numTiles; i++){ ind2Remove.add(i) };
Collections.shuffle(ind2Remove);
//Specifies the percentage of tiles to remove, therefore leaving 30% of all tiles created
ind2Remove = ind2Remove[0..(int)(numTiles*0.70-1)];

for (index in ind2Remove){
    removeObject(tiles[index],false);
}



print getProjectEntry().getImageName() + " GM Sampled"