README for XML configuration file: 

Tags:
<embryo>

	<image> points to an 8bit or 16bit image file (the first of a set). If this points to 8bit and they don't exist, it will search for 16bit iamges
	<nuclei> points to a zip with nuclei files
	<end> the last timepoint AceTree will show (note that all timepoints will be loaded, this is just the last that will be displayed)
	<resolution> contains x,y,z resolution (xy grouped together), and plane end
	<exprCorr> the gene expression channel in AceTree files that controls the type of background color for the interface ("blot" is defauly correction)
	<useStack> (OPTIONAL) indicates 8bit (0) or 16bit (1) images. If not listed, default is 0.
	<Split> (OPTIONAL) indicates that in the presence of 16bit images, split them into two channels (1) or don't split them (0). The default behavior is to split
	<polar> (May be obsolete) AceTree's attempt to filter out early appearing cells which are actually polar bodies

</embryo>


An example:
<?xml version='1.0' encoding='utf-8'?>
<embryo>
<image file="20140407_JIM113_SiO60/image/tif/20140407_JIM113_SiO-0.15_1_s1-t001-p01.tif"/>
<nuclei file="./20140407_JIM113_SiO-0.15_1_s1_emb_mirroredcorrect_edited.zip"/>
<end index="475"/>
<resolution xyRes="0.15" zRes="0.75" planeEnd="60"/> <exprCorr type="blot"/> 
<useStack type="1" /> <Split SplitMode="0"/>
<polar size="15"/>
</embryo>
