import matplotlib.pyplot as plt
import numpy as np
import PIL
import tensorflow as tf
import os
import PIL.Image
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.models import Sequential
import pathlib
import glob
import cv2
from time import sleep

img_height = 224
img_width = 224

class_names = ['e_waste_Mouse', 'e_waste_Printer', 'glass_oilbottles', 'glass_winebottles', 'mixed_recyclables_cardboard', 'mixed_recyclables_plastic']
TF_MODEL_FILE_PATH = 'g:/My_Stuff/Level 6 Year 3/Image Processing & Computer Vision/IPCV/ipcv/modelTest.waste.tflite' # The default path to the saved TensorFlow Lite model

#load the model with the interpreter
interpreter = tf.lite.Interpreter(model_path=TF_MODEL_FILE_PATH)
print("Model loaded")

#print the signatures from the converted model to obtain the names of the inputs
print("Signatures:", interpreter.get_signature_list())

classify_lite = interpreter.get_signature_runner('serving_default')
print("pass the signature names to the signature runner")


# Read original image
dir = os.path.abspath('g:/My_Stuff/Level 6 Year 3/Image Processing & Computer Vision/IPCV/ipcv/AssignmentImages')
img = cv2.imread(dir+'/'+'assorted-test5.jpg')
h_img, w_img, _ = img.shape
org_img = img.copy()

# convert image to Grayscale and Black/White
gry = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
cv2.imshow("grey",gry)

#threshold below needs to be adjusted in order to optimise the edges
bw=cv2.threshold(gry, 210, 255, cv2.THRESH_BINARY)[1]
cv2.imshow("bw", bw)

# Use floodfill to identify outer shape of objects
imFlood = bw.copy()
h, w = bw.shape[:2]
mask = np.zeros((h+2, w+2), np.uint8)
cv2.floodFill(imFlood, mask, (0,0), 0)

# Combine flood filled image with original objects
imFlood[np.where(bw==0)]=255

# Invert output colors
imFlood=~imFlood


# Find objects and draw bounding box
cnts, _ = cv2.findContours(imFlood, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
for cnt in cnts:
    peri = cv2.arcLength(cnt, True)
    approx = cv2.approxPolyDP(cnt, 0.02*peri, True)
    x, y, w, h = cv2.boundingRect(approx)
    bob = img[y:y+h, x:x+w]
    if ((w*h > 500) and (w*h < (w_img*h_img*0.9))):
        #1st condition is to just take the large bounding objects
        #2nd condition is to ignore the outside bounding box of image
        
        cv2.rectangle(img, (x, y), (x+w, y+h), (0, 255, 0), 2)
        crop_img = org_img[y:y+h, x:x+w]
        cv2.imshow("crop_image", crop_img)
        #OpenCV reads images in BGR format whereas in keras, it is represented in RGB. To get the OpenCV
        #version to correspond to the order we expect (RGB), simply reverse the channels
        crop_img = cv2.resize(crop_img, (img_height, img_width))
        cropped_segment = crop_img[...,::-1]
                              
        img_array = tf.keras.utils.img_to_array(cropped_segment)
        img_array = tf.expand_dims(img_array, 0) # Create a batch
        print("Image segment loaded")
        
        #run the classification algorithm
        predictions_lite = classify_lite(rescaling_1_input=img_array)['dense_1']
        score_lite = tf.nn.softmax(predictions_lite)
        print(
            "This waste most likely belongs to {} with a {:.2f} percent confidence."
            .format(class_names[np.argmax(score_lite)], 100 * np.max(score_lite)))
        cv2.imshow("Final", img)
        key = cv2.waitKey(0) & 0xFF

        # press 'r' to reset window
        if key == ord("r"):
             wait(0.1)
             cv2.destroyWindow("crop_image")
        # if the 'c' key is pressed, break from the loop
        elif key == ord("c"):
            break

# Save final image
cv2.imshow("Final",img)

