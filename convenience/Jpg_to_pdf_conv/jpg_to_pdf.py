import sys
from reportlab.lib.pagesizes import A4
from reportlab.platypus import SimpleDocTemplate, flowables

if len(sys.argv) != 3:
    print ("usage: jpg_to_pdf <jpg_in_file> <pdf_out_file>")
    exit(1)

#the images
image_jpg = sys.argv[1]
image_pdf = sys.argv[2]

#sets the witdth and height of image canvas to A4 size
width, height = A4

def drawPageFrame(canv, doc):
    canv.saveState()
    #draws image canvas at x=0, y=0 coords, fitting image north west of given
    #box
    canv.drawImage(image_jpg, 0, 0, width, height, preserveAspectRatio=True,
                   anchor='nw')
    canv.restoreState() # reset
    
#empty page list
page = []

#add states to the page list paragraph by paragraph
#state by state
page.append(flowables.Macro('canvas.saveState()'))
page.append(flowables.Macro('canvas.restoreState()'))

#setting up the image
doc = SimpleDocTemplate(image_pdf, pagesize=(width, height))

#draw the frame on the first page of page[]
doc.build(page, onFirstPage = drawPageFrame) 


    
